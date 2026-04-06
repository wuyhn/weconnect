package com.weconnect.backend.service;

import com.weconnect.backend.dto.PostRequest;
import com.weconnect.backend.dto.PostResponse;
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

    public PostService(PostRepository postRepository,
                       PostMemberRepository postMemberRepository,
                       UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postMemberRepository = postMemberRepository;
        this.userRepository = userRepository;
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
        return "Đã từ chối thành viên.";
    }

    // Lấy danh sách thành viên
    public List<PostMember> getMembers(Long postId) {
        return postMemberRepository.findByPostId(postId);
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
