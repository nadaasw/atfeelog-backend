# 배포 및 CI/CD 가이드

## 현재 상태

- [x] GitHub 레포지토리 생성 및 push
- [x] `application.yml` gitignore 처리
- [x] `Dockerfile` 생성 (2단계 빌드)
- [x] `docker-compose.yml` 구성 (Supabase PostgreSQL + Redis + Spring)
- [x] `.github/workflows/deploy.yml` 생성
- [x] Oracle Cloud VM 생성 및 Docker 설치
- [x] GitHub Secrets 등록
- [x] 첫 배포 완료

---

## 인프라 구성

| 역할 | 서비스 |
|------|--------|
| Spring Boot 앱 | Oracle Cloud VM (1 CPU, 1GB RAM) |
| Redis (캐시) | Oracle Cloud VM — Docker 컨테이너 |
| PostgreSQL (DB) | Supabase 외부 서비스 (무료 티어) |
| 이미지 저장소 | Docker Hub (`nadaa97/atfeelog-backend`) |

MySQL 대신 Supabase를 사용하는 이유: VM 메모리(1GB)가 MySQL + Redis + Spring을 동시에 올리기에 부족하기 때문.

---

## CI/CD 전체 흐름

```
개발자 (로컬)         GitHub               GitHub Actions          Docker Hub          Oracle Cloud VM
     │                  │                       │                      │                     │
     │── git push ──>   │                       │                      │                     │
     │                  │── 워크플로우 트리거 ──> │                      │                     │
     │                  │                       │── 1. 코드 checkout    │                     │
     │                  │                       │── 2. Docker 이미지 빌드                     │
     │                  │                       │── 3. push ─────────> │                     │
     │                  │                       │── 4. SSH 접속 ───────────────────────────> │
     │                  │                       │                      │  .env 생성           │
     │                  │                       │                      │  docker compose pull │
     │                  │                       │                      │  docker compose up   │
```

`main` 브랜치에 push 한 번으로 위 과정이 자동 실행됩니다.

---

## GitHub Secrets 등록 목록

GitHub 레포 → Settings → Secrets and variables → Actions 에서 등록

| 키 | 설명 |
|----|------|
| `DOCKER_USERNAME` | Docker Hub 아이디 (`nadaa97`) |
| `DOCKER_PASSWORD` | Docker Hub Access Token (비밀번호 대신 토큰 사용) |
| `VM_HOST` | Oracle Cloud VM 공인 IP (`152.67.209.165`) |
| `VM_SSH_KEY` | VM SSH 개인키 (`-----BEGIN...` 전체 내용) |
| `JWT_ISSUER` | JWT 발급자 |
| `JWT_SECRET_KEY` | JWT 시크릿 키 |
| `AWS_S3_BUCKET` | S3 버킷 이름 |
| `AWS_ACCESS_KEY` | AWS 액세스 키 |
| `AWS_SECRET_KEY` | AWS 시크릿 키 |
| `DB_PASSWORD` | Supabase 데이터베이스 비밀번호 |

> Docker Hub를 GitHub 계정으로 로그인한 경우 비밀번호가 없으므로 반드시 Access Token을 발급해서 사용해야 합니다.
> Account Settings → Personal access tokens → Generate new token (Read & Write)

---

## VM 최초 세팅 순서 (Oracle Cloud VM 생성 후)

```bash
# 1. SSH 접속 (로컬에서)
chmod 600 ssh-key.key
ssh -i ssh-key.key ubuntu@152.67.209.165

# 2. Docker 공식 저장소에서 설치 (apt의 기본 docker.io는 compose 플러그인 미지원)
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update && sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 3. ubuntu 유저가 sudo 없이 docker 사용
sudo usermod -aG docker ubuntu
newgrp docker

# 4. Docker 데몬 시작 및 부팅 시 자동 시작 설정
sudo systemctl start docker
sudo systemctl enable docker

# 5. 레포 클론 (최초 1회)
git clone https://github.com/nadaasw/atfeelog-backend.git ~/atfeelog-backend
```

이후부터는 main에 push할 때마다 GitHub Actions가 자동 배포합니다.

---

## 로컬에서 SSH 접속 / 로그 확인하는 법

`VM_SSH_KEY` GitHub Secret은 등록 후 값을 다시 볼 수 없음(write-only). 로컬에 Oracle Cloud 콘솔에서 인스턴스 생성 시 다운로드한 개인키 파일이 있어야 함 (`~/Downloads/ssh-key-*.key` 형태로 여러 개 쌓여있을 수 있으니 날짜로 구분).

