# *백엔드 개발 온보딩 과제 (Java)*

## 프로젝트 개요
- 이 프로젝트는 한달 인턴 온보딩 과제 제출용으로 작성된 프로젝트입니다.
- Spring Boot 기반 백엔드 서비스 개발을 통해, 실무 환경에서의 REST API 설계, JWT 인증, 테스트, 그리고 배포를 학습하는 것을 목표로 합니다.  
- 이를 통해 웹 애플리케이션 개발의 전반적인 프로세스를 경험하고, Spring Security와 JWT를 활용한 인증 시스템 구현에 중점을 두었습니다.
- 본 프로젝트는 RESTful API를 제공하며, API 문서는 Swagger UI를 통해 확인할 수 있습니다. 

---

## 기술 스택
- Language: Java 17
- Framework: Spring Boot 3.4.1
- Security: Spring Security, JWT
- Database
  - H2 Database (테스트 환경)
  - MySQL Database (로컬 개발 및 운영 환경: AWS RDS)
- Test: JUnit5
- Build Tool: Gradle
- Documentation: Swagger UI
- Deployment: AWS EC2, AWS RDS

---

## 프로젝트 구조
    src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── javabackendonboarding
    │   │               ├── api
    │   │               │   ├── auth
    │   │               │   │   ├── controller
    │   │               │   │   ├── dto
    │   │               │   │   │   ├── request
    │   │               │   │   │   └── response
    │   │               │   │   └── service
    │   │               │   ├── healthz
    │   │               │   │   └── controller
    │   │               │   └── user
    │   │               │       ├── controller
    │   │               │       ├── dto
    │   │               │       │   ├── request
    │   │               │       │   └── response
    │   │               │       └── service
    │   │               ├── config
    │   │               ├── domain
    │   │               │   ├── refreshToken
    │   │               │   │   ├── entity
    │   │               │   │   └── repository
    │   │               │   └── user
    │   │               │       ├── entity
    │   │               │       ├── enums
    │   │               │       └── repository
    │   │               └── security
    │   │                   ├── config
    │   │                   ├── dto
    │   │                   ├── entity
    │   │                   ├── filter
    │   │                   ├── service
    │   │                   └── util
    │   └── resources
    │
    │
    └── test
        ├── java
        │   └── com
        │       └── example
        │           └── javabackendonboarding
        │               ├── api
        │               │   └── healthz
        │               │       └── controller
        │               └── security
        │                   ├── config
        │                   ├── filter
        │                   └── mock
        └── resources

---

## 시나리오 설계 및 코딩 시작!

### **Spring Security, JWT 기본 이해**

- **Filter**란?
  - 요청 전처리: 클라이언트의 요청이 서블릿에 도달하기 전에 요청을 처리할 수 있습니다.
  - 응답 후처리: 서버에서 클라이언트로 응답을 보내기 전에 응답을 처리할 수 있습니다.
  - 용도: 인증, 로깅, 보안 검사, 공통 헤더 추가 등.
  - **Interceptor**
    - 차이점: Interceptor는 Filter와 비슷하지만, 주로 Spring Framework에서 사용됩니다. 요청이 컨트롤러에 도달하기 전이나, 응답이 클라이언트로 가기 전에 실행됩니다.
    - 용도: 로그인 확인, 권한 체크, 특정한 요청만 처리하도록 설정 등 
  - **AOP**
    - 차이점: AOP는 프로그램 내에서 공통적인 로직(예: 로깅, 트랜잭션 처리 등)을 따로 분리해서 관리할 수 있게 해주는 기법입니다. 특정 메서드가 실행되기 전에 또는 후에 로직을 실행할 수 있습니다. 
    - 용도: 함수가 실행되기 전에 로깅을 추가하거나, 함수 실행 중 문제가 생기면 예외를 처리하는 기능 등을 처리합니다.

<br>

- **Spring Security**란?
  - 웹 애플리케이션의 보안을 위한 프레임워크입니다. 인증과 인가를 처리하는 데 중점을 둡니다.
  - 인증(Authentication): 사용자가 로그인할 때, 올바른 사용자임을 확인하는 기능입니다.
  - 인가(Authorization): 인증된 사용자가 어떤 리소스에 접근할 수 있는지 결정합니다.
  - Spring Security는 또한 CSRF 보호, 세션 관리, XSS 보호, 패스워드 암호화 등 다양한 보안 기능을 내장하고 있어 웹 애플리케이션을 안전하게 보호할 수 있도록 돕습니다.

<br>

- **JWT**란?
  - 웹 애플리케이션에서 사용자 인증 및 정보를 안전하게 전송하기 위한 토큰 형식입니다.
  - JWT는 서버와 클라이언트 간에 정보를 안전하고 간편하게 전송하는 방식입니다.
  - JWT는 서버에 상태를 저장할 필요 없이 클라이언트 측에서 인증 정보를 처리할 수 있기 때문에, 분산 시스템에서 특히 유용합니다.
  - 구성: JWT는 세 부분으로 나뉩니다.
    - 헤더(Header): 토큰의 타입과 사용된 알고리즘을 포함합니다.
    - 페이로드(Payload): 실제 전송하고자 하는 데이터가 들어 있습니다. 사용자의 정보나 토큰의 만료 시간 등이 이곳에 포함됩니다.
    - 서명(Signature): 토큰이 변조되지 않았음을 검증하는 데 사용됩니다. 비밀 키를 사용해 서명을 생성하여 데이터의 무결성을 보장합니다.

---

## AWS에 배포하기
- EC2 배포 IP: 3.39.25.199 (Elastic IP 설정을 안해서 변경될 수 있음)
- [Swagger UI](http://3.39.25.199:8080/swagger-ui/index.html)

---

## API 명세서
  - 회원가입: [POST] /signup
    - request body
    ```json
        {
        	"username": "JIN HO",
        	"password": "12341234",
        	"nickname": "Mentos"
        }
    ```
  - 로그인: [POST] /sign
    - request body
    ```json
        {
        	"username": "JIN HO",
        	"password": "12341234"
        }
    ```
  - 헬스체크: [GET] /healthz

---

## 요구사항
- [x] Spring Security 를 이용한 Filter 적용
- [x] JWT 를 이용한 토큰 발행 및 refresh token 발행
- [x] JUnit 을 이용한 JWT Unit 테스트 코드 작성
- [x] Access / Refresh Token 발행과 검증에 관한 테스트 시나리오 작성하기
- [x] 백엔드 Spring boot로 회원가입, 로그인 로직 작성하기
- [x] AWS EC2에 배포하기
- [x] Swagger UI 로 접속 하능하게 하기
- [ ] AI 에게 코드리뷰 받아보기
- [ ] 피드백을 받아서 코드 개선하기
- [ ] AWS EC2 재배포하기
- [x] 과제 제출