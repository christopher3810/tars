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

Common Module은 다음과 같은 주요 기능을 제공

1. **오류 메시지 관리**: 표준화된 오류 메시지 정의 및 관리
2. **예외 처리 유틸리티**: 일관된 예외 응답 형식 제공
3. **유틸리티 클래스**: 다양한 유틸리티 기능 (ID 생성 등)

## 테스트

Common Module의 테스트는 Kotest 프레임워크를 사용하여 describe-context-it 패턴의 BDD 스타일로 작성되었습니다.

## 사용 방법

### 1. 의존성 추가

프로젝트의 `build.gradle` 파일에 Common Module 의존성을 추가합니다:

현재는 local maven 을 통해서 받는 방식으로 하기 때문에 gradle task 에서 로컬 배포후 받아서 쓰는 방식으로
사용하고 있습니다

아래 스냅샷은 예시이고 local maven 에 배포한 버전 기반으로 가져가면됩니다.

jit pack 같은건 나중에 고려

```gradle
dependencies {
     implementation('com.tars:common:0.0.1-SNAPSHOT')
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

sync 인 타입과 cas를 일일히 하는 타입 두가지

1. UserIdGeneratorCas
2. UserIdGeneratorSync

상황에 맞게 사용하면 됩니다.

