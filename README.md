# Demo SSR v1.1

Spring Boot 기반의 서버 사이드 렌더링(SSR) 웹 애플리케이션입니다. 전통적인 MVC 아키텍처를 기반으로 서버에서 HTML을 렌더링하여 클라이언트에 전달하는 방식으로 구현되었습니다. 게시판, 사용자 인증, 댓글, 유료 게시글 구매 시스템 등 다양한 기능을 포함하고 있으며, 실무에서 사용되는 주요 패턴과 최적화 기법을 적용했습니다.

## 프로젝트 개요

이 프로젝트는 Spring Boot의 서버 사이드 렌더링 방식을 활용하여 구현된 웹 애플리케이션입니다. Mustache 템플릿 엔진을 사용하여 동적 HTML을 생성하고, JPA를 통해 데이터베이스와 상호작용합니다. 세션 기반 인증, 인터셉터를 통한 인가 처리, 예외 처리, 파일 업로드 등 웹 애플리케이션의 핵심 기능들을 포함하고 있습니다.

### 주요 특징

- **서버 사이드 렌더링**: Mustache 템플릿을 활용한 전통적인 SSR 방식
- **계층형 아키텍처**: Controller-Service-Repository 계층 분리
- **인증 및 인가**: 세션 기반 인증과 인터셉터를 통한 권한 관리
- **이메일 인증**: 회원가입 시 이메일 인증번호 발송 및 검증 기능
- **OAuth 연동**: 카카오 소셜 로그인 지원
- **유료 콘텐츠 시스템**: 포인트 기반 게시글 구매 기능
- **REST API**: 이메일 인증, 포인트 충전 등 RESTful API 제공
- **성능 최적화**: 페이징, 배치 페치, 읽기 전용 트랜잭션 등 적용
- **예외 처리**: 전역 예외 처리 및 사용자 친화적 에러 페이지

## 기술 스택

### Backend
- **Java 17**: 최신 LTS 버전의 Java 사용
- **Spring Boot 3.4.12**: 최신 Spring Boot 프레임워크
- **Spring Data JPA**: ORM을 통한 데이터베이스 추상화
- **Spring Security Crypto**: BCrypt를 통한 비밀번호 암호화
- **Spring Mail**: SMTP를 통한 이메일 발송 기능
- **Mustache**: 서버 사이드 템플릿 엔진
- **Spring Web**: MVC 패턴 기반 웹 애플리케이션

### Database
- **MySQL 8.0**: 운영 데이터베이스
- **H2 Database**: 개발 및 테스트용 인메모리 데이터베이스

### 빌드 및 도구
- **Gradle**: 빌드 자동화 도구
- **Lombok**: 보일러플레이트 코드 제거
- **Spring Boot DevTools**: 개발 생산성 향상

### 보안
- **BCrypt**: 단방향 해시 알고리즘을 통한 비밀번호 암호화
- **세션 기반 인증**: HttpSession을 활용한 사용자 인증
- **인터셉터 기반 인가**: HandlerInterceptor를 통한 권한 검사

## 주요 기능

### 사용자 관리 시스템

#### 회원가입 및 로그인
- 일반 회원가입: 사용자명, 이메일, 비밀번호 기반 회원가입
- 이메일 인증: 회원가입 시 이메일 인증번호 발송 및 검증
- 이메일 및 사용자명 중복 검사
- BCrypt를 통한 비밀번호 암호화 저장
- 로그인 시 세션 기반 인증 처리

#### 소셜 로그인 (OAuth 2.0)
- 카카오 소셜 로그인 연동
- OAuth 인가 코드를 통한 액세스 토큰 발급
- 카카오 프로필 정보 조회 및 자동 회원가입
- 소셜 로그인 사용자 프로필 이미지 자동 연동

#### 프로필 관리
- 프로필 이미지 업로드 (최대 10MB)
- UUID 기반 고유 파일명 생성으로 파일 충돌 방지
- 로컬 파일 시스템 저장 및 웹 접근 경로 매핑
- 프로필 이미지 삭제 기능
- 소셜 로그인 사용자의 외부 이미지 URL 지원

#### 회원 정보 관리
- 회원 정보 수정 (비밀번호, 프로필 이미지)
- 더티 체킹을 통한 효율적인 엔티티 업데이트
- 세션 정보 자동 갱신

#### 포인트 시스템
- 포인트 충전 기능 (REST API 제공)
- 포인트 차감 기능 (유료 게시글 구매 시)
- 포인트 부족 시 예외 처리
- 실시간 포인트 조회 및 갱신

