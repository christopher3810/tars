# tars

![tars-noti](https://user-images.githubusercontent.com/61622657/226113256-f64492b9-7a53-4551-b042-0e3d5f8963b9.gif)


stock utilizer

## 변경 사항

auth repo 제거 예정

common 은 유지 및 고도화

### 인증과 인가: 책임의 분리

```mermaid
graph TD
    Client[클라이언트] -->|요청| Gateway[API Gateway]
    Gateway -->|인증 수행| AuthService[Auth Center]
    AuthService -->|토큰 발급| Gateway
    Gateway -->|인증된 요청 + 토큰/클레임| Service1[Tars Service]
    Gateway -->|인증된 요청 + 토큰/클레임| Service2[Tars Analyzer]
    Service1 -->|인가 수행| Service1
    Service2 -->|인가 수행| Service2
    AuthService -.->|동기화| PolicyRepo[(정책 저장소)]
    Service1 -.->|정책 참조| PolicyRepo
    Service2 -.->|정책 참조| PolicyRepo
```

### git repo

[api-gateway](https://github.com/christopher3810/api-gateway)

[auth_center](https://github.com/christopher3810/auth_center)
