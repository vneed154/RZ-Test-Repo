package com.rz.board.service;

import com.rz.board.domain.Member;
import com.rz.board.domain.Post;
import com.rz.board.dto.request.PostCreateRequest;
import com.rz.board.dto.request.PostUpdateRequest;
import com.rz.board.dto.response.CommentResponse;
import com.rz.board.dto.response.PostDetailResponse;
import com.rz.board.dto.response.PostResponse;
import com.rz.board.exception.BusinessException;
import com.rz.board.exception.ErrorCode;
import com.rz.board.repository.CommentRepository;
import com.rz.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    @Transactional
    public PostResponse create(PostCreateRequest request) {
        Member member = memberService.getMember(request.memberId());

        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .member(member)
                .build();

        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }

    public Page<PostResponse> findAll(Pageable pageable) {
        return postRepository.findAllWithMember(pageable)
                .map(PostResponse::from);
    }

    @Transactional
    public PostDetailResponse findByIdAndIncreaseViewCount(Long postId) {
        Post post = getPostWithMember(postId);
        post.increaseViewCount();

        List<CommentResponse> comments = commentRepository.findAllByPostIdWithMember(postId).stream()
                .map(CommentResponse::from)
                .toList();

        return PostDetailResponse.of(post, comments);
    }

    @Transactional
    public PostResponse update(Long postId, PostUpdateRequest request) {
        Post post = getPostWithMember(postId);
        post.update(request.title(), request.content());
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long postId) {
        Post post = getPostWithMember(postId);
        postRepository.delete(post);
    }

    private Post getPostWithMember(Long postId) {
        return postRepository.findByIdWithMember(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }
}