#### 이메일 인증 시스템
- 회원가입 시 이메일 인증번호 발송 (REST API)
- 6자리 랜덤 인증번호 생성 (100000 ~ 999999)
- 세션 기반 인증번호 저장 및 검증
- 이메일 주소별 독립적인 인증번호 관리
- HTML 형식의 이메일 발송 (MimeMessage 사용)
- 인증 완료 후 이메일 수정 불가 처리 (readOnly 속성)
- JavaMailSender를 통한 SMTP 메일 발송
- 인증번호 확인 후 세션에서 자동 제거

#### 역할 기반 접근 제어 (RBAC)
- ADMIN, USER 역할 구분
- 사용자별 다중 역할 지원
- 역할 기반 기능 접근 제어

### 게시판 시스템

#### 게시글 CRUD
- 게시글 작성: 제목, 내용, 유료 여부 설정
- 게시글 수정: 소유자만 수정 가능 (인가 검사)
- 게시글 삭제: 연관된 댓글 자동 삭제 후 게시글 삭제 (CASCADE 처리)
- 게시글 상세 조회: 작성자 정보, 구매 여부 포함

#### 페이징 및 검색
- Spring Data JPA의 Pageable을 활용한 페이징 처리
- 페이지 크기 제한 (최소 1, 최대 50)
- 제목 및 내용 기반 검색 기능
- 대소문자 구분 없는 검색 (LIKE 쿼리)
- 생성일 기준 내림차순 정렬

#### 유료 게시글 시스템
- 프리미엄 게시글 설정 기능
- 유료 게시글 구매 전 내용 일부만 표시
- 구매 후 전체 내용 열람 가능
- 게시글 작성자는 자신의 게시글 구매 불가

#### 인가 처리
- 게시글 수정/삭제 시 소유자 확인
- 엔티티 레벨에서 isOwner() 메서드를 통한 인가 검사

### 댓글 시스템

#### 댓글 기능
- 댓글 작성: 게시글에 댓글 작성 (최대 500자)
- 댓글 삭제: 댓글 소유자만 삭제 가능
- 댓글 목록 조회: JOIN FETCH를 통한 N+1 문제 해결
- 댓글 작성자 정보 표시

#### 성능 최적화
- JOIN FETCH를 통한 User 엔티티 한 번에 조회
- OSIV(Open Session in View) 비활성화 대응을 위한 DTO 변환

### 구매 시스템

#### 구매 프로세스
- 유료 게시글 구매 요청
- 포인트 충분 여부 확인
- 포인트 차감 처리 (트랜잭션 내에서 원자적 처리)
- 구매 내역 저장
- 중복 구매 방지 (구매 이력 확인)

#### 비즈니스 로직
- 게시글 작성자는 자신의 게시글 구매 불가
- 무료 게시글은 구매 불가
- 포인트 부족 시 예외 처리
- 구매 여부 확인 기능 (게시글 상세 조회 시)

### 관리자 기능

#### 관리자 대시보드
- 관리자 전용 페이지 접근
- AdminInterceptor를 통한 관리자 권한 검사
- 역할 기반 접근 제어

### 보안 및 인증 시스템

#### 인증 메커니즘
- 세션 기반 인증: HttpSession을 통한 사용자 정보 저장
- LoginInterceptor: 로그인 필수 페이지 접근 제어
- SessionInterceptor: 세션 정보 전역 관리

#### 인가 메커니즘
- AdminInterceptor: 관리자 권한 검사
- 엔티티 레벨 인가: isOwner() 메서드를 통한 소유자 확인
- 컨트롤러 레벨 인가: Service 계층에서 권한 검사

#### 비밀번호 보안
- BCrypt 해싱 알고리즘 사용
- 단방향 암호화로 원본 비밀번호 복구 불가
- 로그인 시 matches() 메서드를 통한 비밀번호 검증

#### 예외 처리
- 커스텀 예외 클래스: Exception400, Exception401, Exception403, Exception404, Exception500
- @ControllerAdvice를 통한 전역 예외 처리
- HTTP 상태 코드에 맞는 에러 페이지 제공
- 사용자 친화적인 에러 메시지 표시

## 프로젝트 구조

### 패키지 구조

