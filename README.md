# RZ Board API

Spring 백엔드 설계 교안 이해도 테스트용 예시 프로젝트. 게시판(회원-게시글-댓글) 도메인을
계층형 아키텍처(Controller-Service-Repository-Domain-DTO)로 구현했다.

## 목적

이 코드는 실제 서비스가 아니라 **다른 AI/리뷰어에게 "Spring 백엔드 설계"에 대한 이해도를
확인받기 위한 샘플**이다. 너무 크지 않으면서도 아래 설계 포인트들을 실제로 확인할 수 있는
정도의 규모로 구성했다.

## 담고 있는 설계 포인트

- **계층형 아키텍처**: `controller` → `service` → `repository` → `domain` 단방향 의존.
  컨트롤러는 검증·응답 변환만, 비즈니스 로직은 서비스에만 둔다.
- **DTO 경계 분리**: 엔티티를 API로 직접 노출하지 않는다. 요청은 `dto/request`
  (Bean Validation 적용), 응답은 `dto/response` record로 분리.
- **연관관계 설계**: `Member`-`Post`-`Comment`는 전부 지연 로딩(LAZY)이 기본이고,
  실제로 함께 필요한 지점(목록/상세 조회)에서만 Repository에 `join fetch` 쿼리를 따로
  만들어 N+1을 방지한다 (`PostRepository#findAllWithMember`,
  `#findByIdWithMember`, `CommentRepository#findAllByPostIdWithMember`).
- **OSIV 비활성화** (`spring.jpa.open-in-view: false`): 지연 로딩은 반드시 서비스 계층의
  트랜잭션 범위 안에서만 허용되고, 컨트롤러/뷰 레이어에서 추가 쿼리가 발생할 수 없도록
  강제한다.
- **공통 예외 처리**: `ErrorCode` enum + `BusinessException` + `@RestControllerAdvice`
  (`GlobalExceptionHandler`) 조합으로 도메인 예외와 Bean Validation 실패를 동일한
  응답 형식(`ErrorResponse`)으로 변환한다.
- **페이징**: 게시글 목록은 `Pageable`을 그대로 받아 `Page<PostResponse>`로 응답.
- **감사(Auditing)**: `BaseTimeEntity` + `@EnableJpaAuditing`으로 생성/수정 시각을
  공통 처리.
- **테스트 3종**: 서비스 단위 테스트(Mockito), 리포지토리 슬라이스 테스트
  (`@DataJpaTest`), 컨트롤러 슬라이스 테스트(`@WebMvcTest`)를 각각 하나씩 두어
  계층별 테스트 전략의 차이를 보여준다.

## 의도적으로 뺀 것 (교육 목적상 단순화)

- **인증/인가(Spring Security, JWT 등)**: 범위 밖. `Member.password`는 평문 저장이며,
  실제 서비스라면 반드시 `BCryptPasswordEncoder` 등으로 암호화해야 한다.
- **API 문서화(Swagger/OpenAPI)**: 규모를 적당히 유지하기 위해 생략.
- **QueryDSL 등 동적 쿼리 도구**: 이 규모에서는 JPQL `@Query`만으로 충분해 도입하지 않음.

## 실행 방법

```bash
./gradlew bootRun
```

- H2 인메모리 DB를 사용하므로 별도 DB 설치가 필요 없다.
- H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:board`)

## 테스트

```bash
./gradlew test
```

> **참고**: 이 예시 코드는 네트워크가 제한된 샌드박스 환경에서 작성되어
> Maven Central에 접근할 수 없었고, 따라서 `./gradlew build`/`test`로 실제 컴파일·실행
> 검증은 하지 못했다. 코드는 문법·타입·설계를 수동으로 재검토했지만, 로컬(또는 리뷰하는
> AI)에서 반드시 한 번 빌드/테스트를 돌려 확인하는 걸 권장한다.

## API 목록

| Method | URL                                   | 설명                          |
|--------|----------------------------------------|-------------------------------|
| POST   | `/api/members`                         | 회원 가입                     |
| GET    | `/api/members/{memberId}`              | 회원 단건 조회                 |
| POST   | `/api/posts`                           | 게시글 생성                    |
| GET    | `/api/posts?page=0&size=10`            | 게시글 목록(페이징)             |
| GET    | `/api/posts/{postId}`                  | 게시글 상세 조회(조회수 +1)      |
| PATCH  | `/api/posts/{postId}`                  | 게시글 수정                    |
| DELETE | `/api/posts/{postId}`                  | 게시글 삭제                    |
| POST   | `/api/posts/{postId}/comments`         | 댓글 생성                      |
| DELETE | `/api/posts/{postId}/comments/{id}`    | 댓글 삭제                      |

## 리뷰용 체크리스트 (다른 AI에게 이해도 테스트를 맡길 때)

1. `PostRepository`가 목록/상세 조회에서 각각 어떤 fetch 전략을 쓰는지, 왜 그렇게
   나눴는지 설명할 수 있는가?
2. `open-in-view: false` 설정이 없다면 어떤 문제가 생길 수 있는지 설명할 수 있는가?
3. `BusinessException` / `ErrorCode` / `GlobalExceptionHandler`가 어떻게 협력해
   일관된 에러 응답을 만드는지 추적할 수 있는가?
4. 세 종류의 테스트(`PostServiceTest`, `PostRepositoryTest`, `PostControllerTest`)가
   각각 무엇을 검증하고, 무엇을 검증하지 않는지 구분할 수 있는가?
5. `Member.password`가 평문인 것과 Spring Security가 빠진 것이 왜 "버그"가 아니라
   "의도적 범위 제외"인지 README를 근거로 설명할 수 있는가?

## 패키지 구조

```
src/main/java/com/rz/board
├── BoardApplication.java
├── domain/            # 엔티티 (Member, Post, Comment, BaseTimeEntity)
├── repository/        # Spring Data JPA 리포지토리
├── service/           # 비즈니스 로직 + 트랜잭션 경계
├── controller/         # REST 엔드포인트
├── dto/
│   ├── request/        # 요청 DTO (Bean Validation)
│   └── response/        # 응답 DTO (record)
└── exception/          # ErrorCode, BusinessException, GlobalExceptionHandler
```
