# REST4 — 스프링부트 REST API by TDD

글/댓글 REST API를 처음부터 만들고, 테스트 코드로 다듬고, API 문서를 자동 생성한 뒤, 회원과 작성자 권한까지 TDD로 구현해 나가는 학습용 저장소입니다.

- 모든 커밋에는 `001`, `002` … 처럼 **순번 태그**가 붙어 있습니다.
- 커밋 메세지에는 **무엇을 / 왜 / 확인** 이 자세히 적혀 있습니다.
- 아래 작업내역을 위에서부터 따라 읽으면 됩니다.

## 목차
- [기술 스택과 실행 방법](#기술-스택과-실행-방법)
- [커밋 따라가는 방법](#커밋-따라가는-방법)
- [작업내역](#작업내역)

## 기술 스택과 실행 방법

> 프로젝트 생성 이후 단계에서 채워집니다.

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

## 작업내역

### 001 저장소 준비
[이 시점의 코드](https://github.com/tbafe26/REST4/tree/001) · [변경 내용](https://github.com/tbafe26/REST4/commit/001)

**무엇을** README 뼈대, `.gitignore`, `.gitattributes` 를 만들었습니다.

**왜** 단계마다 "무엇을 왜 바꿨는지"를 README에 쌓아 가는 학습 기록이므로 그 틀을 먼저 세웁니다. `.gitattributes` 로 `gradlew`(LF)와 `gradlew.bat`(CRLF)의 줄바꿈을 고정해 OS가 달라도 실행 스크립트가 깨지지 않게 합니다.
