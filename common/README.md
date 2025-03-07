# Common Module

TARS 프로젝트의 Common Module은 여러 모듈에서 공통으로 사용되는 유틸리티, 예외 처리, 오류 메시지 등을 제공하는 순수 Kotlin 라이브러리입니다.

현재 는 이렇게 서브 모듈화 시켜서 쓰는데
나중에 그냥 api gateway로 분리시키려고 합니다.
그전까지 일단 서브모듈로 interface만 맞춰서 쓰는 용도!

## 주의사항

1. Common Module은 순수 Kotlin 라이브러리로, Spring과 같은 프레임워크에 의존하지 않습니다.
2. 이 모듈에 추가되는 모든 코드는 재사용성과 경량성을 고려하여 작성되어야 합니다.
3. 특정 모듈에만 필요한 기능은 Common Module이 아닌 해당 모듈 내에 구현해야 합니다.

## 주요 기능

Common Module은 다음과 같은 주요 기능을 제공합니다:

1. **오류 메시지 관리**: 표준화된 오류 메시지 정의 및 관리
2. **예외 처리 유틸리티**: 일관된 예외 응답 형식 제공
3. **유틸리티 클래스**: 다양한 유틸리티 기능 (ID 생성 등)

## 모듈 구조

```
com.tars.common
├── error
│   └── ErrorMessage.kt - 표준화된 오류 메시지 열거형
├── exception
│   └── ErrorResponse.kt - API 오류 응답 데이터 클래스
└── util
    └── UserIdGenerator.kt - 사용자 ID 생성 유틸리티
```

## 테스트

Common Module의 테스트는 Kotest 프레임워크를 사용하여 describe-context-it 패턴의 BDD 스타일로 작성되었습니다.

## 사용 방법

### 1. 의존성 추가

프로젝트의 `build.gradle` 파일에 Common Module 의존성을 추가합니다:

```gradle
dependencies {
    implementation project(':common')
    // 기타 의존성...
}
```

### 2. 오류 메시지 사용

표준화된 오류 메시지를 사용하여 일관된 오류 처리를 구현할 수 있습니다:

```kotlin
import com.tars.common.error.ErrorMessage

// 오류 메시지 사용
val errorMessage = ErrorMessage.USER_NOT_FOUND.format("user@example.com")
```

### 3. 오류 응답 생성

API 오류 응답을 생성할 때 표준 형식을 사용할 수 있습니다:

```kotlin
import com.tars.common.exception.ErrorResponse
import com.tars.common.error.ErrorMessage
import java.time.LocalDateTime

// 오류 응답 생성
val errorResponse = ErrorResponse(
    timestamp = LocalDateTime.now(),
    status = 404,
    error = "Not Found",
    code = ErrorMessage.USER_NOT_FOUND.code,
    message = ErrorMessage.USER_NOT_FOUND.format("user@example.com"),
    path = "/api/users/email/user@example.com"
)
```

### 4. 사용자 ID 생성

고유한 사용자 ID를 생성할 때 UserIdGenerator를 사용할 수 있습니다:

```kotlin
import com.tars.common.util.UserIdGenerator

// 10자리 고유 사용자 ID 생성
val userId = UserIdGenerator.generate()

// ID 유효성 검증
val isValid = UserIdGenerator.isValid(userId)
``` 