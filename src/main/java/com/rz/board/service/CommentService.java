package com.rz.board.service;

import com.rz.board.domain.Comment;
import com.rz.board.domain.Member;
import com.rz.board.domain.Post;
import com.rz.board.dto.request.CommentCreateRequest;
import com.rz.board.dto.response.CommentResponse;
import com.rz.board.exception.BusinessException;
import com.rz.board.exception.ErrorCode;
import com.rz.board.repository.CommentRepository;
import com.rz.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    @Transactional
    public CommentResponse create(Long postId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        Member member = memberService.getMember(request.memberId());

        Comment comment = Comment.builder()
                .content(request.content())
                .post(post)
                .member(member)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentResponse.from(saved);
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
    }
}
