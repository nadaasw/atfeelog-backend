# 배포 준비 현황

## 완료된 것들

- [x] GitHub 레포지토리 생성 및 push (https://github.com/nadaasw/atfeelog-backend)
- [x] `application.yml` gitignore 처리
- [x] `application.yml.example` 생성 (환경변수 방식)
- [x] `.env` 생성 (gitignore 처리)
- [x] `.env.example` 생성
- [x] `Dockerfile` 생성 (2단계 빌드)
- [x] `docker-compose.yml` 수정 (mysql, redis, app 포함)
- [x] `.github/workflows/deploy.yml` 생성

## 남은 것들

- [ ] Oracle Cloud VM 생성 (자리 날 때까지 대기)
- [ ] VM에 Docker 설치
- [ ] VM에서 레포 clone 후 `application.yml` 세팅
- [ ] GitHub Secrets 등록
- [ ] 첫 배포 테스트

## GitHub Secrets 등록 목록

| 키 | 설명 |
|---|---|
| `DOCKER_USERNAME` | nadaa97 |
| `DOCKER_PASSWORD` | Docker Hub 비밀번호 |
| `VM_HOST` | Oracle VM IP 주소 |
| `VM_SSH_KEY` | VM SSH 개인키 |
| `JWT_ISSUER` | JWT 발급자 |
| `JWT_SECRET_KEY` | JWT 시크릿 키 |
| `AWS_S3_BUCKET` | S3 버킷 이름 |
| `AWS_ACCESS_KEY` | AWS 액세스 키 |
| `AWS_SECRET_KEY` | AWS 시크릿 키 |

## VM 세팅 순서 (자리 생기면)

1. Oracle Cloud 콘솔에서 VM 생성
2. SSH 접속
3. Docker 설치
   ```bash
   sudo apt-get update
   sudo apt-get install docker.io docker-compose -y
   sudo usermod -aG docker ubuntu
   ```
4. 레포 clone
   ```bash
   git clone https://github.com/nadaasw/atfeelog-backend.git ~/atfeelog-backend
   ```
5. GitHub Secrets 등록
6. main 브랜치에 push → 자동 배포

## 배포 흐름

```
git push (main)
→ GitHub Actions 실행
→ Docker 이미지 빌드
→ Docker Hub (nadaa97/atfeelog-backend) push
→ VM SSH 접속
→ application.yml 생성
→ .env 생성
→ docker-compose up -d
```
