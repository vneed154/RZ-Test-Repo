package com.rz.board.service;

import com.rz.board.domain.Member;
import com.rz.board.dto.request.MemberCreateRequest;
import com.rz.board.dto.response.MemberResponse;
import com.rz.board.exception.BusinessException;
import com.rz.board.exception.ErrorCode;
import com.rz.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse register(MemberCreateRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = Member.builder()
                .email(request.email())
                .nickname(request.nickname())
                .password(request.password())
                .build();

        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved);
    }

    public MemberResponse findById(Long memberId) {
        Member member = getMember(memberId);
        return MemberResponse.from(member);
    }

    /**
     * 다른 서비스(PostService, CommentService)에서 작성자 존재 검증 용도로 재사용한다.
     * 패키지 내부용이라 반환 타입을 DTO가 아닌 엔티티로 둔다.
     */
    Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
