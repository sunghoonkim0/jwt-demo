# JWT Demo (Spring Boot 3 + Spring Security)

프로젝트 목적
----------------
- Spring Boot 3 환경에서 Spring Security와 JWT(Json Web Token)를 사용한 인증 예제를 제공합니다.
- 로그인 시 JWT를 발급받고, 발급된 JWT를 Authorization 헤더에 포함해 보호된 API를 호출하는 흐름을 학습/테스트할 수 있습니다.

구현 내용(요약)
----------------
- `POST /login` 엔드포인트: 데모용 하드코딩 사용자(`user` / `password`)로 인증 후 JWT 발급
- JWT 생성/검증 유틸리티: `src/main/java/com/sunghoonkim0/jwt_demo/util/JwtUtils.java`
- 요청 필터: `src/main/java/com/sunghoonkim0/jwt_demo/security/JwtAuthenticationFilter.java` — Authorization 헤더에서 Bearer 토큰을 추출해 검증하고 SecurityContext에 Authentication 설정
- Spring Security 설정: `src/main/java/com/sunghoonkim0/jwt_demo/config/SecurityConfig.java` — `/login`은 허용, 나머지 요청은 인증 필요(Stateless)
- 보호된 API: `GET /api/protected` (컨트롤러: `src/main/java/com/sunghoonkim0/jwt_demo/controller/TestController.java`)

프로젝트 구조(핵심 파일)
----------------
- `build.gradle` — 의존성(JJWT 추가) 및 Gradle 설정
- `src/main/resources/application.properties` — JWT 비밀키(`jwt.secret`) 및 만료시간(`jwt.expiration-ms`)
- `JwtUtils.java` — 토큰 생성/검증
- `JwtAuthenticationFilter.java` — 요청 필터
- `SecurityConfig.java` — SecurityFilterChain 설정
- `AuthController.java` — 로그인 엔드포인트
- `TestController.java` — 보호된 테스트 엔드포인트

환경 및 요구
----------------
- JDK 21 (프로젝트 `toolchain`에 Java 21 설정)
- Gradle은 제공된 `gradlew.bat`을 사용하여 실행 권장
- Windows PowerShell 환경에서 예시 명령 제공

실행 및 테스트 가이드 (PowerShell)
----------------
1) Java 환경 변수 설정 (예: JDK가 `C:\Program Files\Java\jdk-21`에 설치되어 있을 경우)

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
$env:Path = $env:JAVA_HOME + '\\bin;' + $env:Path
cd 'C:\intellijProjects\jwt-demo'
```

2) 빌드 및 테스트 (Gradle wrapper 사용)

```powershell
# 전체 빌드와 테스트 실행
.\gradlew.bat test

# 애플리케이션 실행
.\gradlew.bat bootRun
```

3) 로그인하여 토큰 발급 (PowerShell 예)

```powershell
$login = @{ username='user'; password='password' } | ConvertTo-Json
$response = Invoke-RestMethod -Uri 'http://localhost:8080/login' -Method Post -Body $login -ContentType 'application/json'
$response.token
```

4) 보호된 API 호출 (PowerShell 예)

```powershell
$token = $response.token
Invoke-RestMethod -Uri 'http://localhost:8080/api/protected' -Method Get -Headers @{ Authorization = "Bearer $token" }
```

curl 예시 (Linux/macOS 또는 Git Bash)

```bash
# 로그인 (응답에서 token 값을 추출)
curl -s -X POST http://localhost:8080/login -H "Content-Type: application/json" -d '{"username":"user","password":"password"}'

# 보호된 호출
curl -H "Authorization: Bearer <토큰>" http://localhost:8080/api/protected
```

테스트 자동화 가이드
----------------
- 현재 프로젝트에는 기본 Spring Boot context 로딩 테스트(`src/test/.../JwtDemoApplicationTests.java`)가 포함되어 있습니다.
- 통합 테스트(로그인 -> 토큰 -> 보호된 API)를 추가하려면 `spring-boot-starter-test`와 `spring-security-test`를 사용하여 MockMvc를 이용한 테스트를 작성하세요.
- Gradle 테스트 실행 전에 로컬 환경에 Java가 올바르게 설치되어 있고 `JAVA_HOME`이 설정되었는지 확인하세요. Gradle 실행 오류 예:

```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
```

이 경우 JDK 설치 경로를 `JAVA_HOME`에 설정한 후 다시 시도하세요.

보안 주의사항
----------------
- 예제의 `jwt.secret`은 데모용이며 코드/리포지토리에 노출하면 안 됩니다. 운영 환경에서는 환경 변수, Vault 또는 비밀 관리 서비스를 사용하세요.
- 실무에서는 하드코딩된 사용자 인증 대신 `UserDetailsService`와 안전한 비밀번호 저장(BCrypt 등)을 사용하세요.
- 토큰 만료, 리프레시 토큰 전략, 토큰 블랙리스트(로그아웃) 등을 도입하세요.

다음 권장 작업
----------------
1. `UserDetailsService`로 실제 사용자 인증 적용 (인메모리 또는 JDBC)
2. 토큰에 권한(roles)을 담아 인가(Authorization) 처리 추가
3. 통합/인수 테스트(로그인→토큰→보호된 API)를 추가
4. 운영용 비밀 관리(환경 변수/Secrets Manager)로 `jwt.secret` 이동
