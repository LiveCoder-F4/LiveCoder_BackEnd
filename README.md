# ☕ LiveCoder Backend

> **안정적인 코딩 테스트 환경과 실시간 협업을 지원하는 RESTful API 서버**

LiveCoder의 백엔드 저장소입니다.
**Java 21** 및 **Spring Boot 3.4**를 기반으로 하며, 도메인 간의 높은 응집도와 유지보수성을 위해 **도메인형 패키지 구조(Package by Feature)**를 따릅니다.

<br/>

## Tech Stack

| 분류 | 기술 | 비고 |
| :--- | :--- | :--- |
| **Language** | ![Java](https://img.shields.io/badge/Java-21-ED8B00) | LTS Version |
| **Framework** | ![SpringBoot](https://img.shields.io/badge/SpringBoot-3.4-6DB33F) | Web Application Framework |
| **Build** | ![Gradle](https://img.shields.io/badge/Gradle-8.5-02303A) | 빌드 도구 |
| **Database** | ![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1) ![Redis](https://img.shields.io/badge/Redis-7.2-DC382D) | RDB (메인) / In-memory (캐싱) |
| **ORM** | **Spring Data JPA**, **QueryDSL** | 객체-관계 매핑 및 동적 쿼리 |
| **Security** | **Spring Security**, **JWT** | 인증/인가 및 토큰 관리 |
| **Docs** | **Swagger (SpringDoc)** | API 명세서 자동화 |
| **Infra** | **Docker**, **AWS (EC2, S3)** | 배포 및 스토리지 |
| **Test** | JUnit5, Mockito | 단위/통합 테스트 |

<br/>

## ⚡ Getting Started (설치 및 실행)

이 프로젝트는 **JDK 21** 이상 환경이 필요합니다.
```bash
java -version
```

### 1. 프로젝트 클론
```bash
git clone git@github.com:LiveCoder-Team/LiveCoder-Backend.git
cd LiveCoder-Backend
```

### 2. 환경 변수 설정 (yml)
`src/main/resources` 디렉토리에 `application-secret.yml` 파일을 생성하고, 팀 노션(Notion)에 공유된 DB 접속 정보 및 키 값을 입력하세요.

*(예시)*
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/livecoder
    username: root
    password: your_password
jwt:
  secret: your_jwt_secret_key
```

### 3. 빌드 및 실행
**Mac/Linux**
```bash
./gradlew clean build
./gradlew bootRun
```
**Windows**
```bash
gradlew clean build
gradlew bootRun
```

서버 실행 후 `http://localhost:8080/swagger-ui/index.html`에서 API 명세를 확인할 수 있습니다.

<br/>

## 📂 Project Structure (폴더 구조)

우리는 **도메인(기능) 중심의 패키지 구조**를 사용합니다.
관련된 로직(Controller, Service, Repository, Entity)이 하나의 패키지에 모여 있어 코드 탐색이 직관적입니다.

```text
src/main/java/com/livecoder/
├── global/                # 전역 공통 설정
│   ├── config/            # Security, Swagger, WebMvc 설정
│   ├── error/             # GlobalExceptionHandler, ErrorCode
│   └── util/              # 암호화, 날짜 계산 등 유틸리티
│
└── domain/                # ★ 핵심: 기능 단위 패키징
    ├── auth/              # 인증/인가 도메인
    │   ├── api/           # AuthController
    │   ├── application/   # AuthService
    │   ├── dao/           # AuthRepository
    │   ├── domain/        # User (Entity)
    │   └── dto/           # LoginReq, TokenRes
    │
    ├── problem/           # 문제 풀이 도메인
    │   ├── api/           # ProblemController
    │   ├── application/   # ProblemService
    │   ├── dao/           # ProblemRepository
    │   └── domain/        # Problem, TestCase
    │
    ├── submission/        # 채점 및 제출 도메인
    │   ├── api/
    │   ├── application/   # GradingService (채점 로직)
    │   └── ...
    │
    └── social/            # 친구 및 커뮤니티 도메인
        └── ...
```

<br/>

## 🤝 Contribution Guide (협업 규칙)

### 1. Git Flow 및 브랜치 전략
* `main`: 배포 가능한 안정 버전
* `develop`: 개발 중인 코드 (PR 대상)

**브랜치 명명 규칙: `타입/기능명_작성자`**
누가 작업 중인지 명확히 알기 위해 **기능명 뒤에 작성자 이름(이니셜)**을 붙입니다.

| 타입 | 설명 | 사용 예시 |
| :--- | :--- | :--- |
| `feat` | 새로운 기능 추가 | `feat/oauth-login_Elric(본인 닉네임)` |
| `fix` | 버그 수정 | `fix/npe-error_Elric(본인 닉네임)` |
| `refactor` | 코드 리팩토링 | `refactor/querydsl_Elric(본인 닉네임)` |
| `test` | 테스트 코드 추가 | `test/service-test_Elric(본인 닉네임)` |
| `docs` | 문서 수정 | `docs/readme_Elric(본인 닉네임)` |

### 2. Commit Convention
커밋 메시지는 **Conventional Commits** 규칙을 따릅니다.

* `feat: 회원가입 API 구현`
* `fix: JWT 토큰 만료 시간 수정`
* `chore: 의존성 라이브러리 버전 업데이트`

### 3. Code Quality (PR 전 필독!)

안정적인 서버 운영을 위해 PR(Pull Request) 전, 로컬에서 **테스트**를 반드시 수행해야 합니다.

**PR 전 실행 명령어**
```bash
./gradlew test
```
모든 테스트가 통과(BUILD SUCCESS)했는지 확인 후 푸시해 주세요.

<br/>

## ⚠️ Troubleshooting

**Q. `./gradlew` 실행 시 `Permission denied`가 떠요.**
<br/>
A. 실행 권한이 없어서 그렇습니다. `chmod +x gradlew` 명령어를 입력해 주세요.

**Q. DB 연결 에러가 발생해요.**
<br/>
A. 로컬에 MySQL이 켜져 있는지 확인하고, `application-secret.yml`의 계정 정보가 맞는지 확인하세요.

**Q. 포트가 이미 사용 중이라고 나와요. (Port 8080 was already in use)**
<br/>
A. 이미 다른 프로세스가 8080 포트를 점유 중입니다. 터미널에서 해당 프로세스를 종료하거나, `application.yml`에서 `server.port`를 변경하세요.