```
src/main/java/org/example/demo_ssr_v1_1/
├── _api/                    # REST API 테스트 컨트롤러
├── _core/                   # 공통 기능 및 인프라
│   ├── config/             # 설정 클래스
│   │   └── WebMvcConfig.java      # 인터셉터, 리소스 핸들러, 비밀번호 인코더 설정
│   ├── errors/              # 예외 처리
│   │   ├── MyExceptionHandler.java    # 전역 예외 처리 핸들러
│   │   └── exception/                # 커스텀 예외 클래스들
│   ├── interceptor/         # 인터셉터
│   │   ├── LoginInterceptor.java      # 로그인 인증 인터셉터
│   │   ├── AdminInterceptor.java      # 관리자 권한 인터셉터
│   │   └── SessionInterceptor.java    # 세션 관리 인터셉터
│   └── utils/               # 유틸리티 클래스
│       ├── FileUtil.java            # 파일 업로드/삭제 유틸리티
│       └── MailUtils.java           # 이메일 인증번호 생성 유틸리티
│
├── admin/                   # 관리자 기능
│   └── AdminController.java         # 관리자 대시보드 컨트롤러
│
├── board/                   # 게시판 기능
│   ├── Board.java                  # 게시글 엔티티
│   ├── BoardController.java         # 게시글 컨트롤러
│   ├── BoardService.java            # 게시글 비즈니스 로직
│   ├── BoardRepository.java         # 게시글 데이터 접근 계층
│   ├── BoardRequest.java            # 요청 DTO
│   └── BoardResponse.java           # 응답 DTO
│
├── purchase/                # 구매 기능
│   ├── Purchase.java                # 구매 내역 엔티티
│   ├── PurchaseService.java         # 구매 비즈니스 로직
│   └── PurchaseRepository.java      # 구매 데이터 접근 계층
│
├── reply/                   # 댓글 기능
│   ├── Reply.java                   # 댓글 엔티티
│   ├── ReplyController.java         # 댓글 컨트롤러
│   ├── ReplyService.java            # 댓글 비즈니스 로직
│   ├── ReplyRepository.java         # 댓글 데이터 접근 계층
│   ├── ReplyRequest.java            # 요청 DTO
│   └── ReplyResponse.java           # 응답 DTO
│
└── user/                    # 사용자 기능
    ├── User.java                    # 사용자 엔티티
    ├── UserController.java          # 사용자 웹 컨트롤러 (SSR)
    ├── UserApiController.java       # 사용자 REST API 컨트롤러
    ├── UserService.java             # 사용자 비즈니스 로직
    ├── UserRepository.java          # 사용자 데이터 접근 계층
    ├── UserRequest.java             # 요청 DTO
    ├── UserResponse.java            # 응답 DTO
    ├── UserRole.java                # 사용자 역할 엔티티
    ├── Role.java                    # 역할 enum
    ├── OAuthProvider.java           # OAuth 제공자 enum
    └── MailService.java             # 이메일 인증 서비스
```

### 계층 구조

프로젝트는 전통적인 3계층 아키텍처를 따릅니다:

1. **표현 계층 (Presentation Layer)**
    - Controller: HTTP 요청 처리, 뷰 반환
    - DTO를 통한 데이터 전달
    - 세션 관리

2. **비즈니스 계층 (Business Layer)**
    - Service: 비즈니스 로직 처리
    - 트랜잭션 관리
    - 인가 검사
    - 예외 처리

3. **데이터 접근 계층 (Data Access Layer)**
    - Repository: JPA를 통한 데이터베이스 접근
    - 커스텀 쿼리 (JPQL, Native Query)
    - JOIN FETCH를 통한 성능 최적화

## 데이터베이스 구조

### ERD (Entity Relationship Diagram)

```
User (1) ────< (N) Board
  │
  │ (1)
  │
  └───< (N) Reply
  │
  │ (1)
  │
  └───< (N) Purchase
  │
  │ (1)
  │
  └───< (N) UserRole
```

### 주요 엔티티 상세

#### User (사용자)
- **필드**: id, username (unique), password (암호화), email (unique), point, profileImage, createdAt, provider
- **관계**:
    - OneToMany: Board, Reply, Purchase, UserRole
- **비즈니스 메서드**:
    - isOwner(): 소유자 확인
    - hasRole(): 역할 확인
    - deductPoint(): 포인트 차감
    - chargePoint(): 포인트 충전

#### Board (게시글)
- **필드**: id, title, content, premium (유료 여부), user_id (FK), createdAt
- **관계**:
    - ManyToOne: User
    - OneToMany: Reply, Purchase
- **비즈니스 메서드**:
    - isOwner(): 소유자 확인
    - update(): 게시글 수정

#### Reply (댓글)
- **필드**: id, comment (최대 500자), board_id (FK), user_id (FK), createdAt
- **관계**:
    - ManyToOne: Board, User
- **비즈니스 메서드**:
    - isOwner(): 소유자 확인

#### Purchase (구매 내역)
- **필드**: id, user_id (FK), board_id (FK), price, createdAt
- **관계**:
    - ManyToOne: User, Board
- **제약조건**:
    - 중복 구매 방지 (userId, boardId 조합)

#### UserRole (사용자 역할)
- **필드**: id, role (enum: ADMIN, USER), user_id (FK)
- **관계**:
    - ManyToOne: User
- **역할**:
    - ADMIN: 관리자 권한
    - USER: 일반 사용자 권한

