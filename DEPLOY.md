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