**2026-07-22 기준 이 VM(`152.67.209.165`)에 맞는 키는 `~/Downloads/ssh-key-2026-06-30.key`.** (다른 날짜 키들은 다른 프로젝트/인스턴스용이니 헷갈리지 말 것. 권한이 0644 등으로 열려있으면 `chmod 600` 먼저 해야 ssh가 키를 사용함)

```bash
# 접속
ssh -i ~/Downloads/ssh-key-2026-06-30.key ubuntu@152.67.209.165

# 앱 로그 보기 (최근 N줄)
cd ~/atfeelog-backend
docker compose logs --tail=200 app

# 실시간 로그
docker compose logs -f app

# 에러만 필터링
docker compose logs --tail=500 app | grep -i -B3 -A15 "exception\|error"
```

> 주의: `deploy.yml`이 배포 때마다 `docker compose down && docker compose up -d`를 실행해서 컨테이너를 통째로 새로 만듦. 로깅 드라이버/볼륨 마운트 설정이 없어서(`docker-compose.yml` 기본 `json-file` 드라이버) **컨테이너가 내려가면 그 이전 로그는 사라짐**. 배포 직전 로그를 남기고 싶으면 배포 전에 미리 `docker compose logs > backup.log` 등으로 백업해둘 것.

---

## Oracle Cloud VCN 보안 규칙 (포트 개방)

OCI 콘솔 → VM → Networking 탭 → Subnet → Security List → Add Ingress Rules

| 포트 | 프로토콜 | Source CIDR | 용도 |
|------|----------|-------------|------|
| `22` | TCP | `0.0.0.0/0` | SSH (GitHub Actions 접속용) |
| `8080` | TCP | `0.0.0.0/0` | Spring 앱 |

---

## Supabase 연결 설정

Oracle Cloud VM은 IPv6 외부 연결이 안 되므로 Supabase 직접 연결(`db.xxx.supabase.co`)은 사용 불가.
대신 IPv4를 지원하는 **공유 풀러(Shared Pooler)** 를 사용.

Supabase 대시보드 → Connect → ORM 탭에서 Session mode 호스트 확인.

| 항목 | 값 |
|------|-----|
| Host | `aws-1-ap-southeast-1.pooler.supabase.com` |
| Port | `5432` |
| Database | `postgres` |
| Username | `postgres.pcsbfbstuewcnzufsxod` |
| Password | GitHub Secret `DB_PASSWORD` 참조 |

> `ddl-auto: update` 사용 시 Session mode 필수 (Transaction mode는 DDL 제한 있음)

---

## 역할 분리 정리

| 파일 | 역할 | 사용 시점 |
|------|------|----------|
| `Dockerfile` | Spring 앱을 이미지로 만드는 설계도 | GitHub Actions에서 빌드할 때 |
| `docker-compose.yml` | Redis + Spring 컨테이너 구성 및 환경변수 | VM에서 실행할 때 |
| `deploy.yml` | CI/CD 자동화 스크립트 | main push 시 자동 |

> `docker-compose.yml`의 환경변수가 실제 설정값의 주요 소스입니다.
> VM에 생성되는 `application.yml`은 Docker 컨테이너 내부에서 읽히지 않으므로 참고용입니다.

---

## 주요 트러블슈팅 기록

### 1. `openjdk:17-jdk-slim` 이미지 not found
Docker Hub에서 deprecated됨. `eclipse-temurin:17-jre-jammy`로 교체.

### 2. `docker-compose` vs `docker compose`
`apt install docker.io`로 설치 시 compose 플러그인 미포함 → Docker 공식 저장소에서 재설치 필요. `docker compose`(하이픈 없음) 사용.

### 3. Supabase IPv6 연결 실패
Oracle Cloud VM은 IPv6으로 외부 접속이 안 됨. 공유 풀러(pooler) 주소로 변경하여 해결.

### 4. `user` 테이블명 예약어 충돌
`user`는 PostgreSQL 예약어. `@Table(name = "users")`로 변경.

