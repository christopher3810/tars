# TARS Auth Module

TARS 프로젝트의 Auth Module은 JWT 기반 토큰 관리 기능을 제공하는 독립적인 라이브러리 모듈입니다. 이 모듈은 JWT 토큰 생성, 검증, 파싱 등의 기능을 제공하며, 다른 모듈에서 쉽게 통합하여 사용할 수 있도록 설계되었습니다.

## 주요 기능

- JWT 토큰 생성 및 검증
- 리프레시 토큰 관리
- 토큰 기반 사용자 정보 추출
- 다양한 토큰 타입 지원 (액세스, 리프레시, 권한, 일회용)

## 기술 스택

- Kotlin 1.9.25
- JJWT (JSON Web Token) 0.11.5
- Jackson (JSON 처리)
- JUnit 5 (테스트)

## 모듈 구조

Auth 다음과 같은 패키지 구조로 구성되어 있습니다:

```
auth/
├── facade/           # 외부에 노출되는 인터페이스
│   └── TokenFacade.kt  # 토큰 관련 기능을 제공하는 Facade 클래스
├── service/          # 내부 서비스 구현체
│   ├── JwtTokenService.kt  # 토큰 서비스 구현체
│   └── TokenProvider.kt    # 토큰 생성 및 검증 서비스
├── domain/           # 도메인 모델 및 비즈니스 로직
│   └── token/        # 토큰 관련 도메인 모델
│       ├── builder/  # 토큰 빌더 패턴 구현체
│       │   ├── TokenBuilder.kt          # 토큰 빌더 인터페이스
│       │   ├── AbstractTokenBuilder.kt  # 추상 토큰 빌더
│       │   ├── AccessTokenBuilder.kt    # 액세스 토큰 빌더
│       │   ├── RefreshTokenBuilder.kt   # 리프레시 토큰 빌더
│       │   ├── AuthorizationTokenBuilder.kt  # 권한 토큰 빌더
│       │   └── OneTimeTokenBuilder.kt   # 일회용 토큰 빌더
│       └── type/     # 토큰 관련 타입 정의
│           ├── TokenType.kt     # 토큰 타입 열거형
│           ├── TokenClaim.kt    # 토큰 클레임 열거형
│           └── TokenPurpose.kt  # 토큰 목적 열거형
├── config/           # 설정 클래스
│   └── JwtConfig.kt  # JWT 관련 설정
├── dto/              # 데이터 전송 객체
│   ├── TokenResponse.kt       # 토큰 응답 DTO
│   ├── UserTokenInfo.kt       # 사용자 토큰 정보 DTO
│   └── TokenRefreshRequest.kt # 토큰 갱신 요청 DTO
└── exception/        # 예외 클래스
    ├── TokenException.kt          # 토큰 관련 예외
    └── AuthenticationException.kt # 인증 관련 예외
```

## 사용 방법

### 1. 의존성 추가

Gradle 프로젝트에 다음과 같이 의존성을 추가합니다:

```gradle
implementation 'com.tars:auth:0.0.1-SNAPSHOT'
```

### 2. TokenFacade 인스턴스 생성

TokenFacade는 모듈의 주요 진입점으로, 다음과 같이 인스턴스를 생성할 수 있습니다:

```kotlin
// 기본 설정으로 생성
val tokenFacade = TokenFacade.createDefault()

// 커스텀 설정으로 생성
val tokenFacade = TokenFacade.create(
    secret = "yourSecretKey",
    expirationMs = 3600000L, // 1시간
    refreshExpirationMs = 86400000L // 24시간
)
```

### 3. 토큰 생성

사용자 정보를 기반으로 토큰을 생성합니다:

```kotlin
val tokenResponse = tokenFacade.generateTokens(
    userId = 123L,
    email = "user@example.com",
    roles = setOf("USER", "ADMIN"),
    additionalClaims = mapOf("customClaim" to "value")
)

val accessToken = tokenResponse.accessToken
val refreshToken = tokenResponse.refreshToken
```

### 4. 토큰 검증

토큰의 유효성을 검증합니다:

```kotlin
val isValid = tokenFacade.validateToken(token)
```

### 5. 토큰에서 사용자 정보 추출

토큰에서 사용자 정보를 추출합니다:

```kotlin
// 전체 사용자 정보 추출
val userInfo = tokenFacade.getUserInfoFromToken(token)

// 특정 정보 추출
val userId = tokenFacade.getUserIdFromToken(token)
val email = tokenFacade.getUserEmailFromToken(token)
val roles = tokenFacade.getUserRolesFromToken(token)
```

### 6. 토큰 갱신

리프레시 토큰을 사용하여 액세스 토큰을 갱신합니다:

```kotlin
val newTokenResponse = tokenFacade.refreshToken(refreshToken)
val newAccessToken = newTokenResponse.accessToken
```

## 주의사항

- 실제 운영 환경에서는 반드시 안전한 시크릿 키를 사용하세요.
- 토큰의 만료 시간은 보안 요구사항에 맞게 설정하세요.
- 리프레시 토큰은 안전하게 저장하고 관리해야 합니다.

## 확장 가능성

- 토큰 저장소 구현을 통한 토큰 블랙리스트 관리
- 비대칭 키(RSA) 기반 토큰 서명 지원
- 토큰 회전(Rotation) 정책 구현

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 