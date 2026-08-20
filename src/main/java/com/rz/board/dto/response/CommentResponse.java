package com.rz.board.dto.response;

import com.rz.board.domain.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String writerNickname,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getCreatedAt()
        );
    }
}