### 연관관계 전략

- **LAZY 로딩**: 기본적으로 모든 연관관계는 LAZY로 설정하여 필요할 때만 조회
- **JOIN FETCH**: N+1 문제 해결을 위해 필요한 경우 JOIN FETCH 사용
- **CASCADE**: UserRole은 User 삭제 시 함께 삭제 (orphanRemoval = true)
- **외래키 제약**: 데이터 무결성 보장을 위한 외래키 제약조건 설정

## 실행 방법

### 환경 요구사항

#### 필수 소프트웨어
- **Java 17 이상**: JDK 17 이상 설치 필요
- **MySQL 8.0 이상**: 운영 데이터베이스 (또는 H2 Database로 대체 가능)
- **Gradle 7.x 이상**: 빌드 도구

#### 개발 도구 (선택사항)
- **IntelliJ IDEA** 또는 **Eclipse**: IDE
- **Postman** 또는 **curl**: API 테스트 도구

### 환경 변수 설정

프로젝트 실행 전 다음 환경 변수를 설정해야 합니다:

#### Windows (PowerShell)
```powershell
$env:KAKAO_CLIENT_ID="your_kakao_client_id"
$env:KAKAO_CLIENT_SECRET="your_kakao_client_secret"
$env:TENCO_KEY="your_tenco_key"
```

#### Linux/Mac (Bash)
```bash
export KAKAO_CLIENT_ID=your_kakao_client_id
export KAKAO_CLIENT_SECRET=your_kakao_client_secret
export TENCO_KEY=your_tenco_key
```

#### IDE에서 설정 (IntelliJ IDEA)
1. Run → Edit Configurations
2. Environment variables에 위 변수들 추가

### 데이터베이스 설정

#### MySQL 설정
1. MySQL 서버 실행
2. 데이터베이스 생성:
```sql
CREATE DATABASE myblog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. `src/main/resources/application-dev.yml` 파일에서 연결 정보 수정:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/myblog?useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

#### H2 Database 사용 (개발용)
H2를 사용하는 경우 별도 설정 없이 실행 가능합니다. H2 콘솔은 http://localhost:8080/h2-console 에서 접근 가능합니다.

### 파일 업로드 디렉토리 설정

프로필 이미지가 저장될 디렉토리를 생성해야 합니다:

#### Windows
```
D:\uploads\
```

#### Linux/Mac
`src/main/java/org/example/demo_ssr_v1_1/_core/config/WebMvcConfig.java` 파일에서 경로 수정:
```java
registry.addResourceHandler("/images/**")
    .addResourceLocations("file:///your/absolute/path/uploads/");
```

### 빌드 및 실행

#### Gradle을 통한 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# Windows의 경우
gradlew.bat build
gradlew.bat bootRun
```

#### IDE에서 실행
1. `DemoSsrV11Application.java` 파일 열기
2. main 메서드에서 실행 (Run 버튼 클릭)

#### JAR 파일로 실행
```bash
# JAR 파일 생성
./gradlew bootJar

# JAR 파일 실행
java -jar build/libs/demo_ssr_v1_1-0.0.1-SNAPSHOT.jar
```

### 접속 정보

