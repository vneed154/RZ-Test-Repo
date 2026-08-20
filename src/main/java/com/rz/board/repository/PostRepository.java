package com.rz.board.repository;

import com.rz.board.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 목록 조회 시 작성자(member)를 fetch join으로 함께 가져와 N+1을 방지한다.
     * count 쿼리는 fetch join 없이 별도로 둔다(컬렉션이 아니라 단순 ManyToOne이라
     * 페이지네이션 자체는 정상 동작하지만, count까지 join할 필요는 없어 분리했다).
     */
    @Query(value = "select p from Post p join fetch p.member",
            countQuery = "select count(p) from Post p")
    Page<Post> findAllWithMember(Pageable pageable);

    @Query("select p from Post p join fetch p.member where p.id = :id")
    Optional<Post> findByIdWithMember(@Param("id") Long id);
}
