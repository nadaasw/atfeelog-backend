# 배포 및 CI/CD 가이드

## 현재 상태

- [x] GitHub 레포지토리 생성 및 push
- [x] `application.yml` gitignore 처리
- [x] `Dockerfile` 생성 (2단계 빌드)
- [x] `docker-compose.yml` 수정 (mysql, redis, app 포함)
- [x] `.github/workflows/deploy.yml` 생성
- [x] `docker-compose.yml` - app 서비스에 `image` 필드 추가 (Docker Hub 이미지 pull 가능하도록 수정)
- [ ] Oracle Cloud VM 생성
- [ ] VM에 Docker 설치
- [ ] GitHub Secrets 등록
- [ ] 첫 배포 테스트

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
     │                  │                       │                      │  application.yml 생성│
     │                  │                       │                      │  docker-compose pull │
     │                  │                       │                      │  docker-compose up   │
```

`main` 브랜치에 push 한 번으로 위 과정이 자동 실행됩니다.

---

## 각 단계 상세 설명

### 1단계: 트리거

```yaml
on:
  push:
    branches:
      - main
```

`main` 브랜치에 push하는 순간 GitHub이 워크플로우를 자동 실행합니다.
GitHub이 `ubuntu-latest` 임시 서버를 띄워서 거기서 작업을 진행합니다.

---

### 2단계: 코드 checkout

```yaml
uses: actions/checkout@v3
```

GitHub Actions 임시 서버에 레포지토리 코드를 가져옵니다. `Dockerfile`도 이 시점에 함께 받아집니다.

---

### 3단계: Docker 이미지 빌드 & push

```yaml
docker build -t nadaa97/atfeelog-backend:latest .
docker push nadaa97/atfeelog-backend:latest
```

`Dockerfile`을 읽어서 Spring 앱 이미지를 만들고 Docker Hub에 올립니다.

**Dockerfile 내부에서 일어나는 일:**

```dockerfile
FROM gradle:8.5-jdk17 AS build   # Gradle + JDK17 환경 준비
COPY . .                          # 소스코드 복사
RUN gradle bootJar                # .jar 파일로 빌드 (빌드 결과물 생성)

FROM openjdk:17-jdk-slim          # 실행용 가벼운 JDK 환경
COPY --from=build .../app.jar .   # 빌드된 .jar만 복사 (소스코드 제외)
ENTRYPOINT ["java", "-jar", ...]  # 실행 명령
```

2단계로 나누는 이유: 빌드 도구(Gradle)는 실행 이미지에 필요 없어서 최종 이미지를 가볍게 만들기 위함입니다.

---

### 4단계: VM에 application.yml 생성

```yaml
uses: appleboy/ssh-action@v1
host: ${{ secrets.VM_HOST }}
key: ${{ secrets.VM_SSH_KEY }}
```

Oracle Cloud VM에 SSH로 접속해서 `application.yml`을 생성합니다.
JWT, AWS 키 등 민감한 정보는 GitHub Secrets에 저장해두고 여기서 꺼내 씁니다.
코드에는 `${{ secrets.JWT_SECRET_KEY }}` 형태로만 존재하고, 실제 값은 GitHub만 알고 있습니다.

---

### 5단계: VM에 배포

```yaml
git pull origin main         # docker-compose.yml 최신화
docker-compose pull app      # Docker Hub에서 새 Spring 이미지 받기
docker-compose up -d         # 3개 컨테이너 실행 (백그라운드)
```

**docker-compose.yml이 실행하는 컨테이너:**

| 서비스 | 이미지 출처 | 역할 |
|--------|------------|------|
| `mysql` | Docker Hub 공식 이미지 | 데이터베이스 |
| `redis` | Docker Hub 공식 이미지 | 캐시 |
| `app` | `nadaa97/atfeelog-backend:latest` | Spring 앱 |

`app`은 `mysql`이 healthy 상태가 된 후 실행됩니다 (`depends_on` 설정).

---

## 역할 분리 정리

| 파일 | 역할 | 사용 시점 |
|------|------|----------|
| `Dockerfile` | Spring 앱을 이미지로 만드는 설계도 | GitHub Actions에서 빌드할 때 |
| `docker-compose.yml` | MySQL + Redis + Spring을 같이 띄우는 설정 | VM에서 실행할 때 |
| `deploy.yml` | CI/CD 자동화 스크립트 | main push 시 자동 |

**로컬 개발 시**: Spring은 `./gradlew bootRun`, MySQL/Redis만 docker-compose로 띄움
**CI/CD 시**: Dockerfile로 이미지 빌드 → Docker Hub push
**VM 운영 시**: docker-compose로 전체 컨테이너 실행

---

## GitHub Secrets 등록 목록

GitHub 레포 → Settings → Secrets and variables → Actions 에서 등록

| 키 | 설명 |
|----|------|
| `DOCKER_USERNAME` | Docker Hub 아이디 (nadaa97) |
| `DOCKER_PASSWORD` | Docker Hub 비밀번호 |
| `VM_HOST` | Oracle Cloud VM IP 주소 |
| `VM_SSH_KEY` | VM SSH 개인키 |
| `JWT_ISSUER` | JWT 발급자 |
| `JWT_SECRET_KEY` | JWT 시크릿 키 |
| `AWS_S3_BUCKET` | S3 버킷 이름 |
| `AWS_ACCESS_KEY` | AWS 액세스 키 |
| `AWS_SECRET_KEY` | AWS 시크릿 키 |

---

## VM 최초 세팅 순서 (Oracle Cloud VM 생성 후)

```bash
# 1. Docker 설치
sudo apt update
sudo apt install -y docker.io docker-compose-plugin

# 2. ubuntu 유저가 sudo 없이 docker 사용할 수 있게
sudo usermod -aG docker ubuntu
newgrp docker

# 3. 레포 클론 (최초 1회)
git clone https://github.com/nadaasw/atfeelog-backend.git ~/atfeelog-backend
```

이후부터는 main에 push할 때마다 GitHub Actions가 자동 배포합니다.

---

## Oracle Cloud VM 보안 그룹에서 열어야 할 포트

| 포트 | 용도 |
|------|------|
| `22` | SSH (GitHub Actions 접속용) |
| `8080` | Spring 앱 |

---

## 현재 안 되는 이유

```
✅ 1단계: 트리거 (작동)
✅ 2단계: 코드 checkout (작동)
✅ 3단계: Docker 빌드 & push (DOCKER_USERNAME/PASSWORD Secrets 설정 시 작동)
❌ 4단계: application.yml 생성 → VM이 없어서 SSH 실패
❌ 5단계: 배포 → VM이 없어서 SSH 실패
```

VM을 생성하고 GitHub Secrets를 등록하면 전체 파이프라인이 동작합니다.
