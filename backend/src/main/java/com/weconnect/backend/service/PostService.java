package com.weconnect.backend.service;

import com.weconnect.backend.dto.PostRequest;
import com.weconnect.backend.dto.PostResponse;
import com.weconnect.backend.entity.Notification;
import com.weconnect.backend.entity.Post;
import com.weconnect.backend.entity.PostMember;
import com.weconnect.backend.entity.User;
import com.weconnect.backend.repository.PostMemberRepository;
import com.weconnect.backend.repository.PostRepository;
import com.weconnect.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMemberRepository postMemberRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ChatService chatService;

    public PostService(PostRepository postRepository,
                       PostMemberRepository postMemberRepository,
                       UserRepository userRepository,
                       NotificationService notificationService,
                       ChatService chatService) {
        this.postRepository = postRepository;
        this.postMemberRepository = postMemberRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.chatService = chatService;
    }

    // Lấy danh sách bài đăng active
    public List<PostResponse> getActivePosts(Long currentUserId) {
        List<Post> posts = postRepository.findByArchivedFalseAndEndTimeAfterOrderByCreatedAtDesc(LocalDateTime.now());
        return toResponseList(posts, currentUserId);
    }

    // Lấy chi tiết bài đăng
    public PostResponse getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));
        return toResponse(post, currentUserId);
    }

    // Tạo bài đăng mới
    public PostResponse createPost(Long authorId, PostRequest request) {
        Post post = Post.builder()
                .authorId(authorId)
                .content(request.getContent())
                .interestTag(request.getInterestTag())
                .location(request.getLocation())
                .imageUrl(request.getImageUrl())
                .maxMembers(request.getMaxMembers() > 0 ? request.getMaxMembers() : 10)
                .startTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now())
                .endTime(request.getEndTime() != null ? request.getEndTime() : LocalDateTime.now().plusDays(1))
                .archived(false)
                .build();

        post = postRepository.save(post);

        // Tự động tạo phòng chat cho hoạt động
        String chatTitle = (request.getInterestTag() != null && !request.getInterestTag().isEmpty())
                ? request.getInterestTag() : "Hoạt động";
        User author = userRepository.findById(authorId).orElse(null);
        if (author != null) {
            chatTitle = chatTitle + " - " + author.getFullName();
        }
        chatService.createActivityChatRoom(post.getId(), authorId, chatTitle);

        return toResponse(post, authorId);
    }

    // Sửa bài đăng
    public PostResponse updatePost(Long postId, Long userId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));

        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa bài đăng này.");
        }

        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getInterestTag() != null) post.setInterestTag(request.getInterestTag());
        if (request.getLocation() != null) post.setLocation(request.getLocation());
        if (request.getImageUrl() != null) post.setImageUrl(request.getImageUrl());
        if (request.getMaxMembers() > 0) post.setMaxMembers(request.getMaxMembers());
        if (request.getStartTime() != null) post.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) post.setEndTime(request.getEndTime());

        post = postRepository.save(post);
        return toResponse(post, userId);
    }

    // Xóa bài đăng
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));

        if (!post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa bài đăng này.");
        }

        postMemberRepository.findByPostId(postId).forEach(postMemberRepository::delete);
        postRepository.delete(post);
    }

    // Xin tham gia hoạt động
    public String joinPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));

        if (post.getAuthorId().equals(userId)) {
            throw new RuntimeException("Bạn là chủ bài đăng, không cần tham gia.");
        }

        if (postMemberRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("Bạn đã gửi yêu cầu tham gia rồi.");
        }

        int currentMembers = postMemberRepository.countByPostIdAndStatus(postId, PostMember.Status.APPROVED);
        if (currentMembers >= post.getMaxMembers()) {
            throw new RuntimeException("Hoạt động đã đủ thành viên.");
        }

        PostMember member = PostMember.builder()
                .postId(postId)
                .userId(userId)
                .status(PostMember.Status.PENDING)
                .build();

        postMemberRepository.save(member);

        // Tạo thông báo cho chủ bài đăng
        User joiner = userRepository.findById(userId).orElse(null);
        String joinerName = joiner != null ? joiner.getFullName() : "Người dùng";
        String postTitle = post.getContent();
        if (postTitle != null && postTitle.length() > 50) {
            postTitle = postTitle.substring(0, 50) + "...";
        }
        String message = joinerName + " muốn tham gia kèo \"" + postTitle + "\" của bạn.";
        notificationService.createNotification(
                post.getAuthorId(),
                Notification.NotificationType.JOIN_REQUEST,
                message,
                joinerName,
                postId,
                userId
        );

        return "Đã gửi yêu cầu tham gia!";
    }

    // Duyệt thành viên
    public String approveMember(Long postId, Long memberId, Long ownerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));

        if (!post.getAuthorId().equals(ownerId)) {
            throw new RuntimeException("Bạn không có quyền duyệt thành viên.");
        }

        PostMember member = postMemberRepository.findByPostIdAndUserId(postId, memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tham gia."));

        member.setStatus(PostMember.Status.APPROVED);
        postMemberRepository.save(member);

        // Tạo thông báo cho người được duyệt
        User owner = userRepository.findById(ownerId).orElse(null);
        String ownerName = owner != null ? owner.getFullName() : "Chủ bài đăng";
        String postTitle = post.getContent();
        if (postTitle != null && postTitle.length() > 50) {
            postTitle = postTitle.substring(0, 50) + "...";
        }
        String message = "Chúc mừng! " + ownerName + " đã chấp nhận yêu cầu của bạn cho kèo \"" + postTitle + "\".";
        notificationService.createNotification(
                memberId,
                Notification.NotificationType.JOIN_APPROVED,
                message,
                ownerName,
                postId,
                ownerId
        );

        // Thêm user vào phòng chat hoạt động
    chatService.addMemberToActivityRoom(postId, memberId);

    return "Đã duyệt thành viên!";
    }

    // Từ chối thành viên
    public String rejectMember(Long postId, Long memberId, Long ownerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài đăng."));

        if (!post.getAuthorId().equals(ownerId)) {
            throw new RuntimeException("Bạn không có quyền từ chối thành viên.");
        }

        PostMember member = postMemberRepository.findByPostIdAndUserId(postId, memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tham gia."));

        member.setStatus(PostMember.Status.REJECTED);
        postMemberRepository.save(member);

        // Tạo thông báo cho người bị từ chối
        User owner = userRepository.findById(ownerId).orElse(null);
        String ownerName = owner != null ? owner.getFullName() : "Chủ bài đăng";
        String postTitle = post.getContent();
        if (postTitle != null && postTitle.length() > 50) {
            postTitle = postTitle.substring(0, 50) + "...";
        }
        String message = ownerName + " đã từ chối yêu cầu tham gia kèo \"" + postTitle + "\" của bạn.";
        notificationService.createNotification(
                memberId,
                Notification.NotificationType.JOIN_REJECTED,
                message,
                ownerName,
                postId,
                ownerId
        );

        return "Đã từ chối thành viên.";
    }

    // Lấy danh sách thành viên (enriched with user info)
    public List<java.util.Map<String, Object>> getMembers(Long postId) {
        List<PostMember> members = postMemberRepository.findByPostId(postId);
        List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        for (PostMember pm : members) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("userId", pm.getUserId());
            map.put("status", pm.getStatus().name());
            User user = userRepository.findById(pm.getUserId()).orElse(null);
            if (user != null) {
                map.put("fullName", user.getFullName());
                map.put("username", user.getEmail());
            } else {
                map.put("fullName", "Người dùng #" + pm.getUserId());
                map.put("username", "");
            }
            result.add(map);
        }
        return result;
    }

    // Lấy danh sách thành viên đang chờ duyệt
    public List<PostMember> getPendingMembers(Long postId) {
        return postMemberRepository.findByPostIdAndStatus(postId, PostMember.Status.PENDING);
    }

    // Bài đăng của user (active)
    public List<PostResponse> getUserActivePosts(Long userId, Long currentUserId) {
        List<Post> posts = postRepository.findByAuthorIdAndArchivedFalseAndEndTimeAfterOrderByCreatedAtDesc(userId, LocalDateTime.now());
        return toResponseList(posts, currentUserId);
    }

    // Bài đăng đã lưu trữ
    public List<PostResponse> getUserArchivedPosts(Long userId, Long currentUserId) {
        List<Post> posts = postRepository.findByAuthorIdOrderByCreatedAtDesc(userId);
        List<Post> archived = new ArrayList<>();
        for (Post post : posts) {
            if (post.isArchived() || post.isExpired()) {
                archived.add(post);
            }
        }
        return toResponseList(archived, currentUserId);
    }

    // Tìm kiếm bài đăng
    public List<PostResponse> searchPosts(String query, Long currentUserId) {
        List<Post> posts = postRepository.findByContentContainingIgnoreCaseOrInterestTagContainingIgnoreCase(query, query);
        return toResponseList(posts, currentUserId);
    }

    // --- Helper methods ---
    private PostResponse toResponse(Post post, Long currentUserId) {
        int memberCount = postMemberRepository.countByPostIdAndStatus(post.getId(), PostMember.Status.APPROVED);
        memberCount += 1; // +1 tính cả người tổ chức (author)
        boolean joined = false;
        boolean pending = false;

        if (currentUserId != null && !post.getAuthorId().equals(currentUserId)) {
            PostMember pm = postMemberRepository.findByPostIdAndUserId(post.getId(), currentUserId).orElse(null);
            if (pm != null) {
                joined = pm.getStatus() == PostMember.Status.APPROVED;
                pending = pm.getStatus() == PostMember.Status.PENDING;
            }
        }

        String authorName = "";
        User author = userRepository.findById(post.getAuthorId()).orElse(null);
        if (author != null) {
            authorName = author.getFullName();
        }

        return PostResponse.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .authorName(authorName)
                .content(post.getContent())
                .interestTag(post.getInterestTag())
                .location(post.getLocation())
                .imageUrl(post.getImageUrl())
                .maxMembers(post.getMaxMembers())
                .memberCount(memberCount)
                .joined(joined)
                .pendingApproval(pending)
                .archived(post.isArchived())
                .expired(post.isExpired())
                .startTime(post.getStartTime())
                .endTime(post.getEndTime())
                .createdAt(post.getCreatedAt())
                .build();
    }

    private List<PostResponse> toResponseList(List<Post> posts, Long currentUserId) {
        List<PostResponse> responses = new ArrayList<>();
        for (Post post : posts) {
            responses.add(toResponse(post, currentUserId));
        }
        return responses;
    }
}
