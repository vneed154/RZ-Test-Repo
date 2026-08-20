package com.rz.board.dto.response;

import com.rz.board.domain.Post;

import java.time.LocalDateTime;

/**
 * 목록/생성/수정 응답용 DTO. 댓글은 담지 않는다(상세 조회에서만 필요하기도 하고,
 * 여기서 comments.size()를 노출하면 목록 조회 때마다 컬렉션을 초기화하며
 * N+1이 재발하기 때문에 의도적으로 뺐다).
 */
public record PostResponse(
        Long id,
        String title,
        String writerNickname,
        long viewCount,
        LocalDateTime createdAt
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getMember().getNickname(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
