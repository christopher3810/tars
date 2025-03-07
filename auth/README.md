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
class TokenFacade(jwtConfig: JwtConfig = JwtConfig.standard) {
    // 토큰 생성, 검증, 파싱 등의 메서드를 제공
    
    companion object {
        fun standard(): TokenFacade
        fun custom(configCustomizer: JwtConfig.Builder.() -> Unit): TokenFacade
    }
}
```

### JwtConfig

JWT 관련 설정을 관리하는 클래스입니다. 빌더 패턴을 지원하여 설정을 유연하게 커스터마이징할 수 있습니다.

```kotlin
data class JwtConfig(
    val secret: String = "defaultSecretKeyForJwtAuthenticationPleaseOverrideInProduction",
    val expirationMs: Long = 3600000L, // 1시간
    val refreshExpirationMs: Long = 86400000L // 24시간
) {
    // 빌더 클래스 및 메서드 제공
    
    companion object {
        val standard: JwtConfig
        fun builder(): Builder
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
// 기본 설정 사용
val tokenFacade = TokenFacade.standard()

// 커스텀 설정 사용
val tokenFacade = TokenFacade.custom {
    secret("your-secure-secret-key")
    expirationMs(1800000L) // 30분
    refreshExpirationMs(604800000L) // 7일
}
```

> 현재 custom의 경우 3가지 필드만 받는데 추후 확장할 예정

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

## 보안 권장사항

- 실제 운영 환경에서는 반드시 강력한 시크릿 키를 사용.
  - Hms, vault, 등 시크릿관리를 별도로하는게 더 좋음.
- 토큰 만료 시간은 애플리케이션의 보안 요구사항에 맞게 설정하세요.
  - 만료시간은 프로젝트별로 특정 상황에 따라서 충분히 고려하고 설정하자.
- 리프레시 토큰은 서버 측에서 안전하게 관리하세요.
- 민감한 정보는 토큰 클레임에 포함하지 마세요.


## 라이센스

이 프로젝트는 MIT 라이센스 하에 배포됩니다. 