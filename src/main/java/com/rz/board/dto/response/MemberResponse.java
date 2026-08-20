package com.rz.board.dto.response;

import com.rz.board.domain.Member;

public record MemberResponse(
        Long id,
        String email,
        String nickname
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getNickname());
    }
}
