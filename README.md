# REST4 — 스프링부트 REST API by TDD

글/댓글 REST API를 처음부터 만들고, 테스트 코드로 다듬고, API 문서를 자동 생성한 뒤, 회원과 작성자 권한까지 TDD로 구현해 나가는 학습용 저장소입니다.

- 모든 커밋에는 `001`, `002` … 처럼 **순번 태그**가 붙어 있습니다.
- 커밋 메세지에는 **무엇을 / 왜 / 확인** 이 자세히 적혀 있습니다.
- 아래 작업내역을 위에서부터 따라 읽으면 됩니다.

## 목차
- [기술 스택과 실행 방법](#기술-스택과-실행-방법)
- [커밋 따라가는 방법](#커밋-따라가는-방법)
- [실험 커밋 목록](#실험-커밋-목록)
- [패키지 구조](#패키지-구조)
- [API 명세](#API-명세)
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
| [`014`](https://github.com/tbafe26/REST4/commit/014) | 엔티티 필드명 `createDate` → `createdDate` | 응답 JSON 키가 바뀌고 기존 행의 값이 `null` 이 됨 |
| [`015`](https://github.com/tbafe26/REST4/commit/015) | 엔티티 필드명 `title`/`content` → `subject`/`body` | 응답 키가 `subject`, `body` 로 바뀌고, 댓글까지 키가 `body` 로 바뀜 |
| [`017`](https://github.com/tbafe26/REST4/commit/017) | getter 없는 `PostDto` 반환 | 오류 없이 빈 객체가 응답됨: 다건 `[{},{},{}]`, 단건 `{}` |
| [`024`](https://github.com/tbafe26/REST4/commit/024) | `@Transactional` 없이 댓글 삭제 | "1번 댓글이 삭제되었습니다." 응답 후에도 댓글이 그대로 남아 있음 |
| [`030`](https://github.com/tbafe26/REST4/commit/030) | 댓글 전용 `RsData` 로 글 삭제 응답 | 컴파일 오류: `PostDto cannot be converted to PostCommentDto` |

## 패키지 구조

```
com.back
├─ BackApplication
├─ domain
│  └─ post
│     ├─ post
│     │  ├─ controller   ApiV1PostController
│     │  ├─ entity       Post
│     │  ├─ repository   PostRepository
│     │  └─ service      PostService
│     └─ postComment
│        └─ entity       PostComment
└─ global
   ├─ initData           BaseInitData (샘플 데이터)
   └─ jpa.entity         BaseEntity
```

도메인(`domain`)은 `기능/세부기능/계층` 순서로 나누고, 여러 도메인이 함께 쓰는 코드는 `global` 에 둡니다.

## API 명세

기본 경로는 `http://localhost:8080` 입니다. 단계가 진행되면서 응답 형태가 바뀌므로, 아래는 **최신 단계 기준**이며 단계별 변화는 작업내역에 적혀 있습니다.

### 글

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/api/v1/posts` | 글 다건 조회 |
| GET | `/api/v1/posts/{id}` | 글 단건 조회 |

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
| [`012`](https://github.com/tbafe26/REST4/commit/012) | 클래스 레벨 @RequestMapping 으로 공통 경로 분리 | @RequestMapping |
| [`013`](https://github.com/tbafe26/REST4/commit/013) | 글 단건조회 GET /api/v1/posts/{id} | @PathVariable |
| [`014`](https://github.com/tbafe26/REST4/commit/014) | [실험] 엔티티 필드명 createDate → createdDate 변경 | 엔티티 = API 스펙, ddl-auto update |
| [`015`](https://github.com/tbafe26/REST4/commit/015) | [실험] 엔티티 필드명 title/content → subject/body | 엔티티 변경의 파급 범위 |
| [`016`](https://github.com/tbafe26/REST4/commit/016) | 엔티티 필드명 원상복구 | 엔티티와 API 응답의 분리 필요성 |
| [`017`](https://github.com/tbafe26/REST4/commit/017) | [실험] PostDto 도입(getter 없음) | DTO, Jackson 직렬화는 getter 기반 |
| [`018`](https://github.com/tbafe26/REST4/commit/018) | `fail-on-empty-beans` 설정 메모 | Jackson 직렬화 설정 |
| [`019`](https://github.com/tbafe26/REST4/commit/019) | PostDto 에 @Getter | Lombok @Getter, 직렬화 대상 속성 |
| [`020`](https://github.com/tbafe26/REST4/commit/020) | PostDto → record | record, 불변 DTO |
| [`021`](https://github.com/tbafe26/REST4/commit/021) | DTO 필드명을 createDate/modifyDate/title/content 로 | DTO 덕분에 엔티티 수정 없이 응답 변경 |
| [`022`](https://github.com/tbafe26/REST4/commit/022) | `.map(PostDto::new)` | 생성자 메서드 참조 |
| [`023`](https://github.com/tbafe26/REST4/commit/023) | 댓글 다건/단건조회, PostCommentDto | 중첩 리소스 경로, 다중 @PathVariable |
| [`024`](https://github.com/tbafe26/REST4/commit/024) | [실험] 댓글 삭제(GET 임시), @Transactional 없음 | 트랜잭션과 더티체킹 |
| [`025`](https://github.com/tbafe26/REST4/commit/025) | 액션 메서드에 @Transactional / readOnly | 영속성 컨텍스트, orphanRemoval, readOnly 트랜잭션 |
| [`026`](https://github.com/tbafe26/REST4/commit/026) | 삭제 응답을 Map(resultCode, msg) 으로 | 조회 외 응답의 결과 코드·메시지 |
| [`027`](https://github.com/tbafe26/REST4/commit/027) | RsData(resultCode, msg) record | 공통 응답 객체 |
| [`028`](https://github.com/tbafe26/REST4/commit/028) | RsData 에 data(PostCommentDto) 추가 | 부가 데이터 응답 |
| [`029`](https://github.com/tbafe26/REST4/commit/029) | 글 삭제(GET 임시) | CascadeType.REMOVE |
| [`030`](https://github.com/tbafe26/REST4/commit/030) | [실험] 글 삭제 응답에 RsData 재사용 | 타입이 고정된 공통 응답의 한계 |
| [`031`](https://github.com/tbafe26/REST4/commit/031) | ForPostRsData 임시 도입 | 타입별 응답 클래스의 중복 |
| [`032`](https://github.com/tbafe26/REST4/commit/032) | ForPostRsData 제거, data 타입 Object | 범용화(Object)의 대가 |
| [`033`](https://github.com/tbafe26/REST4/commit/033) | RsData<T> 제네릭, 글 삭제는 RsData<PostDto> | 제네릭으로 타입 안정성 확보 |
| [`034`](https://github.com/tbafe26/REST4/commit/034) | 댓글 삭제 RsData<PostCommentDto> | raw 타입 지양 |
| [`035`](https://github.com/tbafe26/REST4/commit/035) | 삭제 응답 RsData<Void>, data 없는 생성자 | Void 타입 인자 |
| [`036`](https://github.com/tbafe26/REST4/commit/036) | 글/댓글 삭제 → `DELETE /{id}` | HTTP 메서드의 의미 |
| [`037`](https://github.com/tbafe26/REST4/commit/037) | 글 작성 `POST /api/v1/posts`, JSON 본문 + 검증 | @RequestBody, @Valid |
| [`038`](https://github.com/tbafe26/REST4/commit/038) | PostWriteForm → PostWriteReqBody | 이름이 역할을 드러내게 |
| [`039`](https://github.com/tbafe26/REST4/commit/039) | 작성 응답 data 를 List(totalCount, post) 로 | data 에 여러 값 담기 |
| [`040`](https://github.com/tbafe26/REST4/commit/040) | List → Map(totalCount, post) | 이름 있는 응답 필드 |
| [`041`](https://github.com/tbafe26/REST4/commit/041) | Map → 전용 record PostWriteResBody | 액션별 응답 본문 타입 |
| [`042`](https://github.com/tbafe26/REST4/commit/042) | 지역 변수 제거, 인라인 생성 | 가독성 정리 |
| [`043`](https://github.com/tbafe26/REST4/commit/043) | 작성 성공 resultCode `201-1` | 201 Created, resultCode ≠ HTTP 상태 |
| [`044`](https://github.com/tbafe26/REST4/commit/044) | ResponseEntity + HttpStatus.CREATED | HTTP 상태 코드 직접 지정 |
| [`045`](https://github.com/tbafe26/REST4/commit/045) | ResponseEntity 되돌리기 | 비용 대비 효과 판단 |
| [`046`](https://github.com/tbafe26/REST4/commit/046) | ResponseAspect(AOP), RsData.statusCode | @Around, 공통 후처리 |
| [`047`](https://github.com/tbafe26/REST4/commit/047) | 글 수정 `PUT /api/v1/posts/{id}`, statusCode @JsonIgnore | PUT, @JsonIgnore |

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

#### 012 클래스 레벨 @RequestMapping
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/012) · [변경 내용](https://github.com/tbafe26/REST4/commit/012)

**무엇을** 공통 경로 `/api/v1/posts` 를 클래스에 `@RequestMapping` 으로 올리고, 메서드는 `@GetMapping` 만 남겼습니다.

**왜** 글 관련 엔드포인트가 늘어날수록 메서드마다 `/api/v1/posts` 를 반복하게 됩니다. 공통 접두사를 클래스에 한 번만 적으면 중복이 줄고 경로를 바꿀 때 한 곳만 고치면 됩니다.

```java
@RestController
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {
    @GetMapping
    public List<Post> getItems() { ... }
}
```

**확인** `./gradlew test` 통과. 요청 경로(`GET /api/v1/posts`)는 그대로입니다.

#### 013 글 단건조회
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/013) · [변경 내용](https://github.com/tbafe26/REST4/commit/013)

**무엇을** `GET /api/v1/posts/{id}` 를 추가했습니다. `@PathVariable` 로 id 를 받아 `postService.findById(id)` 결과를 반환합니다. README 에 "패키지 구조", "API 명세" 섹션도 만들었습니다.

**왜** 목록만으로는 특정 글 하나를 가져올 수 없습니다. 경로의 `{id}` 를 메서드 인자로 받는 것이 REST 에서 리소스 하나를 지정하는 기본 방식입니다. (지금은 없는 id 를 요청하면 `Optional.get()` 이 예외를 내는데, 예외 처리는 이후 단계에서 다룹니다.)

```java
@GetMapping("/{id}")
public Post getItem(@PathVariable int id) {
    return postService.findById(id).get();
}
```

**확인** `curl http://localhost:8080/api/v1/posts/1` 이 1번 글과 댓글 3개를 반환합니다.

#### 014 [실험] 엔티티 필드명 변경 — createDate → createdDate
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/014) · [변경 내용](https://github.com/tbafe26/REST4/commit/014)

**무엇을** `BaseEntity.createDate` 를 `createdDate` 로 이름만 바꿨습니다.

**왜** 엔티티를 그대로 JSON 으로 내보내면 **필드 이름이 곧 API 스펙**이 됩니다. 내부 코드를 정리하려고 이름을 바꾸는 순간 외부(프론트엔드 등)가 쓰던 응답이 달라지고, 컬럼 이름도 함께 바뀌어 DB 까지 영향을 받는다는 것을 확인하는 실험입니다.

> **확인해 볼 것** `curl http://localhost:8080/api/v1/posts/1` 응답의 키가 `createDate` 에서 `createdDate` 로 바뀌고, 기존 DB 데이터는 새 컬럼(`created_date`)이 비어 있어 값이 `null` 로 나옵니다(`ddl-auto: update` 가 컬럼을 새로 추가만 하기 때문). 로그에도 `add column created_date` 가 보입니다.


#### 015 [실험] 엔티티 필드명 변경 — title → subject, content → body
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/015) · [변경 내용](https://github.com/tbafe26/REST4/commit/015)

**무엇을** `Post` 의 `title`, `content` 를 `subject`, `body` 로, `PostComment` 의 `content` 를 `body` 로 바꾸고, 생성자·`modify`·`addComment` 와 `PostService` 의 매개변수 이름까지 따라 바꿨습니다.

**왜** "응답 JSON 의 키 이름을 바꿔 달라"는 요구를 엔티티를 고쳐서 들어주면 어디까지 바뀌어야 하는지 직접 겪어 봅니다. 이름 하나를 바꾸려고 엔티티, 서비스, 테이블 컬럼까지 손대야 합니다.

**확인** (test 프로파일로 서버 실행 후 `curl http://localhost:8080/api/v1/posts/1`) 응답이 `{"subject":"제목 1","body":"내용 1","comments":[{"body":"댓글 1-1", ...}], ...}` 처럼 바뀌었습니다. 이름 하나를 바꾸려고 엔티티 2개와 서비스까지 고쳐야 했고, 응답 JSON 도 통째로 달라집니다.


#### 016 엔티티 필드명 원상복구 — 응답 모양은 DTO 로 맞추기로
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/016) · [변경 내용](https://github.com/tbafe26/REST4/commit/016)

**무엇을** 014, 015 에서 바꿨던 `createdDate`, `subject`, `body` 를 모두 원래 이름(`createDate`, `title`, `content`)으로 되돌렸습니다.

**왜** API 에서 쓰는 이름은 클라이언트와 협의하면서 언제든 바뀔 수 있습니다. 그때마다 엔티티와 DB 를 고치는 것은 비용이 너무 큽니다. 엔티티는 그대로 두고, **응답 전용 객체(DTO)** 에서 이름을 맞추는 방향으로 갑니다.

**확인** `curl http://localhost:8080/api/v1/posts/1` 응답 키가 다시 `createDate`, `title`, `content` 입니다.


#### 017 [실험] PostDto 도입 — getter 가 없는 DTO
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/017) · [변경 내용](https://github.com/tbafe26/REST4/commit/017)

**무엇을** `PostDto` 클래스를 만들고 컨트롤러가 엔티티 대신 `PostDto` 를 반환하게 했습니다. DTO 의 필드 이름은 클라이언트 요구대로 `createdDate`, `modifiedDate`, `subject`, `body` 입니다. 이제 댓글은 응답에 포함되지 않으므로 `PostComment.post` 의 `@JsonIgnore` 는 제거했습니다.

**왜** 엔티티를 건드리지 않고 응답 모양만 바꾸려면 응답 전용 객체가 필요합니다. 다만 이 DTO 에는 `private final` 필드만 있고 getter 가 없습니다. Jackson 은 getter(또는 public 필드)로 값을 읽기 때문에 어떤 결과가 나오는지 확인하는 실험입니다.

```java
public class PostDto {
    private final int id;
    private final LocalDateTime createdDate;
    private final LocalDateTime modifiedDate;
    private final String subject;
    private final String body;
    // getter 없음
}
```

**확인** (test 프로파일로 서버 실행 후 curl) 다건조회는 `[{},{},{}]`, 단건조회는 `{}` 가 **200** 으로 응답됩니다. 예외는 나지 않지만 Jackson 이 읽을 수 있는 속성이 없어 내용이 모두 사라집니다.


#### 018 Jackson 빈 객체 직렬화 설정 살펴보기
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/018) · [변경 내용](https://github.com/tbafe26/REST4/commit/018)

**무엇을** `application.yaml` 에 `spring.jackson.serialization.fail-on-empty-beans: false` 설정을 **주석으로** 남겼습니다.

**왜** 읽을 수 있는 속성이 하나도 없는 객체를 직렬화할 때 오류를 낼지(`true`) 빈 객체 `{}` 로 내보낼지(`false`) 정하는 설정입니다. 예전에는 `true` 가 기본값이라 getter 없는 DTO 가 바로 오류를 냈지만, 지금 쓰는 버전은 기본값이 `false` 라서 따로 켤 필요가 없습니다. 오류가 안 난다고 문제가 해결된 것은 아니라는 점을 기록해 둡니다.

```yaml
#  jackson:
#    serialization:
#      fail-on-empty-beans: false # 예전에는 true가 기본값이었지만, 지금은 false가 기본값입니다.
```

**확인** 설정 주석만 추가했으므로 동작 변화는 없습니다.


#### 019 PostDto 에 @Getter 적용
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/019) · [변경 내용](https://github.com/tbafe26/REST4/commit/019)

**무엇을** `PostDto` 에 `@Getter` 를 붙였고, 018 에서 남긴 Jackson 설정 주석은 지웠습니다.

**왜** Jackson 이 값을 읽을 수 있도록 getter 를 만들어 주는 것이 정답입니다. 이제 응답 JSON 에 `id`, `createdDate`, `modifiedDate`, `subject`, `body` 가 담깁니다.

**확인** `curl http://localhost:8080/api/v1/posts` 응답에 DTO 의 다섯 필드가 모두 보입니다.


#### 020 PostDto 를 record 로 변경
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/020) · [변경 내용](https://github.com/tbafe26/REST4/commit/020)

**무엇을** `PostDto` 를 `record` 로 바꿨습니다. `Post` 를 받는 보조 생성자는 `this(...)` 로 정식 생성자를 호출합니다.

**왜** DTO 는 값을 담아 전달만 하는 불변 객체입니다. `record` 는 `private final` 필드, 생성자, 접근자(`id()` 등), `equals/hashCode/toString` 을 자동으로 만들어 주고, Jackson 도 record 의 접근자를 그대로 읽습니다. `@Getter` 와 필드 선언이 모두 사라져 코드가 짧아집니다.

```java
public record PostDto(int id, LocalDateTime createdDate, LocalDateTime modifiedDate, String subject, String body) {
    public PostDto(Post post) {
        this(post.getId(), post.getCreateDate(), post.getModifyDate(), post.getTitle(), post.getContent());
    }
}
```

**확인** 응답 JSON 은 019 와 같습니다.


#### 021 응답 필드명은 DTO 에서만 변경
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/021) · [변경 내용](https://github.com/tbafe26/REST4/commit/021)

**무엇을** 클라이언트와 다시 협의한 결과에 맞춰 `PostDto` 의 필드 이름을 `createDate`, `modifyDate`, `title`, `content` 로 바꿨습니다.

**왜** 이번에는 **DTO 한 파일만** 바뀌었습니다. 엔티티·서비스·DB 는 그대로입니다. 014~015 에서 엔티티를 고쳤을 때와 비교하면 DTO 를 두는 이유가 분명해집니다.

**확인** `curl http://localhost:8080/api/v1/posts/1` 응답 키가 `id, createDate, modifyDate, title, content` 입니다.


#### 022 메서드 참조로 DTO 변환 간소화
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/022) · [변경 내용](https://github.com/tbafe26/REST4/commit/022)

**무엇을** 다건조회의 `.map(p -> new PostDto(p))` 를 `.map(PostDto::new)` 로 바꿨습니다.

**왜** 람다가 인자를 그대로 생성자에 넘기기만 할 때는 생성자 메서드 참조로 줄여 쓸 수 있습니다. 동작은 같습니다.

**확인** 응답은 이전과 같습니다.


#### 023 댓글 다건조회·단건조회 API
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/023) · [변경 내용](https://github.com/tbafe26/REST4/commit/023)

**무엇을** `ApiV1PostCommentController` 와 `PostCommentDto`(record) 를 추가했습니다.
- `GET /api/v1/posts/{postId}/comments` : 해당 글의 댓글 목록
- `GET /api/v1/posts/{postId}/comments/{id}` : 해당 글의 댓글 하나

**왜** 댓글은 항상 어떤 글에 속하므로 경로도 `글/{postId}/댓글` 형태로 표현합니다. 댓글은 글(애그리거트 루트)을 통해 꺼내므로 별도 리포지터리 없이 `post.getComments()`, `post.findCommentById(id)` 를 사용합니다. 응답은 처음부터 DTO 로 내보냅니다.

```java
@GetMapping("/{id}")
public PostCommentDto getItem(@PathVariable int postId, @PathVariable int id) {
    Post post = postService.findById(postId).get();
    PostComment postComment = post.findCommentById(id).get();
    return new PostCommentDto(postComment);
}
```

**확인** `curl http://localhost:8080/api/v1/posts/1/comments` 는 1번 글의 댓글 3개, `.../comments/1` 은 댓글 하나를 반환합니다.


#### 024 [실험] 댓글 삭제 API — @Transactional 없이
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/024) · [변경 내용](https://github.com/tbafe26/REST4/commit/024)

**무엇을** 댓글 삭제를 추가했습니다. 브라우저 주소창으로 바로 호출해 보기 위해 임시로 `GET /api/v1/posts/{postId}/comments/{id}/delete` 로 열었습니다. `postService.deleteComment(post, postComment)` 는 글의 댓글 목록에서 댓글을 빼기만 합니다(`orphanRemoval = true` 에 의해 DB 삭제를 기대).

**왜** 컬렉션에서 빼는 것만으로 DELETE 쿼리가 나가려면 엔티티가 **영속성 컨텍스트에 관리되는 상태에서 트랜잭션이 커밋**되어야 합니다. 그 조건이 없는 지금 상태에서 어떻게 되는지 확인하는 실험입니다.

**확인** (test 프로파일로 서버 실행 후 curl) `GET /api/v1/posts/1/comments/1/delete` 는 `1번 댓글이 삭제되었습니다.` 를 **200** 으로 응답하지만, 이어서 `GET /api/v1/posts/1/comments` 를 보면 1번 댓글이 **그대로 남아 있습니다**. 서버 로그에도 `delete` 쿼리가 없습니다. 트랜잭션 밖에서는 컬렉션에서 빼는 것이 DB 에 반영되지 않습니다.


#### 025 액션 메서드에 @Transactional 적용
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/025) · [변경 내용](https://github.com/tbafe26/REST4/commit/025)

**무엇을** 조회 액션에는 `@Transactional(readOnly = true)`, 댓글 삭제 액션에는 `@Transactional` 을 붙였습니다.

**왜** 하나의 요청 안에서 글 조회 → 댓글 제거가 **같은 트랜잭션** 안에서 일어나야 영속성 컨텍스트가 변경을 추적하고, 커밋 시점에 `orphanRemoval` 에 의한 DELETE 가 실행됩니다. 조회만 하는 액션은 `readOnly = true` 로 의도를 드러내고 불필요한 변경 감지를 줄입니다.

```java
@GetMapping("/{id}/delete")
@Transactional
public String delete(@PathVariable int postId, @PathVariable int id) { ... }
```

**확인** `curl http://localhost:8080/api/v1/posts/1/comments/1/delete` 후 `.../comments` 목록에서 1번 댓글이 사라집니다.


#### 026 삭제 응답에 resultCode 와 msg 담기
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/026) · [변경 내용](https://github.com/tbafe26/REST4/commit/026)

**무엇을** 댓글 삭제 응답을 문자열 대신 `Map`(`resultCode: "200-1"`, `msg`) 으로 바꿨습니다. 순서가 유지되도록 `LinkedHashMap` 을 썼습니다.

**왜** 조회가 아닌 요청(삭제, 작성, 수정)은 "성공했는지/어떤 결과인지"를 클라이언트가 **기계적으로** 판단할 수 있어야 합니다. 사람에게 보여줄 `msg` 와, 코드로 분기할 `resultCode` 를 함께 내려줍니다.

**확인** 삭제 응답이 `{"resultCode":"200-1","msg":"1번 댓글이 삭제되었습니다."}` 입니다.


#### 027 RsData 도입
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/027) · [변경 내용](https://github.com/tbafe26/REST4/commit/027)

**무엇을** `global/rsData/RsData` record(`resultCode`, `msg`) 를 만들고 댓글 삭제가 `RsData` 를 반환하게 했습니다.

**왜** `resultCode` 와 `msg` 는 조회를 제외한 모든 응답에 필요한 공통 요소입니다. 매번 `Map` 에 키 문자열을 넣으면 오타가 나기 쉽고 응답 형태가 보장되지 않으므로 전용 타입으로 고정합니다.

```java
public record RsData(String resultCode, String msg) { }
```

**확인** 응답 JSON 은 026 과 같습니다.


#### 028 RsData 에 data 필드 추가
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/028) · [변경 내용](https://github.com/tbafe26/REST4/commit/028)

**무엇을** `RsData` 에 세 번째 필드 `data` 를 추가하고, 댓글 삭제 시 삭제된 댓글의 `PostCommentDto` 를 담아 응답합니다. 지금은 타입이 `PostCommentDto` 로 고정되어 있습니다.

**왜** `resultCode` 와 `msg` 만으로는 표현하기 어려운 부가 정보(삭제된 댓글 내용, 새로 만든 글의 id 등)를 함께 돌려주면 클라이언트가 추가 요청을 하지 않아도 됩니다.

**확인** 삭제 응답에 `"data": {"id":1, ..., "content":"댓글 1-1"}` 이 포함됩니다.


#### 029 글 삭제 API
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/029) · [변경 내용](https://github.com/tbafe26/REST4/commit/029)

**무엇을** `GET /api/v1/posts/{id}/delete`(임시) 로 글을 삭제하고 삭제된 글의 `PostDto` 를 반환합니다. `PostService.delete(post)` 는 `postRepository.delete(post)` 를 호출합니다.

**왜** 글에는 댓글이 딸려 있지만 `Post.comments` 에 `CascadeType.REMOVE` 가 걸려 있어서, 글을 지우면 JPA 가 관련 댓글을 **먼저** 삭제한 뒤 글을 삭제합니다. 외래키 제약 때문에 직접 댓글을 지울 필요가 없습니다.

**확인** `curl http://localhost:8080/api/v1/posts/1/delete` 후 SQL 로그에 댓글 DELETE 들이 글 DELETE 보다 먼저 찍힙니다.


#### 030 [실험] 글 삭제 응답에 댓글 전용 RsData 재사용
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/030) · [변경 내용](https://github.com/tbafe26/REST4/commit/030)

**무엇을** 글 삭제도 `resultCode`, `msg`, `data` 형태로 응답하려고 `RsData` 를 그대로 썼습니다. `data` 에는 `PostDto` 를 넣었습니다.

**왜** 지금 `RsData.data` 의 타입은 `PostCommentDto` 로 고정되어 있습니다. 공통 응답 객체가 특정 타입에 묶여 있으면 다른 리소스에 재사용할 수 없다는 것을 확인하는 실험입니다.

**확인** `./gradlew compileJava` 가 `error: incompatible types: PostDto cannot be converted to PostCommentDto` 로 **컴파일 단계에서 실패**합니다. 타입이 고정된 공통 응답 객체는 다른 리소스에 재사용할 수 없습니다.


#### 031 글 전용 ForPostRsData 임시 도입
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/031) · [변경 내용](https://github.com/tbafe26/REST4/commit/031)

**무엇을** `data` 가 `PostDto` 인 `ForPostRsData` record 를 따로 만들어 글 삭제 응답에 사용했습니다.

**왜** 일단 동작하게 만드는 가장 단순한 방법입니다. 하지만 리소스가 늘 때마다 `ForXxxRsData` 가 늘어나는 구조라 오래 갈 수 없습니다.

**확인** 글 삭제 응답이 `{"resultCode":"200-1","msg":"1번 글이 삭제되었습니다.","data":{...}}` 형태입니다.


#### 032 ForPostRsData 제거, RsData.data 를 Object 로
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/032) · [변경 내용](https://github.com/tbafe26/REST4/commit/032)

**무엇을** `ForPostRsData` 를 지우고 `RsData.data` 의 타입을 `Object` 로 바꿔 글/댓글 삭제 모두 `RsData` 를 씁니다.

**왜** `Object` 로 두면 어떤 데이터든 담을 수 있어 클래스 하나로 통합됩니다. 다만 메서드 시그니처만 보고는 `data` 에 무엇이 들어있는지 알 수 없고, 꺼내 쓸 때 형변환이 필요하다는 단점이 생깁니다.

**확인** 글/댓글 삭제 응답 JSON 은 이전과 같습니다.


#### 033 RsData 에 제네릭 적용
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/033) · [변경 내용](https://github.com/tbafe26/REST4/commit/033)

**무엇을** `RsData` 를 `RsData<T>(String resultCode, String msg, T data)` 로 바꾸고 글 삭제의 반환 타입을 `RsData<PostDto>` 로 명시했습니다.

**왜** 하나의 클래스로 모든 응답을 표현하면서도 `data` 의 타입을 컴파일 시점에 알 수 있습니다. 반환 타입만 봐도 어떤 데이터가 오는지 문서처럼 읽힙니다.

```java
public record RsData<T>(String resultCode, String msg, T data) { }
```

> **확인해 볼 것** 댓글 삭제는 아직 raw 타입 `RsData` 로 남아 있는데도 컴파일됩니다. raw 타입은 `RsData<Object>` 처럼 동작하지만 타입 검사를 건너뛰므로 지양합니다.


#### 034 댓글 삭제 응답 타입에도 제네릭 명시
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/034) · [변경 내용](https://github.com/tbafe26/REST4/commit/034)

**무엇을** 댓글 삭제의 반환 타입을 `RsData<PostCommentDto>` 로, 생성을 `new RsData<>(...)` 로 바꿨습니다.

**왜** raw 타입 `RsData` 는 사실상 `RsData<Object>` 처럼 동작해 제네릭을 도입한 이점(타입 검사)을 잃습니다. 모든 곳에서 타입 인자를 명시합니다.

**확인** 응답 JSON 은 이전과 같습니다.


#### 035 데이터가 필요 없는 응답은 RsData<Void>
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/035) · [변경 내용](https://github.com/tbafe26/REST4/commit/035)

**무엇을** 글/댓글 삭제 응답에서 `data` 를 빼고 반환 타입을 `RsData<Void>` 로 바꿨습니다. `RsData` 에는 `resultCode`, `msg` 만 받는 보조 생성자를 추가했습니다.

**왜** 삭제된 대상의 정보는 클라이언트가 이미 알고 있으므로 굳이 돌려줄 필요가 없습니다. "데이터가 없다"는 사실을 `RsData<Void>` 로 **타입에 명시**하면 읽는 사람이 바로 알 수 있습니다.

```java
public RsData(String resultCode, String msg) {
    this(resultCode, msg, null);
}
```

**확인** 삭제 응답이 `{"resultCode":"200-1","msg":"1번 글이 삭제되었습니다.","data":null}` 입니다.


#### 036 삭제는 DELETE 메서드로
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/036) · [변경 내용](https://github.com/tbafe26/REST4/commit/036)

**무엇을** 임시로 `GET /{id}/delete` 였던 글/댓글 삭제를 `@DeleteMapping("/{id}")` 로 바꿨습니다.
- `DELETE /api/v1/posts/{id}`
- `DELETE /api/v1/posts/{postId}/comments/{id}`

**왜** REST 에서는 **주소(URL)는 리소스**를, **HTTP 메서드는 행위**를 나타냅니다. GET 은 조회용이라 브라우저 미리 불러오기, 크롤러, 캐시 등에 의해 의도치 않게 호출될 수 있어 상태를 바꾸는 요청에 쓰면 위험합니다.

**확인** `curl -X DELETE http://localhost:8080/api/v1/posts/1/comments/1`


#### 037 글 작성 API — POST 와 JSON 요청 본문
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/037) · [변경 내용](https://github.com/tbafe26/REST4/commit/037)

**무엇을** `POST /api/v1/posts` 를 추가했습니다. 요청 본문은 JSON(`{"title": "...", "content": "..."}`) 이며 컨트롤러 안의 record `PostWriteForm` 으로 받습니다. `@NotBlank`, `@Size` 로 검증합니다.

**왜** 데이터를 새로 만드는 요청은 POST 를 쓰고, REST API 에서는 폼 파라미터 대신 **JSON 본문**으로 주고받는 것이 일반적입니다. `@RequestBody` 가 JSON 을 객체로 바꿔 주고 `@Valid` 가 제약 조건을 검사합니다.

```java
@PostMapping
@Transactional
public RsData<PostDto> write(@RequestBody @Valid PostWriteForm form) {
    Post post = postService.write(form.title, form.content);
    return new RsData<>("200-1", "%d번 글이 작성되었습니다.".formatted(post.getId()), new PostDto(post));
}
```

**확인** `curl -X POST http://localhost:8080/api/v1/posts -H "Content-Type: application/json" -d '{"title":"제목","content":"내용"}'`


#### 038 요청 본문 record 이름을 PostWriteReqBody 로
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/038) · [변경 내용](https://github.com/tbafe26/REST4/commit/038)

**무엇을** `PostWriteForm` 을 `PostWriteReqBody` 로, 매개변수 `form` 을 `reqBody` 로 바꿨습니다.

**왜** REST API 에는 HTML 폼이 없습니다. 이 객체는 **요청 본문(Request Body)** 을 담는 것이므로 이름도 그 역할을 드러내게 합니다. 이후 응답 본문 전용 객체는 `...ResBody` 로 짝을 맞춥니다.

**확인** 동작은 이전과 같습니다.


#### 039 글 작성 응답에 전체 글 수 추가 — List
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/039) · [변경 내용](https://github.com/tbafe26/REST4/commit/039)

**무엇을** 글 작성 후 전체 글 수(`totalCount`)도 함께 달라는 요구에 맞춰 `data` 를 `List.of(totalCount, new PostDto(post))` 로 바꿨습니다. 반환 타입은 `RsData<List<Object>>` 입니다.

**왜** `RsData.data` 에는 값을 하나만 담을 수 있으므로 여러 값을 담으려면 묶어야 합니다. 가장 먼저 떠오르는 방법인 List 로 시도해 봅니다. 결과 JSON 은 `[3, {...}]` 처럼 **각 값이 무엇인지 이름이 없습니다.**

**확인** `data` 가 `[4, {"id":4, ...}]` 형태의 배열로 나옵니다.


#### 040 글 작성 응답 data 를 Map 으로
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/040) · [변경 내용](https://github.com/tbafe26/REST4/commit/040)

**무엇을** `data` 를 `Map.of("totalCount", totalCount, "post", new PostDto(post))` 로 바꿨습니다.

**왜** Map 은 키가 있어 JSON 이 `{"totalCount": 4, "post": {...}}` 처럼 읽기 쉽습니다. 다만 키는 문자열이라 오타를 컴파일러가 잡지 못하고, 반환 타입 `Map<String, Object>` 만 보고는 어떤 키가 오는지 알 수 없습니다.

**확인** `data` 가 `{"totalCount": ..., "post": {...}}` 로 나옵니다.


#### 041 글 작성 전용 응답 본문 record PostWriteResBody
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/041) · [변경 내용](https://github.com/tbafe26/REST4/commit/041)

**무엇을** 컨트롤러 안에 `record PostWriteResBody(long totalCount, PostDto post)` 를 만들고 반환 타입을 `RsData<PostWriteResBody>` 로 바꿨습니다.

**왜** 해당 액션 전용 응답 타입을 두면 JSON 결과는 Map 과 같으면서도 필드 이름과 타입이 컴파일 시점에 고정되고, 반환 타입만으로 응답 구조를 알 수 있습니다. 요청은 `...ReqBody`, 응답은 `...ResBody` 로 짝을 맞춥니다.

```java
public record PostWriteResBody(long totalCount, PostDto post) { }
```

**확인** 응답 JSON 은 040 과 같습니다.


#### 042 글 작성 응답 생성 코드 간소화
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/042) · [변경 내용](https://github.com/tbafe26/REST4/commit/042)

**무엇을** `PostWriteResBody data = ...` 지역 변수를 없애고 `RsData` 생성자 안에서 바로 만들었습니다.

**왜** 한 번만 쓰는 변수는 줄이고 응답 구조가 코드 모양에 그대로 드러나게 합니다. 동작은 같습니다.

**확인** 응답 JSON 은 이전과 같습니다.


#### 043 글 작성 resultCode 를 201-1 로
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/043) · [변경 내용](https://github.com/tbafe26/REST4/commit/043)

**무엇을** 글 작성 성공의 `resultCode` 를 `200-1` 에서 `201-1` 로 바꿨습니다.

**왜** 201(Created)은 "성공했고, 그 결과 무언가가 새로 생성됐다"는 뜻으로 작성 성공에 가장 정확한 코드입니다. 그런데 `resultCode` 는 응답 본문 안의 문자열일 뿐이라 **실제 HTTP 응답 상태 코드는 여전히 200** 입니다.

> **확인해 볼 것** `curl -i -X POST ...` 로 응답 헤더를 보면 상태 줄은 `200` 인데 본문의 `resultCode` 만 `201-1` 입니다.


#### 044 ResponseEntity 로 HTTP 상태 코드 201 설정
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/044) · [변경 내용](https://github.com/tbafe26/REST4/commit/044)

**무엇을** 글 작성 반환 타입을 `ResponseEntity<RsData<PostWriteResBody>>` 로 감싸고 `HttpStatus.CREATED` 를 지정했습니다.

**왜** 실제 HTTP 상태 코드를 바꾸려면 응답 자체(상태 코드, 헤더, 본문)를 다루는 `ResponseEntity` 가 필요합니다. 동작은 하지만 반환 타입과 생성 코드가 한 겹 더 감싸져 장황해집니다. 상태 코드 하나 바꾸려고 모든 액션을 이렇게 쓰는 것은 부담입니다.

```java
return new ResponseEntity<>(new RsData<>("201-1", ...), HttpStatus.CREATED);
```

**확인** `curl -i -X POST ...` 의 상태 줄이 `201` 입니다.


#### 045 ResponseEntity 제거
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/045) · [변경 내용](https://github.com/tbafe26/REST4/commit/045)

**무엇을** 글 작성을 다시 `RsData<PostWriteResBody>` 를 반환하도록 되돌렸습니다.

**왜** 오직 상태 코드를 바꾸기 위해 모든 액션에 `ResponseEntity` 를 두르는 것은 비용 대비 효과가 낮습니다. 이미 `resultCode` 앞자리(`201`)에 원하는 상태 코드가 들어 있으니, 이를 보고 **한 곳에서 자동으로** 상태 코드를 바꾸는 방법을 다음 단계에서 적용합니다. (지금은 다시 200 으로 응답합니다.)

**확인** 반환 타입이 다시 `RsData<PostWriteResBody>` 입니다.


#### 046 ResponseAspect — resultCode 로 HTTP 상태 코드 자동 설정
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/046) · [변경 내용](https://github.com/tbafe26/REST4/commit/046)

**무엇을** `global/aspect/ResponseAspect` 를 추가했습니다. `@RestController` 의 매핑 메서드가 `RsData` 를 반환하면, 그 `statusCode` 로 `HttpServletResponse` 의 상태 코드를 설정합니다. `RsData` 에는 `resultCode` 앞자리를 숫자로 바꾼 `statusCode` 필드를 추가했습니다.

**왜** 모든 액션에 같은 후처리(상태 코드 설정)가 필요할 때, 각 메서드를 고치는 대신 AOP 로 **한 곳에서** 처리합니다. 액션 메서드는 `RsData` 만 반환하면 됩니다.

```java
@Around("within(@RestController *) && (@annotation(GetMapping) || ... )")
public Object handleResponse(ProceedingJoinPoint joinPoint) throws Throwable {
    Object proceed = joinPoint.proceed();
    if (proceed instanceof RsData<?> rsData) response.setStatus(rsData.statusCode());
    return proceed;
}
```

```java
public RsData(String resultCode, String msg, T data) {
    this(resultCode, Integer.parseInt(resultCode.split("-", 2)[0]), msg, data);
}
```

> **확인해 볼 것** `curl -i -X POST ...` 의 상태 줄이 `201` 입니다. 응답 JSON 에 `statusCode` 도 함께 노출되는데, 다음 단계에서 숨깁니다.


#### 047 글 수정 API — PUT, statusCode 는 응답에서 숨김
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/047) · [변경 내용](https://github.com/tbafe26/REST4/commit/047)

**무엇을** `PUT /api/v1/posts/{id}` 로 글을 수정합니다. 요청 본문은 `PostModifyReqBody`(title, content) 이고 응답은 `RsData<Void>`(`200-1`) 입니다. `RsData.statusCode` 에는 `@JsonIgnore` 를 붙였습니다.

**왜** 수정은 리소스를 갱신하는 PUT 이 맞습니다. 엔티티 값을 바꾸면 트랜잭션 커밋 시 더티체킹으로 UPDATE 가 나갑니다. `statusCode` 는 HTTP 상태 줄로 이미 전달되므로 본문에 중복해서 노출하지 않습니다.

**확인** `curl -X PUT http://localhost:8080/api/v1/posts/1 -H "Content-Type: application/json" -d '{"title":"제목new","content":"내용new"}'`
