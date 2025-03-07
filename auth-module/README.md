# TARS Auth Module

TARS 프로젝트의 Auth Module은 인증 및 권한 관리 기능을 제공하는 독립적인 라이브러리 모듈입니다. 이 모듈은 JWT 기반 인증, 사용자 인증 정보 관리, 권한 검증 등의 기능을 제공하며, 다른 모듈에서 쉽게 통합하여 사용할 수 있도록 설계되었습니다.

## 주요 기능

- JWT 토큰 생성 및 검증
- 사용자 인증 및 로그인 처리
- 토큰 갱신 (Refresh Token)
- Spring Security 통합
- 역할 기반 접근 제어 (RBAC)
- 어노테이션 기반 권한 체크 (`@PreAuthorize`)

## 기술 스택

- Kotlin 1.9.25
- Spring Boot 3.4.0
- Spring Security
- JJWT (JSON Web Token)
- Gradle

## 모듈 구조

Auth Module은 포트-어댑터 아키텍처(헥사고날 아키텍처)를 기반으로 설계되었습니다:

```
auth-module/
├── domain/           # 도메인 모델 및 비즈니스 로직
│   └── UserAuthDetails.kt  # 인증에 필요한 사용자 정보 모델
├── port/             # 인터페이스 정의
│   ├── input/        # 입력 포트 (다른 모듈이 사용)
│   │   └── AuthServicePort.kt  # 인증 서비스 인터페이스
│   └── output/       # 출력 포트 (다른 모듈이 구현)
│       └── UserAuthPort.kt  # 사용자 정보 조회 인터페이스
├── adapter/          # 어댑터 구현체
│   ├── input/        # 입력 어댑터
│   │   ├── AuthServiceAdapter.kt  # AuthServicePort 구현체
│   │   └── JwtAuthenticationFilter.kt  # JWT 인증 필터
│   └── config/       # 설정 클래스
│       ├── SecurityConfig.kt  # Spring Security 설정
│       └── JwtConfig.kt  # JWT 관련 설정
├── service/          # 서비스 구현체
│   ├── AuthService.kt  # 인증 서비스 구현체
│   ├── TokenProvider.kt  # 토큰 생성 및 검증 서비스
│   └── CustomUserDetailsService.kt  # Spring Security 사용자 서비스
└── dto/              # 데이터 전송 객체
    ├── LoginRequest.kt  # 로그인 요청 DTO
    ├── TokenResponse.kt  # 토큰 응답 DTO
    └── TokenRefreshRequest.kt  # 토큰 갱신 요청 DTO
```

## 상위 모듈에서의 사용 방법

### 1. 의존성 추가

상위 모듈의 `build.gradle` 파일에 Auth Module 의존성을 추가합니다:

```gradle
dependencies {
    implementation('com.tars:auth-module:0.0.1-SNAPSHOT')
}
```

### 2. JWT 설정 추가

상위 모듈의 `application.yml` 또는 `application.properties` 파일에 JWT 관련 설정을 추가합니다:

```yaml
jwt:
  secret: yourSecretKeyHereShouldBeAtLeast32CharactersLong
  expirationMs: 3600000        # 액세스 토큰 만료 시간 (1시간)
  refreshExpirationMs: 86400000  # 리프레시 토큰 만료 시간 (24시간)
```

> **참고**: Auth Module은 기본값을 제공하지만, 프로덕션 환경에서는 반드시 위 설정을 오버라이드하여 사용하세요.

### 3. Auth Module 설정 가져오기

상위 모듈의 메인 애플리케이션 클래스에 Auth Module 설정을 가져오도록 `@Import` 어노테이션을 추가합니다:

```kotlin
import com.tars.auth.AuthModuleConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication(scanBasePackages = ["com.tars.app"])  # 상위 모듈의 패키지만 스캔
@Import(AuthModuleConfig::class)  # Auth Module 설정 가져오기
class YourApplication

fun main(args: Array<String>) {
    runApplication<YourApplication>(*args)
}
```

