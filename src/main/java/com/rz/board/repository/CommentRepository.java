package com.rz.board.repository;

import com.rz.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글 상세 조회 시 댓글 작성자(member)까지 fetch join으로 함께 가져온다.
     * (여기서 join fetch를 빼먹으면 댓글 개수만큼 N+1 쿼리가 발생한다)
     */
    @Query("select c from Comment c join fetch c.member where c.post.id = :postId order by c.createdAt asc")
    List<Comment> findAllByPostIdWithMember(@Param("postId") Long postId);
}
