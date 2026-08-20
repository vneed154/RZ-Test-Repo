package com.rz.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(

        @NotNull(message = "작성자 ID는 필수입니다.")
        Long memberId,

        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 500, message = "댓글은 500자를 넘을 수 없습니다.")
        String content
) {
}