> **중요**: `scanBasePackages` 속성을 사용하여 상위 모듈의 패키지만 스캔하도록 설정하고, `@Import` 어노테이션을 사용하여 `AuthModuleConfig` 클래스를 명시적으로 임포트해야 합니다. 이렇게 하면 컴포넌트 스캔 중복으로 인한 빈 정의 충돌을 방지할 수 있습니다.

### 4. UserAuthPort 인터페이스 구현

Auth Module이 사용자 정보를 가져올 수 있도록 `UserAuthPort` 인터페이스를 구현합니다. 이 인터페이스는 Auth Module이 상위 모듈에서 사용자 정보를 조회하기 위해 필요합니다.

```kotlin
package com.tars.app.auth.adapter

import com.tars.auth.domain.UserAuthDetails
import com.tars.auth.port.output.UserAuthPort
import org.springframework.stereotype.Component

@Component
class UserAuthAdapter(
    private val userRepository: UserRepository  # 상위 모듈의 사용자 저장소
) : UserAuthPort {
    
    override fun findUserByEmail(email: String): UserAuthDetails? {
        val user = userRepository.findByEmail(email) ?: return null
        
        return UserAuthDetails(
            id = user.id,
            email = user.email,
            hashedPassword = user.password,
            roles = user.roles.map { it.name }.toSet()
        )
    }
    
    override fun findUserById(id: Long): UserAuthDetails? {
        val user = userRepository.findById(id).orElse(null) ?: return null
        
        return UserAuthDetails(
            id = user.id,
            email = user.email,
            hashedPassword = user.password,
            roles = user.roles.map { it.name }.toSet()
        )
    }
}
```

또는 설정 클래스를 통해 빈으로 등록할 수도 있습니다:

```kotlin
@Configuration
class AppAuthConfig {
    
    @Bean
    fun userAuthPort(): UserAuthPort {
        return UserAuthAdapter()
    }
}
```

### 5. AuthServicePort 사용하기

Auth Module에서 제공하는 `AuthServicePort` 인터페이스를 통해 인증 기능을 사용합니다:

```kotlin
import com.tars.auth.dto.LoginRequest
import com.tars.auth.dto.TokenRefreshRequest
import com.tars.auth.port.input.AuthServicePort
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authServicePort: AuthServicePort
) {
    
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest) = 
        authServicePort.login(request.email, request.password)
    
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: TokenRefreshRequest) = 
        authServicePort.refreshToken(request.refreshToken)
}
```

### 6. 어노테이션 기반 권한 체크 사용하기

Auth Module은 Spring Security의 메서드 수준 보안 기능을 활성화합니다. `@PreAuthorize` 어노테이션을 사용하여 메서드 레벨에서 권한을 체크할 수 있습니다:

```kotlin
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {
    
    // 관리자 권한이 있는 사용자만 접근 가능
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers() = userService.findAll()
    
    // 인증된 사용자만 접근 가능
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser() = userService.getCurrentUser()
    
    // 자신의 정보나 관리자만 접근 가능
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    fun getUserById(@PathVariable id: Long) = userService.findById(id)
}
```

주요 SpEL 표현식:
- `hasRole('ROLE_NAME')`: 특정 역할을 가진 사용자만 접근 가능
- `hasAnyRole('ROLE1', 'ROLE2')`: 나열된 역할 중 하나라도 가진 사용자만 접근 가능
- `isAuthenticated()`: 인증된 사용자만 접근 가능
- `isAnonymous()`: 익명 사용자만 접근 가능
- `authentication.principal.property`: 현재 인증된 사용자의 속성에 접근
- `#paramName`: 메서드 파라미터에 접근

### 7. SecurityFilterChain 커스터마이징

Auth Module은 기본적인 SecurityFilterChain을 제공하지만, 상위 모듈에서 필요에 따라 커스터마이징할 수 있습니다:

