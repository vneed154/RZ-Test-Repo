package com.rz.board.dto.response;

import com.rz.board.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        String writerNickname,
        long viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentResponse> comments
) {
    public static PostDetailResponse of(Post post, List<CommentResponse> comments) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getMember().getNickname(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                comments
        );
    }
}
