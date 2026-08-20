package com.rz.board.service;

import com.rz.board.domain.Member;
import com.rz.board.domain.Post;
import com.rz.board.dto.request.PostCreateRequest;
import com.rz.board.dto.request.PostUpdateRequest;
import com.rz.board.dto.response.PostResponse;
import com.rz.board.exception.BusinessException;
import com.rz.board.exception.ErrorCode;
import com.rz.board.repository.CommentRepository;
import com.rz.board.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * 순수 단위 테스트: 실제 DB 없이 Repository/Service 협력만 Mockito로 검증한다.
 * DB까지 포함한 검증은 PostRepositoryTest(@DataJpaTest)에서 담당한다.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글을 생성하면 저장된 게시글 정보를 반환한다")
    void create_success() {
        // given
        Member member = createMember(1L, "tester@rz.com", "테스터");
        PostCreateRequest request = new PostCreateRequest(1L, "제목", "내용");
        Post savedPost = createPost(1L, "제목", "내용", member);

        given(memberService.getMember(1L)).willReturn(member);
        given(postRepository.save(any(Post.class))).willReturn(savedPost);

        // when
        PostResponse response = postService.create(request);

        // then
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.writerNickname()).isEqualTo("테스터");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 수정하려 하면 POST_NOT_FOUND 예외가 발생한다")
    void update_postNotFound() {
        // given
        given(postRepository.findByIdWithMember(999L)).willReturn(Optional.empty());
        PostUpdateRequest request = new PostUpdateRequest("수정 제목", "수정 내용");

        // when & then
        assertThatThrownBy(() -> postService.update(999L, request))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.POST_NOT_FOUND));
    }

    private Member createMember(Long id, String email, String nickname) {
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .password("password1234")
                .build();
        setId(member, id);
        return member;
    }

    private Post createPost(Long id, String title, String content, Member member) {
        Post post = Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
        setId(post, id);
        return post;
    }

    // JPA는 영속화 시점에 id를 채워주지만, 순수 단위 테스트에서는 DB를 타지 않으므로
    // 리플렉션으로 흉내낸다. (통합 테스트에서는 필요 없음 - PostRepositoryTest 참고)
    private void setId(Object target, Long id) {
        try {
            Field field = target.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(target, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