```kotlin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class AppSecurityConfig {
    
    @Bean
    @Order(1)  // 우선순위 설정 (낮은 숫자가 높은 우선순위)
    fun apiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/api/**")  // 이 필터 체인은 /api/** 경로에만 적용
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
        
        return http.build()
    }
    
    @Bean
    @Order(2)
    fun webSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/**")  // 이 필터 체인은 나머지 모든 경로에 적용
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/home", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .permitAll()
            }
        
        return http.build()
    }
}
```

> **참고**: 상위 모듈에서 SecurityFilterChain을 정의하면 Auth Module에서 제공하는 기본 설정을 오버라이드합니다. 이 경우 JWT 인증 필터를 수동으로 추가해야 할 수 있습니다.

### 8. 토큰 생성 및 검증

Auth Module은 `AuthServicePort` 인터페이스를 통해 토큰 생성 및 검증 기능을 제공합니다:

```kotlin
// 로그인 및 토큰 생성
val tokenResponse = authServicePort.login(email, password)
val accessToken = tokenResponse.accessToken
val refreshToken = tokenResponse.refreshToken

// 토큰 갱신
val newTokenResponse = authServicePort.refreshToken(refreshToken)

// 토큰 검증
val isValid = authServicePort.validateToken(accessToken)

// 토큰에서 사용자 이메일 추출
val email = authServicePort.getUserEmailFromToken(accessToken)
```

## Auth Module이 상위 모듈에게 필요로 하는 것

Auth Module이 정상적으로 동작하기 위해 상위 모듈에서 제공해야 하는 것들은 다음과 같습니다:

1. **UserAuthPort 구현체**:
   - 사용자 인증 정보를 제공하는 `UserAuthPort` 인터페이스의 구현체
   - 이메일 또는 ID로 사용자를 조회하는 기능 제공
   - `UserAuthDetails` 객체를 반환하여 인증에 필요한 정보 제공

2. **JWT 설정**:
   - JWT 비밀 키 (`jwt.secret`)
   - 액세스 토큰 만료 시간 (`jwt.expirationMs`)
   - 리프레시 토큰 만료 시간 (`jwt.refreshExpirationMs`)

3. **Spring Security 설정 (선택적)**:
   - 필요에 따라 SecurityFilterChain 커스터마이징
   - 접근 제어 규칙 정의
   - 인증 예외 처리 설정

4. **의존성 관리**:
   - Spring Boot 버전 호환성 유지 (현재 3.4.0)
   - 필요한 의존성 추가 (Spring Security, JWT 등)

## 문제 해결

### 모듈 간 호환성 문제

Spring Boot 버전 호환성 문제가 발생할 경우, 모든 모듈이 동일한 Spring Boot 버전(현재 3.4.0)을 사용하는지 확인하세요.

### 빈 충돌 문제

컴포넌트 스캔 중복으로 인한 빈 충돌이 발생할 경우:
1. 상위 모듈의 `@SpringBootApplication`에 `scanBasePackages` 속성을 설정하여 상위 모듈의 패키지만 스캔하도록 합니다.
2. `@Import` 어노테이션을 사용하여 `AuthModuleConfig` 클래스를 명시적으로 임포트합니다.
3. 필요한 경우 `@Qualifier` 어노테이션을 사용하여 빈을 구분합니다.

### 설정 문제

JWT 설정이 제대로 로드되지 않는 경우:
1. `application.yml` 또는 `application.properties` 파일에 JWT 설정이 올바르게 정의되어 있는지 확인합니다.
2. Auth Module은 기본값을 제공하지만, 프로덕션 환경에서는 명시적으로 설정하는 것이 좋습니다.

### 인증 문제

인증이 제대로 작동하지 않는 경우:
1. JWT 토큰이 올바르게 생성되고 있는지 확인합니다.
2. `Authorization` 헤더에 `Bearer` 접두사와 함께 토큰이 포함되어 있는지 확인합니다.
3. `UserAuthPort` 구현체가 올바르게 동작하는지 확인합니다.

## 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 