### 5. 환경변수 누락
`application.yml`은 Docker 컨테이너 내부에서 읽히지 않음. 아래 항목들을 모두 `docker-compose.yml`의 `environment`에 명시해야 함:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME`
- `SPRING_JPA_DATABASE_PLATFORM`
- `SPRING_JPA_HIBERNATE_DDL_AUTO`
- `SPRING_GRAPHQL_GRAPHIQL_ENABLED`
- `CLOUD_AWS_CREDENTIALS_ACCESS_KEY`
- `CLOUD_AWS_CREDENTIALS_SECRET_KEY`
- `CLOUD_AWS_REGION_STATIC`
- `CLOUD_AWS_S3_BUCKET`

### 6. `fetchBoards`/`fetchBoardsCount` 전부 "Internal Error" (2026-07-22)
`BoardRepository.searchBoards`의 `(:start IS NULL OR b.createdAt >= :start)` 패턴에서, PostgreSQL이 `:start`(OffsetDateTime)가 `IS NULL` 체크에만 쓰이는 자리에서는 타입을 추론하지 못해 `could not determine data type of parameter` (SQLState 42P18) 에러 발생. MySQL은 관대하게 넘어가던 부분이라 PostgreSQL 마이그레이션 이후 잠복해있던 버그. `AND b.createdAt >= COALESCE(:start, b.createdAt)` 형태로 바꿔서 해결. 겸사겸사 `Page<Board>` 반환용 count 쿼리도 `LEFT JOIN FETCH`가 섞여있으면 문제될 수 있어 `countQuery`를 명시적으로 분리해둠.

### 7. 사진 업로드가 "안 되는 것처럼" 보였던 사건 (2026-07-22) — 실제로는 백엔드 문제 아니었음
`/api/upload`(S3), `createBoard` 뮤테이션 모두 직접 테스트해보니 정상 동작. 로그 확인 결과 액세스 토큰이 만료(`iat`~`exp` 1시간)된 채로 요청이 들어와서 "인증 실패"가 찍힌 것이 원인으로 추정됨. 프론트가 401/인증실패 시 리프레시 토큰으로 재발급하는 로직을 제대로 타는지 프론트 쪽 확인 필요 (백엔드에 리프레시 토큰 발급 로직 자체는 존재: `TokenService.java`).

추가로 확인은 안 했지만 잠재 위험 요소로 남겨둠: `SecurityConfig.java`에 `CorsConfiguration`/`CorsConfigurationSource` import는 되어있는데 실제 `.cors(...)` 설정이나 빈 등록이 전혀 없음 (CORS 미설정). 프론트가 API와 다른 origin에서 호출하는 구조라면 브라우저에서 요청이 막힐 수 있으니 나중에 필요하면 점검할 것.

### 8. 갑자기 502 — `app` 컨테이너가 OOM killed (2026-07-23)
`docker inspect atfeelog-app`에서 `OOMKilled=true, ExitCode=137`. `dmesg`에도 `Out of memory: Killed process (java)` 기록 확인. 배포 때문이 아니라 런타임 중 리눅스 OOM killer가 자바 프로세스를 강제 종료한 것 — 컨테이너가 죽어있으니 앞단에서 502가 뜬 것. VM이 1GB RAM에 swap이 0B라서, JVM 힙에 상한이 없으면(`-Xmx` 미지정) 메모리 스파이크 시 스왑으로 버티지 못하고 바로 OOM kill로 직행함.

대응: `docker-compose.yml`에 `restart: unless-stopped`(컨테이너가 죽어도 자동 재기동), `mem_limit`(app 700m / redis 100m), `JAVA_TOOL_OPTIONS: -Xmx450m -XX:MaxMetaspaceSize=150m`(힙 상한 명시) 추가. 근본적으로는 swap 파일이 없는 게 더 위험하니, 필요하면 VM에 2GB 정도 swapfile 추가하는 것도 고려할 것 (`fallocate` + `/etc/fstab`).

무중단 복구 팁: `docker compose ps -a`로 `Exited (137)` 확인 → `docker inspect <container> --format '{{.State.OOMKilled}}'`로 OOM 여부 확정 → `docker compose up -d`로 재기동.

### 9. `/api/upload`에서 504 / socket hang up — 실제 파일 업로드 용량 제한이 10MB가 아니라 1MB였음 (2026-07-23)
`application.yml`에 `spring.servlet.multipart.max-file-size: 10MB`가 설정돼 있지만, 이 파일은 `.gitignore` 처리되어 있어서 GitHub Actions가 리포를 checkout할 때 존재하지 않고, 따라서 Docker 이미지 빌드에도 전혀 반영되지 않음. `docker-compose.yml`에도 이를 대체할 `SPRING_SERVLET_MULTIPART_*` 환경변수가 없었기 때문에, 실제 운영 컨테이너는 Spring Boot 기본값인 **1MB** 제한으로 동작 중이었음 (10MB 설정은 한 번도 적용된 적 없음).

VM에서 8MB 파일로 직접 재현: `MaxUploadSizeExceededException: Maximum upload size exceeded` 로그 확인. 폰 사진(보통 2~10MB)은 대부분 이 1MB 한도에 걸려 실패하고, 프론트 프록시 쪽에서는 이게 504/`socket hang up`(ECONNRESET)으로 보임.

대응: `docker-compose.yml`의 `app.environment`에 `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE`, `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE`를 명시적으로 추가. **교훈: `application.yml`이 gitignore되어 빌드에 포함되지 않는 구조이므로, `docker-compose.yml`의 environment가 실제 설정의 유일한 소스라는 걸 항상 전제하고 새 설정을 추가할 것.**
