# atfeelog-backend

공연/콘서트 후기 공유 커뮤니티 **atfeelog**의 백엔드 서버입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 4.0.5**
- **GraphQL** (Spring for GraphQL)
- **MySQL 8.0**
- **Redis 7**
- **AWS S3** (이미지 업로드)
- **JWT** (Access Token + Refresh Token)
- **Spring Security**

## 주요 기능

- 회원가입 / 로그인 / 로그아웃
- JWT 기반 인증 (Access Token 1시간, Refresh Token 14일)
- 게시글 CRUD (공연 후기 작성/수정/삭제)
- 게시글 좋아요 / 베스트 게시글 조회
- 댓글 CRUD
- 이미지 업로드 (AWS S3)
- 게시글 검색 및 페이징
- 인기 키워드 조회
- AOP 기반 메서드 실행 로깅

## 프로젝트 구조

```
src/main/java/hello/atfeelogbackend/
├── domain/
│   ├── board/          # 게시글, 댓글, 좋아요
│   ├── filemanager/    # 파일 업로드 (S3)
│   ├── refreshToken/   # Refresh Token 관리
│   └── user/           # 회원 관리
└── global/
    ├── aop/            # AOP 로깅
    ├── auth/           # JWT 인증
    ├── config/         # Security, GraphQL 설정
    ├── cookie/         # 쿠키 유틸
    ├── exception/      # 전역 예외 처리
    └── redis/          # Redis 서비스
```

## 로컬 실행 방법

### 1. 사전 요구사항

- Java 17
- Docker

### 2. MySQL, Redis 실행

```bash
docker-compose up -d
```

### 3. application.yml 설정

`src/main/resources/application.yml` 파일을 생성하고 아래 내용을 채워주세요.

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  datasource:
    url: jdbc:mysql://localhost:3306/atfeelog?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
    username: root
    password: 1234
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  graphql:
    graphiql:
      enabled: true
    path: /graphql

jwt:
  issuer: your_issuer
  secret_key: your_secret_key_must_be_at_least_32_characters

cloud:
  aws:
    s3:
      bucket: your_bucket_name
    credentials:
      access-key: your_access_key
      secret-key: your_secret_key
    region:
      static: ap-northeast-2
```

### 4. 서버 실행

```bash
./gradlew bootRun
```

### 5. GraphiQL 접속

```
http://localhost:8080/graphiql
```

## API

GraphQL 엔드포인트: `POST /graphql`

### 주요 Query

| Query | 설명 | 인증 필요 |
|-------|------|----------|
| `fetchUserLoggedIn` | 로그인한 유저 정보 조회 | O |
| `fetchBoard(boardId)` | 게시글 단건 조회 | X |
| `fetchBoards` | 게시글 목록 조회 (검색, 날짜 필터, 페이징) | X |
| `fetchBoardsCount` | 게시글 총 개수 조회 | X |
| `fetchBoardsOfMine` | 내 게시글 목록 | O |
| `fetchBoardsCountOfMine` | 내 게시글 수 | O |
| `fetchBoardsLike` | 좋아요한 게시글 목록 | O |
| `fetchBoardsOfBest` | 베스트 게시글 (좋아요 순 상위 5개) | X |
| `fetchBoardsKeyword` | 인기 키워드 (상위 5개) | X |
| `fetchBoardComments` | 댓글 목록 조회 | X |

### 주요 Mutation

| Mutation | 설명 | 인증 필요 |
|----------|------|----------|
| `createUser` | 회원가입 | X |
| `loginUser` | 로그인 | X |
| `logoutUser` | 로그아웃 | O |
| `restoreAccessToken` | Access Token 재발급 | X |
| `updateUser` | 회원정보 수정 | O |
| `resetUserPassword` | 비밀번호 재설정 | O |
| `createBoard` | 게시글 작성 | O |
| `updateBoard` | 게시글 수정 | O |
| `deleteBoard` | 게시글 삭제 | O |
| `deleteBoards` | 게시글 일괄 삭제 | O |
| `likeBoard` | 좋아요 토글 | O |
| `createBoardComment` | 댓글 작성 | O |
| `updateBoardComment` | 댓글 수정 | O |
| `deleteBoardComment` | 댓글 삭제 | O |