- **애플리케이션**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:testdb`
    - Username: `sa`
    - Password: (비워두기)

### 초기 계정 정보

프로젝트 실행 시 `data.sql`을 통해 초기 데이터가 로드됩니다:

- **관리자 계정**:
    - Username: `admin`
    - Password: `1234`

- **일반 사용자 계정**:
    - Username: `ssar`, `cos`, `hong`, `kim`
    - Password: `1234`

## 주요 엔드포인트

### 사용자 관련 엔드포인트

#### 인증 및 회원가입
- `GET /join` - 회원가입 화면
    - 요구사항: 로그인하지 않은 사용자
    - 반환: 회원가입 폼 페이지

- `POST /join` - 회원가입 처리
    - 요청 데이터: username, password, email, profileImage (선택)
    - 검증: 사용자명/이메일 중복 체크, 비밀번호 암호화
    - 반환: 로그인 페이지로 리다이렉트

#### 로그인 및 로그아웃
- `GET /login` - 로그인 화면
    - 요구사항: 로그인하지 않은 사용자
    - 반환: 로그인 폼 페이지

- `POST /login` - 로그인 처리
    - 요청 데이터: username, password
    - 검증: 사용자 존재 여부, 비밀번호 일치 여부
    - 세션: sessionUser 속성에 User 객체 저장
    - 반환: 메인 페이지로 리다이렉트

- `GET /logout` - 로그아웃
    - 동작: 세션 무효화
    - 반환: 메인 페이지로 리다이렉트

#### 소셜 로그인
- `GET /user/kakao` - 카카오 소셜 로그인 콜백
    - 파라미터: code (카카오 인가 코드)
    - 처리: 액세스 토큰 발급 → 프로필 조회 → 회원가입/로그인
    - 반환: 메인 페이지로 리다이렉트

#### 사용자 정보 관리
- `GET /user/detail` - 마이페이지
    - 요구사항: 로그인 필수
    - 반환: 사용자 정보 페이지

- `GET /user/update` - 회원정보 수정 화면
    - 요구사항: 로그인 필수, 본인만 접근 가능
    - 반환: 회원정보 수정 폼 페이지

- `POST /user/update` - 회원정보 수정 처리
    - 요청 데이터: password, profileImage (선택)
    - 검증: 인가 검사, 비밀번호 암호화
    - 세션: 갱신된 사용자 정보로 세션 업데이트
    - 반환: 마이페이지로 리다이렉트

- `POST /user/profile-image/delete` - 프로필 이미지 삭제
    - 요구사항: 로그인 필수, 본인만 접근 가능
    - 동작: 파일 시스템에서 삭제, DB에서 null 처리
    - 반환: 마이페이지로 리다이렉트

### 게시판 관련 엔드포인트

#### 게시글 조회
- `GET /` 또는 `GET /board/list` - 게시글 목록 (페이징)
    - 파라미터:
        - page (기본값: 1)
        - size (기본값: 3, 최대: 50)
        - keyword (선택, 검색어)
    - 반환: 게시글 목록 페이지

- `GET /board/{id}` - 게시글 상세 조회
    - 파라미터: id (게시글 ID)
    - 반환: 게시글 상세 페이지 (댓글 목록 포함)
    - 비고: 로그인하지 않은 사용자도 접근 가능

#### 게시글 작성 및 수정
- `GET /board/save` - 게시글 작성 화면
    - 요구사항: 로그인 필수
    - 반환: 게시글 작성 폼 페이지

- `POST /board/save` - 게시글 작성 처리
    - 요청 데이터: title, content, premium (선택)
    - 검증: 제목, 내용 필수
    - 반환: 메인 페이지로 리다이렉트

- `GET /board/{id}/update` - 게시글 수정 화면
    - 요구사항: 로그인 필수, 게시글 소유자만 접근 가능
    - 반환: 게시글 수정 폼 페이지

- `POST /board/{id}/update` - 게시글 수정 처리
    - 요청 데이터: title, content, premium (선택)
    - 검증: 인가 검사, 제목/내용 필수
    - 반환: 게시글 목록으로 리다이렉트

- `POST /board/{id}/delete` - 게시글 삭제
    - 요구사항: 로그인 필수, 게시글 소유자만 접근 가능
    - 동작: 연관된 댓글 먼저 삭제 후 게시글 삭제
    - 반환: 게시글 목록으로 리다이렉트

#### 유료 게시글 구매
- `POST /board/{boardId}/purchase` - 유료 게시글 구매
    - 요구사항: 로그인 필수
    - 검증:
        - 유료 게시글인지 확인
        - 본인이 작성한 게시글인지 확인
        - 이미 구매한 게시글인지 확인
        - 포인트 충분 여부 확인
    - 동작: 포인트 차감, 구매 내역 저장
    - 반환: 게시글 상세 페이지로 리다이렉트

### 댓글 관련 엔드포인트

- `POST /reply/save` - 댓글 작성
    - 요구사항: 로그인 필수
    - 요청 데이터: boardId, comment
    - 검증: 댓글 내용 필수, 최대 500자
    - 반환: 게시글 상세 페이지로 리다이렉트

- `POST /reply/{id}/delete` - 댓글 삭제
    - 요구사항: 로그인 필수, 댓글 소유자만 접근 가능
    - 동작: 댓글 삭제
    - 반환: 해당 게시글 상세 페이지로 리다이렉트

### 관리자 관련 엔드포인트

- `GET /admin/dashboard` - 관리자 대시보드
    - 요구사항: 로그인 필수, 관리자 권한 필요
    - 반환: 관리자 대시보드 페이지

### REST API 엔드포인트

#### 이메일 인증 API
- `POST /api/email/send` - 이메일 인증번호 발송
    - 요청 데이터: `{ "email": "user@example.com" }`
    - 동작: 6자리 랜덤 인증번호 생성 후 이메일 발송, 세션에 저장
    - 반환: `{ "message": "인증번호가 발송되었습니다" }`

- `POST /api/email/verify` - 이메일 인증번호 확인
    - 요청 데이터: `{ "email": "user@example.com", "code": "123456" }`
    - 동작: 세션에 저장된 인증번호와 비교, 일치 시 세션에서 제거
    - 반환: `{ "message": "인증되었습니다" }` 또는 에러 메시지

#### 포인트 충전 API
- `POST /api/point/charge` - 포인트 충전
    - 요구사항: 로그인 필수
    - 요청 데이터: `{ "amount": 1000 }`
    - 동작: 사용자 포인트 충전, 세션 정보 갱신
    - 반환: `{ "message": "포인트가 충전되었습니다", "amount": 1000, "currentPoint": 5000 }`

### REST API 테스트 엔드포인트

- `GET /todos/{id}` - 외부 API 연동 테스트 (JSONPlaceholder)
- `GET /todos-todo/{id}` - 외부 API 응답을 DTO로 변환 테스트
- `GET /posts-test` - 외부 API에 POST 요청 테스트
- `GET /exchange-test` - 커스텀 헤더를 포함한 HTTP 요청 테스트

## 주요 설계 특징

### 아키텍처 패턴

#### MVC 패턴
- **Model**: JPA Entity를 통한 도메인 모델
- **View**: Mustache 템플릿을 통한 서버 사이드 렌더링
- **Controller**: HTTP 요청 처리 및 뷰 반환

#### 계층형 아키텍처
- **표현 계층**: Controller - HTTP 요청/응답 처리
- **비즈니스 계층**: Service - 비즈니스 로직 및 트랜잭션 관리
- **데이터 접근 계층**: Repository - 데이터베이스 접근
- **도메인 계층**: Entity - 비즈니스 로직 포함

### 보안 설계

#### 인증 메커니즘
- **세션 기반 인증**: HttpSession을 통한 상태 유지
- **인터셉터 체인**:
    - SessionInterceptor: 전역 세션 관리
    - LoginInterceptor: 로그인 필수 페이지 보호
    - AdminInterceptor: 관리자 권한 검사

#### 인가 메커니즘
- **엔티티 레벨 인가**: isOwner() 메서드를 통한 소유자 확인
- **서비스 레벨 인가**: Service 계층에서 권한 검사 후 비즈니스 로직 수행
- **역할 기반 접근 제어**: UserRole을 통한 다중 역할 지원

#### 비밀번호 보안
- **BCrypt 해싱**: 단방향 해시 알고리즘 사용
- **솔트 자동 생성**: BCrypt가 자동으로 솔트 생성 및 관리
- **안전한 검증**: matches() 메서드를 통한 비밀번호 확인

### 예외 처리 전략

#### 커스텀 예외 계층
- **Exception400**: 잘못된 요청 (Bad Request)
- **Exception401**: 인증 필요 (Unauthorized)
- **Exception403**: 권한 없음 (Forbidden)
- **Exception404**: 리소스 없음 (Not Found)
- **Exception500**: 서버 오류 (Internal Server Error)

#### 전역 예외 처리
- **@ControllerAdvice**: 모든 컨트롤러의 예외를 중앙에서 처리
- **HTTP 상태 코드 매핑**: 예외 타입에 맞는 적절한 HTTP 상태 코드 반환
- **사용자 친화적 메시지**: 기술적 오류 메시지 대신 사용자에게 이해하기 쉬운 메시지 제공
- **에러 페이지**: 각 HTTP 상태 코드에 맞는 전용 에러 페이지 제공

### 트랜잭션 관리

#### 트랜잭션 전략
- **@Transactional**: 선언적 트랜잭션 관리
- **읽기 전용 트랜잭션**: 조회 작업에 @Transactional(readOnly = true) 적용
- **트랜잭션 경계**: Service 계층에서 트랜잭션 경계 설정

#### 더티 체킹
- **자동 업데이트**: 엔티티 상태 변경 시 자동으로 UPDATE 쿼리 실행
- **효율적인 수정**: 명시적 save() 호출 없이도 변경사항 반영
- **트랜잭션 종료 시점**: 트랜잭션 커밋 시점에 변경사항 일괄 반영

### 성능 최적화

#### N+1 문제 해결
- **JOIN FETCH**: 연관된 엔티티를 한 번의 쿼리로 조회
- **배치 페치 사이즈**: default_batch_fetch_size 설정으로 배치 조회
- **명시적 조회**: 필요한 데이터만 선별적으로 조회

#### 페이징 최적화
- **Spring Data JPA Pageable**: 효율적인 페이징 처리
- **페이지 크기 제한**: 최소/최대 값 제한으로 성능 보장
- **카운트 쿼리 최적화**: DISTINCT를 활용한 중복 제거

#### 세션 관리
- **OSIV 비활성화**: Open Session in View를 false로 설정
- **명시적 조회**: 트랜잭션 내에서 필요한 데이터 모두 조회
- **DTO 변환**: 엔티티를 DTO로 변환하여 세션 종료 후에도 사용 가능

#### 쿼리 최적화
- **JPQL 활용**: 복잡한 쿼리는 JPQL로 작성
- **인덱스 활용**: 자주 조회되는 컬럼에 대한 인덱스 고려
- **정렬 최적화**: 인덱스가 있는 컬럼 기준 정렬

### 파일 업로드 처리

#### 파일 저장 전략
- **UUID 기반 파일명**: 파일명 충돌 방지를 위한 UUID 사용
- **디렉토리 자동 생성**: 업로드 디렉토리 없을 시 자동 생성
- **파일 타입 검증**: Content-Type을 통한 이미지 파일 검증
- **파일 크기 제한**: 최대 10MB로 제한

#### 파일 접근
- **리소스 핸들러**: /images/** 경로로 업로드된 파일 접근
- **절대 경로 매핑**: file:/// 프로토콜을 통한 파일 시스템 접근
- **소셜 이미지 지원**: HTTP URL로 시작하는 외부 이미지 지원

### OAuth 2.0 구현

#### 카카오 소셜 로그인 플로우
1. **인가 코드 요청**: 사용자가 카카오 로그인 버튼 클릭
2. **인가 코드 수신**: 카카오에서 인가 코드를 콜백으로 전달
3. **액세스 토큰 발급**: 인가 코드로 액세스 토큰 교환
4. **프로필 정보 조회**: 액세스 토큰으로 사용자 프로필 정보 조회
5. **자동 회원가입**: 프로필 정보로 사용자 생성 또는 조회
6. **세션 생성**: 로그인된 사용자 정보를 세션에 저장

#### 보안 고려사항
- **클라이언트 시크릿**: 환경 변수로 관리하여 코드에 노출 방지
- **리다이렉트 URI 검증**: 등록된 리다이렉트 URI만 허용
- **임시 비밀번호**: 소셜 로그인 사용자는 임시 비밀번호 설정

## 개발 환경 설정

### 프로파일 관리

Spring Boot의 프로파일 기능을 활용하여 환경별 설정을 분리했습니다:

- **dev**: 개발 환경 (기본값)
    - MySQL 데이터베이스 사용
    - SQL 로깅 활성화
    - DEBUG 레벨 로깅

- **local**: 로컬 개발 환경
    - H2 인메모리 데이터베이스 사용 가능
    - 상세한 로깅 설정

- **prod**: 운영 환경
    - 프로덕션 데이터베이스 설정
    - INFO 레벨 로깅
    - 보안 강화 설정

프로파일 변경 방법:
```bash
# 환경 변수로 설정
export SPRING_PROFILES_ACTIVE=prod

