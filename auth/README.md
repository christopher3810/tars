# TARS Auth Module

TARS 프로젝트의 Auth Module은 JWT 기반 토큰 관리 기능을 제공하는 독립적인 Kotlin 라이브러리입니다. Spring 의존성 없이 순수 Kotlin으로 작성되어 어떤 JVM 기반 프로젝트에서도 쉽게 사용할 수 있습니다.

## 주요 기능

- JWT 액세스 토큰 및 리프레시 토큰 생성
- 토큰 검증 및 파싱
- 토큰에서 사용자 정보 추출
- 유연한 설정 옵션 (토큰 만료 시간, 시크릿 키 등)

## 기술 스택

- Kotlin 1.9.25
- JJWT (JSON Web Token) 0.11.5
- Jackson (JSON 처리)

## 의존성 추가 방법

현재 이 라이브러리는 Maven Local Repository를 통해 배포되어 있습니다. 프로젝트에 다음과 같이 의존성을 추가할 수 있습니다:

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    implementation("com.tars:auth:0.0.1-SNAPSHOT")
}
```

### Gradle (Groovy DSL)

```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation 'com.tars:auth:0.0.1-SNAPSHOT'
}
```

### Maven

```xml
<dependency>
    <groupId>com.tars</groupId>
    <artifactId>auth</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

> **참고**: 향후 JitPack과 같은 외부 라이브러리 배포 플랫폼을 통해 더 쉽게 의존성을 관리할 수 있도록 개선할 예정입니다.

## 주요 인터페이스

이 라이브러리는 외부에서 사용할 수 있는 간단한 인터페이스를 제공합니다. 모든 기능은 `TokenFacade` 클래스를 통해 접근할 수 있습니다.

### TokenFacade

토큰 관련 모든 기능의 진입점입니다. 내부 구현을 추상화하여 간단한 인터페이스를 제공합니다.

```kotlin
class TokenFacade(jwtConfig: JwtConfig = JwtConfig.standard())

companion object {
    fun standard(): TokenFacade
    fun custom(configBlock: JwtConfigBuilder.() -> Unit): TokenFacade
}
```

### JwtConfig

JWT 설정을 위한 인터페이스입니다. 정적 팩토리 메서드를 통해 표준 설정 또는 커스텀 설정을 생성할 수 있습니다.

```kotlin
interface JwtConfig {
    val secret: String
    val expirationMs: Long
    val refreshExpirationMs: Long
    
    companion object {
        fun standard(): JwtConfig
        fun builder(): JwtConfigBuilder
    }
}
```

### 데이터 클래스

#### TokenResponse

토큰 생성 및 갱신 결과를 나타내는 데이터 클래스입니다.

```kotlin
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600 // 기본 만료 시간 1시간 (초 단위)
)
```

#### UserTokenInfo

토큰에 포함될 사용자 정보를 나타내는 데이터 클래스입니다.

```kotlin
data class UserTokenInfo(
    val id: Long,
    val email: String,
    val roles: Set<String> = emptySet(),
    val additionalClaims: Map<String, Any> = emptyMap()
)
```

## 사용 예시

### TokenFacade 인스턴스 생성

```kotlin
// 표준 설정 사용
val tokenFacade = TokenFacade.standard()

// 커스텀 설정 사용
val tokenFacade = TokenFacade.custom {
    secret("your-secure-secret-key")
    expirationMs(1800000L) // 30분
    refreshExpirationMs(604800000L) // 7일
}
```

### 토큰 생성

```kotlin
val tokenResponse = tokenFacade.generateTokens(
    userId = 123L,
    email = "user@example.com",
    roles = setOf("USER", "ADMIN"),
    additionalClaims = mapOf("organization" to "TARS")
)

val accessToken = tokenResponse.accessToken
val refreshToken = tokenResponse.refreshToken
val expiresIn = tokenResponse.expiresIn // 만료 시간(초)
```

### 토큰 검증

```kotlin
val isValid = tokenFacade.validateToken(accessToken)
```

### 토큰에서 정보 추출

```kotlin
// 전체 사용자 정보 추출
val userInfo = tokenFacade.getUserInfoFromToken(accessToken)

// 특정 정보 추출
val userId = tokenFacade.getUserIdFromToken(accessToken)
val email = tokenFacade.getUserEmailFromToken(accessToken)
val roles = tokenFacade.getUserRolesFromToken(accessToken)
```

### 토큰 갱신

```kotlin
val newTokenResponse = tokenFacade.refreshToken(refreshToken)
val newAccessToken = newTokenResponse.accessToken
```

## JWT 설정 관리

이 라이브러리는 다양한 방식으로 JWT 설정을 관리할 수 있도록 유연한 구조를 제공합니다:

### 표준 JWT 설정

기본 설정은 개발 및 테스트 환경에 적합한 값으로 미리 구성되어 있습니다:

```kotlin
val jwtConfig = JwtConfig.standard()
```

이 설정은 다음과 같은 기본값을 제공합니다:
- 시크릿 키: 기본 개발용 시크릿 (운영 환경에서는 반드시 변경 필요)
- 액세스 토큰 만료 시간: 1시간 (3,600,000ms)
- 리프레시 토큰 만료 시간: 24시간 (86,400,000ms)

### 커스텀 JWT 설정

빌더 패턴을 사용하여 JWT 설정을 커스터마이징할 수 있습니다:

```kotlin
val jwtConfig = JwtConfig.builder()
    .secret("your-custom-secret-key")
    .expirationMs(1800000L) // 30분
    .refreshExpirationMs(604800000L) // 7일
    .build()
```

## 보안 권장사항

- 실제 운영 환경에서는 반드시 강력한 시크릿 키를 사용하세요.
  - 클라우드 서비스의 시크릿 관리 도구(AWS Secrets Manager, HashiCorp Vault 등)를 활용하는 것을 권장합니다.
- 토큰 만료 시간은 애플리케이션의 보안 요구사항에 맞게 설정하세요.
  - 액세스 토큰은 짧게, 리프레시 토큰은 적절하게 설정하여 보안과 사용자 경험 사이의 균형을 맞추세요.
- 리프레시 토큰은 서버 측에서 안전하게 관리하세요.
- 민감한 정보는 토큰 클레임에 포함하지 마세요.

## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 