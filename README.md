# REST4 — 스프링부트 REST API by TDD

글/댓글 REST API를 처음부터 만들고, 테스트 코드로 다듬고, API 문서를 자동 생성한 뒤, 회원과 작성자 권한까지 TDD로 구현해 나가는 학습용 저장소입니다.

- 모든 커밋에는 `001`, `002` … 처럼 **순번 태그**가 붙어 있습니다.
- 커밋 메세지에는 **무엇을 / 왜 / 확인** 이 자세히 적혀 있습니다.
- 아래 작업내역을 위에서부터 따라 읽으면 됩니다.

## 목차
- [기술 스택과 실행 방법](#기술-스택과-실행-방법)
- [커밋 따라가는 방법](#커밋-따라가는-방법)
- [실험 커밋 목록](#실험-커밋-목록)
- [작업내역](#작업내역)

## 기술 스택과 실행 방법

| 구분 | 내용 |
|---|---|
| 언어 / 빌드 | Java 25, Gradle (Kotlin DSL, 래퍼 포함) |
| 프레임워크 | Spring Boot 4.1.2, Spring Web MVC |
| 편의 도구 | Lombok, Spring Boot DevTools |
| 데이터 | Spring Data JPA, H2 (개발용 파일 DB / 테스트용 메모리 DB), H2 콘솔 |
| 검증 | Bean Validation (spring-boot-starter-validation) |

> 이후 단계에서 의존성이 늘어나면 이 표도 함께 갱신됩니다.

실행:
```bash
./gradlew bootRun    # 서버 실행 (기본 포트 8080)
./gradlew test       # 테스트 실행
```

## 커밋 따라가는 방법

- **변경 내용(diff)** : `https://github.com/tbafe26/REST4/commit/태그` — 커밋 메세지와 바뀐 코드를 함께 봅니다.
- **그 시점의 코드** : `https://github.com/tbafe26/REST4/tree/태그` — 그 단계의 README와 코드 전체를 봅니다.
- **내 PC에서** :
  ```bash
  git clone https://github.com/tbafe26/REST4.git
  cd REST4
  git checkout 010      # 010 단계 코드로 이동
  git checkout main     # 최신으로 복귀
  ```
- 표시
  - **[실험]** : 일부러 문제가 드러나는 상태를 남긴 커밋입니다. 바로 다음 커밋에서 해결합니다.
  - 🔴 Red : 실패하는 테스트를 먼저 작성한 커밋
  - 🟢 Green : 테스트를 통과하도록 구현한 커밋
  - 🔵 Refactor : 동작은 그대로 두고 구조를 다듬은 커밋

## 실험 커밋 목록

일부러 문제가 드러나는 상태를 남긴 커밋입니다. 바로 뒤 커밋에서 해결합니다.

| 태그 | 실험 | 드러나는 문제 |
|---|---|---|
| [`009`](https://github.com/tbafe26/REST4/commit/009) | 엔티티를 그대로 JSON 으로 반환 | 글 ↔ 댓글 순환 참조로 직렬화가 끝나지 않음 (`Document nesting depth (501) exceeds the maximum allowed`) |

## 작업내역

### 0부. 저장소 준비 (001)

#### 001 저장소 준비
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/001) · [변경 내용](https://github.com/tbafe26/REST4/commit/001)

**무엇을** README 뼈대, `.gitignore`, `.gitattributes` 를 만들었습니다.

**왜** 단계마다 "무엇을 왜 바꿨는지"를 README에 쌓아 가는 학습 기록이므로 그 틀을 먼저 세웁니다. `.gitattributes` 로 `gradlew`(LF)와 `gradlew.bat`(CRLF)의 줄바꿈을 고정해 OS가 달라도 실행 스크립트가 깨지지 않게 합니다.

### 1부. 프로젝트 세팅과 REST API (002 ~ 048)

| 태그 | 내용 | 핵심 개념 |
|---|---|---|
| [`002`](https://github.com/tbafe26/REST4/commit/002) | 스프링부트 프로젝트 생성(webmvc, lombok, devtools) | Spring Initializr, Gradle 래퍼 |
| [`003`](https://github.com/tbafe26/REST4/commit/003) | JPA, H2, validation, H2 콘솔 의존성 추가 | starter 의존성 |
| [`004`](https://github.com/tbafe26/REST4/commit/004) | dev/test 프로파일, H2·JPA·로그 설정 | 프로파일, 파일/메모리 DB |
| [`005`](https://github.com/tbafe26/REST4/commit/005) | BaseEntity(id, 생성/수정 시각) + @EnableJpaAuditing | @MappedSuperclass, Auditing |
| [`006`](https://github.com/tbafe26/REST4/commit/006) | Post, PostComment 엔티티(1:N, cascade, orphanRemoval) | 연관관계 매핑, cascade |
| [`007`](https://github.com/tbafe26/REST4/commit/007) | PostRepository, PostService | Spring Data JPA, 서비스 계층 |
| [`008`](https://github.com/tbafe26/REST4/commit/008) | 샘플 데이터(글 3, 댓글 5) 생성 | ApplicationRunner, 자기 주입 @Lazy |
| [`009`](https://github.com/tbafe26/REST4/commit/009) | [실험] 글 다건조회 API, 엔티티 순환 참조로 JSON 무한 중첩 | Jackson 직렬화, 순환 참조 |
| [`010`](https://github.com/tbafe26/REST4/commit/010) | @JsonIgnore 로 순환 참조 해결 | @JsonIgnore |
| [`011`](https://github.com/tbafe26/REST4/commit/011) | @RestController 로 통합 | @RestController |

#### 002 스프링부트 프로젝트 생성
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/002) · [변경 내용](https://github.com/tbafe26/REST4/commit/002)

**무엇을** Spring Initializr 로 만든 프로젝트를 올렸습니다. `build.gradle.kts`(webmvc, lombok, devtools), `settings.gradle.kts`, Gradle 래퍼, `BackApplication`, `application.yaml`, 컨텍스트 로딩 테스트(`BackApplicationTests`)가 들어 있습니다. `.gitignore` 에는 `build/`, `.gradle` 같은 Initializr 기본 항목을 합쳤습니다.

**왜** 이후 모든 단계가 이 뼈대 위에 쌓입니다. 래퍼(`gradlew`)가 들어 있어 Gradle 을 따로 설치하지 않아도 같은 버전으로 빌드됩니다.

**확인** `./gradlew test` 통과.

#### 003 의존성 추가 (JPA, H2, validation, H2 콘솔)
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/003) · [변경 내용](https://github.com/tbafe26/REST4/commit/003)

**무엇을** `build.gradle.kts` 에 `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-h2console`, 런타임 `h2` 와 각각의 테스트 starter 를 추가했습니다.

**왜** 글/댓글을 DB에 저장하려면 JPA 와 DB 드라이버가 필요합니다. H2 는 별도 설치 없이 파일/메모리로 동작해 학습용으로 알맞고, 검증 starter 는 이후 요청 데이터 검증에 씁니다.

**확인** `./gradlew test` 통과. 아직 DB 접속 설정이 없지만 H2 임베디드 기본 설정으로 컨텍스트가 뜹니다.

#### 004 프로파일과 DB·JPA·로그 설정
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/004) · [변경 내용](https://github.com/tbafe26/REST4/commit/004)

**무엇을** `application.yaml` 에 기본 프로파일(`dev`), Hikari `auto-commit: false`, JPA(`show-sql`, `ddl-auto: update`, SQL 포맷), 로그 레벨을 설정했습니다. `application-dev.yaml` 은 파일 DB(`jdbc:h2:./db_dev`), `application-test.yaml` 은 메모리 DB(`jdbc:h2:mem:db_test`)를 가리킵니다. `.gitignore` 에는 H2 파일(`db_dev.*`)을 추가했습니다.

**왜** 개발 중에는 서버를 껐다 켜도 데이터가 남는 파일 DB가 편하고, 테스트는 매번 깨끗한 메모리 DB가 필요해 환경별 설정을 나눕니다. 실행되는 SQL 과 바인딩 값, 트랜잭션 시작/종료를 로그로 보면 JPA 가 무엇을 하는지 눈으로 확인할 수 있습니다.

```yaml
spring:
  profiles:
    active: dev
  datasource:
    hikari:
      auto-commit: false
```

**확인** `./gradlew test` 통과.

#### 005 BaseEntity 와 JPA Auditing
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/005) · [변경 내용](https://github.com/tbafe26/REST4/commit/005)

**무엇을** 모든 엔티티가 상속할 `BaseEntity`(`id`, `createDate`, `modifyDate`, `equals/hashCode`)를 만들고, `BackApplication` 에 `@EnableJpaAuditing` 을 붙였습니다.

**왜** 글, 댓글, 이후 회원까지 공통으로 필요한 PK 와 생성/수정 시각을 한 곳에 둡니다. `@MappedSuperclass` 는 테이블을 만들지 않고 필드만 물려주며, Auditing 을 켜면 저장/수정 시 시각이 자동으로 채워집니다. `equals/hashCode` 는 id 로 비교해 같은 행을 같은 객체로 취급하게 합니다.

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    private int id;
    @CreatedDate private LocalDateTime createDate;
    @LastModifiedDate private LocalDateTime modifyDate;
}
```

**확인** `./gradlew test` 통과.

#### 006 Post, PostComment 엔티티
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/006) · [변경 내용](https://github.com/tbafe26/REST4/commit/006)

**무엇을** `Post`(제목 `title`, 내용 `content`, 댓글 목록 `comments`)와 `PostComment`(`post`, `content`)를 만들었습니다. 글 쪽에는 `modify`, `addComment`, `findCommentById`, `deleteComment` 가 있습니다.

**왜** 글 1개에 댓글 N개인 1:N 구조입니다. 외래 키는 `PostComment.post` 가 갖고(연관관계의 주인), `Post.comments` 는 `mappedBy` 로 읽기 편의를 제공합니다. `cascade = {PERSIST, REMOVE}` 와 `orphanRemoval = true` 덕분에 글에서 댓글을 추가/제거하는 것만으로 DB 반영이 됩니다. 댓글 조작 규칙을 엔티티 안에 두어 서비스가 단순해집니다.

```java
@OneToMany(mappedBy = "post", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
private final List<PostComment> comments = new ArrayList<>();
```

**확인** `./gradlew test` 통과(엔티티 매핑이 잘못되면 컨텍스트 로딩에서 실패합니다).

#### 007 PostRepository, PostService
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/007) · [변경 내용](https://github.com/tbafe26/REST4/commit/007)

**무엇을** `PostRepository`(`JpaRepository<Post, Integer>`)와 `PostService`(`count`, `write`, `findById`, `findAll`, `modify`, `writeComment`, `deleteComment`, `modifyComment`)를 만들었습니다.

**왜** 컨트롤러가 저장소를 직접 만지지 않고 서비스를 거치게 하는 계층 구조의 시작입니다. `JpaRepository` 를 상속하면 CRUD 구현 없이 `save`, `findById`, `findAll` 을 쓸 수 있습니다.

**확인** `./gradlew test` 통과.

#### 008 샘플 데이터 (글 3개, 댓글 5개)
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/008) · [변경 내용](https://github.com/tbafe26/REST4/commit/008)

**무엇을** `BaseInitData` 의 `ApplicationRunner` 가 서버 시작 시 글 3개와 댓글 5개(1번 글 3개, 2번 글 2개)를 만듭니다. 이미 글이 있으면 건너뜁니다.

**왜** API 를 만들기 전에 눈으로 확인할 데이터가 필요합니다. 파일 DB 를 쓰므로 `count() > 0` 검사로 재시작해도 중복 생성되지 않게 합니다. 실제 저장 작업은 `@Transactional` 이 붙은 `work1()` 에서 하는데, 같은 클래스 안에서 직접 호출하면 프록시를 거치지 않아 트랜잭션이 적용되지 않으므로 `@Lazy` 로 자기 자신(프록시)을 주입해 `self.work1()` 로 부릅니다.

```java
@Autowired
@Lazy
private BaseInitData self;
```

**확인** `./gradlew test` 통과. 서버 시작 로그에 글 3건, 댓글 5건의 `insert` SQL 이 출력됩니다.

#### 009 [실험] 글 다건조회 API — 순환 참조로 JSON 무한 루프
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/009) · [변경 내용](https://github.com/tbafe26/REST4/commit/009)

**무엇을** `ApiV1PostController` 를 만들어 `GET /api/v1/posts` 가 `List<Post>` 엔티티를 그대로 반환하게 했습니다(`@Controller` + `@ResponseBody`).

**왜** 가장 단순한 방식으로 JSON 응답을 만들어 보고, 엔티티를 그대로 내보낼 때 어떤 문제가 생기는지 확인하려는 실험입니다. `Post.comments` 의 각 댓글이 `post` 를 다시 가리키고, 그 글이 또 `comments` 를 가리키는 순환 참조 때문에 Jackson 이 직렬화를 끝내지 못합니다.

> **확인해 볼 것** 서버를 띄워 `curl -i http://localhost:8080/api/v1/posts` 를 호출하면 응답이 `200` 으로 시작하지만 `comments → post → comments → …` 가 끝없이 중첩되다 잘립니다. 서버 로그에는 `HttpMessageNotWritableException: Could not write JSON: Document nesting depth (501) exceeds the maximum allowed (500)` 가 남습니다. 다음 단계에서 해결합니다.

#### 010 @JsonIgnore 로 순환 참조 끊기
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/010) · [변경 내용](https://github.com/tbafe26/REST4/commit/010)

**무엇을** `PostComment.post` 필드에 `@JsonIgnore` 를 붙였습니다.

**왜** 순환의 한 고리(댓글 → 글)를 JSON 직렬화에서 빼면 글 → 댓글 방향으로만 출력되어 무한 중첩이 사라집니다. 댓글은 어차피 글 안에 들어 있으므로 `post` 를 다시 보여 줄 필요가 없습니다.

```java
@ManyToOne(fetch = LAZY)
@JsonIgnore
private Post post;
```

**확인** `curl http://localhost:8080/api/v1/posts` 가 글 3개를 각 댓글 목록과 함께 정상 JSON 으로 반환합니다.

#### 011 @RestController
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/011) · [변경 내용](https://github.com/tbafe26/REST4/commit/011)

**무엇을** `@Controller` + 메서드의 `@ResponseBody` 를 `@RestController` 하나로 바꿨습니다.

**왜** REST API 컨트롤러는 모든 메서드가 뷰 이름이 아니라 데이터를 응답 본문(JSON)으로 돌려줍니다. `@RestController` 는 `@Controller` + `@ResponseBody` 를 합친 것이라 메서드마다 `@ResponseBody` 를 반복하지 않아도 됩니다.

```java
@RestController // @Controller + @ResponseBody
public class ApiV1PostController { ... }
```

**확인** `curl http://localhost:8080/api/v1/posts` 응답이 이전과 같습니다.