# 또는 JVM 옵션으로 설정
java -jar app.jar --spring.profiles.active=prod
```

### 로깅 설정

#### 로그 레벨
- **ERROR**: 심각한 오류
- **WARN**: 경고 메시지
- **INFO**: 일반 정보 (프로덕션 기본값)
- **DEBUG**: 상세 디버깅 정보 (개발 환경)
- **TRACE**: 매우 상세한 추적 정보

#### 로그 설정 위치
`src/main/resources/application-dev.yml`:
```yaml
logging:
  level:
    root: INFO           # 모든 라이브러리는 INFO 이상만 출력
    com.example: DEBUG   # 내 프로젝트는 DEBUG 이상 모두 출력
```

### 파일 업로드 설정

#### 파일 크기 제한
```yaml
spring:
  servlet:
    multipart:
      enabled: true           # 파일 업로드 활성화
      max-file-size: 10MB     # 개별 파일 최대 크기
      max-request-size: 10MB  # 전체 요청 최대 크기
```

#### 파일 저장 경로
- **물리적 경로**: `D:/uploads/` (Windows) 또는 설정 파일에서 변경 가능
- **웹 접근 경로**: `/images/**`
- **파일명 생성**: UUID + 원본 파일명

#### 지원 파일 형식
- 이미지 파일만 업로드 가능
- Content-Type 검증: `image/*`로 시작하는 파일만 허용

### 데이터베이스 설정 상세

#### JPA 설정
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # 스키마 자동 업데이트
    show-sql: true      # SQL 쿼리 콘솔 출력
    properties:
      hibernate:
        format_sql: true              # SQL 포맷팅
        default_batch_fetch_size: 100 # 배치 페치 사이즈
    open-in-view: false  # OSIV 비활성화
```

#### 데이터 초기화
- `data.sql` 파일을 통한 초기 데이터 로드
- `defer-datasource-initialization: true`로 Hibernate 초기화 후 실행

### 이메일 설정

이메일 인증 기능을 사용하기 위해 SMTP 서버 설정이 필요합니다:

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password  # Gmail 앱 비밀번호 사용
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

**Gmail 사용 시 주의사항**:
- 일반 비밀번호가 아닌 앱 비밀번호를 사용해야 합니다
- 2단계 인증이 활성화되어 있어야 앱 비밀번호 생성 가능
- 앱 비밀번호 생성: Google 계정 → 보안 → 2단계 인증 → 앱 비밀번호

## 초기 데이터

프로젝트 실행 시 `src/main/resources/db/data.sql` 파일을 통해 다음 초기 데이터가 로드됩니다:

### 사용자 데이터
- **관리자 계정 1개**:
    - Username: `admin`
    - Password: `1234`
    - Role: ADMIN, USER

- **일반 사용자 계정 4개**:
    - Username: `ssar`, `cos`, `hong`, `kim`
    - Password: `1234`
    - Role: USER

### 게시글 데이터
- 총 10개의 게시글
- 각 사용자별로 다양한 주제의 게시글 포함
- 생성일 기준으로 시간차를 두고 생성

### 댓글 데이터
- 각 게시글에 2~4개의 댓글
- 다양한 사용자가 작성한 댓글 포함
- 실제 사용 시나리오를 반영한 댓글 내용

## 개발 가이드

### 코드 스타일

#### 네이밍 컨벤션
- **클래스명**: PascalCase (예: `UserService`)
- **메서드명**: camelCase (예: `게시글작성`)
- **상수**: UPPER_SNAKE_CASE (예: `IMAGES_DIR`)
- **패키지명**: 소문자 (예: `org.example.demo_ssr_v1_1`)

#### DTO 설계 원칙
- **Request DTO**: `*Request.*DTO` (예: `BoardRequest.SaveDTO`)
- **Response DTO**: `*Response.*DTO` (예: `BoardResponse.DetailDTO`)
- **내부 클래스**: 정적 내부 클래스로 그룹화

#### 트랜잭션 관리 원칙
- **읽기 작업**: `@Transactional(readOnly = true)`
- **쓰기 작업**: `@Transactional`
- **Service 클래스 레벨**: 기본적으로 `readOnly = true` 설정

### 주의사항

#### 보안 관련
- **환경 변수 관리**: 민감한 정보(API 키, 비밀번호)는 환경 변수로 관리
- **SQL 인젝션 방지**: JPQL 파라미터 바인딩 사용
- **XSS 방지**: Mustache 템플릿의 자동 이스케이프 활용

#### 성능 관련
- **N+1 문제**: JOIN FETCH 또는 배치 페치 사용
- **페이징**: 대량 데이터 조회 시 반드시 페이징 적용
- **세션 관리**: OSIV 비활성화로 인한 LAZY 로딩 주의

#### 데이터 무결성
- **외래키 제약**: 연관된 데이터 삭제 시 CASCADE 처리 고려
- **트랜잭션 경계**: 비즈니스 로직 단위로 트랜잭션 설정
- **동시성 제어**: 필요 시 낙관적/비관적 락 적용 고려

## 트러블슈팅

### 자주 발생하는 문제

#### 1. 환경 변수 인식 안 됨
**증상**: `KAKAO_CLIENT_ID` 등의 환경 변수를 찾을 수 없다는 오류

**해결 방법**:
- IDE에서 환경 변수 설정 확인
- 시스템 환경 변수로 설정
- `application.yml`에서 직접 설정 (개발용만)

#### 2. 파일 업로드 경로 오류
**증상**: 프로필 이미지 업로드 시 파일을 찾을 수 없음

**해결 방법**:
- `D:/uploads/` 디렉토리 생성 확인
- `WebMvcConfig.java`에서 경로 설정 확인
- 파일 권한 확인

#### 3. 데이터베이스 연결 실패
**증상**: MySQL 연결 오류

**해결 방법**:
- MySQL 서버 실행 확인
- 데이터베이스 생성 확인
- 연결 정보(URL, username, password) 확인
- 타임존 설정 확인 (`serverTimezone=Asia/Seoul`)

#### 4. LAZY 초기화 예외
**증상**: `LazyInitializationException` 발생

**해결 방법**:
- JOIN FETCH 사용하여 필요한 데이터 한 번에 조회
- Service 계층에서 DTO로 변환하여 반환
- OSIV 설정 확인 (현재는 false로 설정됨)

## 참고 자료

### 공식 문서
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Data JPA 공식 문서](https://spring.io/projects/spring-data-jpa)
- [Mustache 템플릿 엔진](https://mustache.github.io/)

### 관련 기술
- [JPA 연관관계 매핑](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html)
- [OAuth 2.0 인증 플로우](https://oauth.net/2/)
- [BCrypt 암호화](https://en.wikipedia.org/wiki/Bcrypt)

## 라이선스

이 프로젝트는 데모 및 학습 목적으로 제작되었습니다.
