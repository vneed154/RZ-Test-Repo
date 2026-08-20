package com.rz.board.repository;

import com.rz.board.domain.Member;
import com.rz.board.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("findAllWithMember는 fetch join으로 작성자를 함께 조회한다")
    void findAllWithMember_fetchJoin() {
        // given
        Member member = memberRepository.save(Member.builder()
                .email("writer@rz.com")
                .nickname("작성자")
                .password("password1234")
                .build());

        postRepository.save(Post.builder()
                .title("첫 번째 글")
                .content("내용")
                .member(member)
                .build());

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> result = postRepository.findAllWithMember(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMember().getNickname()).isEqualTo("작성자");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByIdWithMember는 존재하지 않는 id에 대해 빈 Optional을 반환한다")
    void findByIdWithMember_notFound() {
        assertThat(postRepository.findByIdWithMember(999L)).isEmpty();
    }
}
