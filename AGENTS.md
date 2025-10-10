# 📘 AGENTS.md

## 0. Basic Rule
- 모든 문서는 한글로 작성한다.

## 1. Spring Boot Code Convention
- laungauge: java17
- framework: springboot 3.5.6
- database: mysql8, redis-cli 7.0.8
- deploy os: ubuntu

### 코드 스타일 (Code Style)
- **Java/Spring Boot**
    - 클래스: `PascalCase`
    - 메서드/변수: `camelCase`
    - 최대 라인 길이: 120자
    - 들여쓰기: 4 spaces
    - `@Override` 반드시 명시
    - `Optional`은 **반환 타입**으로만 사용
- **Entity**
    - `Setter` 사용 금지 → Builder/생성자 활용
    - 연관관계는 기본 `FetchType.LAZY`
    - PK는 `UUID` 사용, DB에는 `BINARY(16)` 또는 동등한 형태로 저장
- **DI**
    - 의존성 주입은 기본적으로 Lombok `@RequiredArgsConstructor` 기반 **생성자 주입**을 사용한다.
    - 테스트 등 특수 케이스에서만 명시적 생성자/필드 주입을 고려한다.
- **트랜잭션**
    - `@Transactional`은 **서비스 계층에서만 허용**

---

### 패키지 구조 (Package Structure)
```
com.example.project
├─ domain
│   └─ user
│       ├─ entity
│       ├─ repository
│       ├─ service
│       ├─ controller
│       └─ exception
└─ global
    ├─ config
    ├─ exception
    └─ util
```
- 도메인 단위 패키징
- 공통 모듈은 `global`에 배치
- 패키지명은 **단수형**

---

### 네이밍 규칙 (Naming Convention)
- **Controller**: `UserController`
- **Service**: `UserService`, `UserServiceImpl`
- **Repository**: `UserRepository`
- **DTO**: `UserRequest`, `UserResponse`
- **Exception**: `UserNotFoundException`

### Repository 구현 방식

#### 1. 기본 CRUD (JpaRepository)
- 모든 엔티티는 JpaRepository를 상속한 Repository를 가진다.
- 단순 CRUD, 단일 조회에는 JpaRepository 기본 메서드를 활용한다.
- 실무에서는 Optional<T> 반환을 기본으로 한다.

#### 2. 커스텀 Repository
- 복잡한 조회나 동적 쿼리는 Custom Repository를 분리한다.
- Querydsl을 사용해 타입 안전성과 유지보수성을 확보한다.
- Repository 인터페이스는 JpaRepository + Custom Repository 조합을 기본으로 한다.

#### 3. CQRS (필요 시 적용)
- 읽기/쓰기 트래픽이 많은 서비스라면 조회용 QueryRepository 와 저장용 CommandRepository 로 분리한다.
- 조회는 DTO Projection 기반으로 작성하여 불필요한 엔티티 로딩을 줄인다.
- 작은 프로젝트에서는 과설계이므로 필요한 API에만 선택적으로 적용한다.

#### 4. Native Query Repository
- 대량 Insert/Update/통계성 집계 등 성능이 중요한 경우 Native Query를 활용한다.
- 영속성 컨텍스트와 분리되므로, 벌크 연산 전후로 영속성 컨텍스트 초기화를 원칙으로 한다.

#### 5. INSERT 전략
- save() / saveAll() 호출 시 엔티티 개수만큼 INSERT 발생 → 대량 처리 시 비효율적이다.
- hibernate.jdbc.batch_size 를 설정하여 Batch Insert를 적용한다.
- Custom Repository에서는 flush()/clear() 주기를 제어해 메모리 사용량을 최적화한다.
- 대량 적재가 필요한 경우 Native Query 벌크 Insert로 처리한다.

### Service 계층 규칙

- 트랜잭션 단위는 Service에서 관리 (`@Transactional`)
- 조회 메서드에는 `@Transactional(readOnly = true)` 적용
- DTO 변환 책임은 Service에서 담당
- Repository 호출 시 예외 발생 시 → 도메인별 `CustomException` 변환 후 throw
- 비즈니스 로직은 반드시 Service 계층에 위치
- Controller와 Repository에는 비즈니스 로직 두지 않는다.

---

### 예외 처리 설계

- **도메인 분리식 (방식 B)** 적용
    - 공통 에러코드: `CommonErrorCode`
    - 도메인별 에러코드: `UserErrorCode`, `AuthErrorCode` 등
    - 각 도메인별 예외 클래스는 해당 도메인 ErrorCode 포함

---

#### ErrorResponse 표준 구조
```
{
  "timestamp": "2025-10-04T12:34:56",
  "statusCode": 401,
  "errorCode": "A1001",
  "message": "비밀번호가 올바르지 않습니다.",
  "path": "/auth/login/email"
}
```

---

#### ErrorCode 설계 규칙
- `enum`으로 정의, 공통 인터페이스 `ErrorCode` 구현
- 코드 + 메시지 + HTTP 상태 포함

**예시) ErrorCode 인터페이스**
```
public interface ErrorCode {
    String getCode();
    String getMessage();
    int getStatus();
}
```

---

#### CommonErrorCode
```
public enum CommonErrorCode implements ErrorCode {
    INTERNAL_SERVER_ERROR("C1000", "서버 내부 오류", 500),
    INVALID_REQUEST("C1001", "잘못된 요청", 400),
    UNAUTHORIZED("C1002", "인증 필요", 401),
    FORBIDDEN("C1003", "접근 불가", 403);
}
```

---

#### UserErrorCode
```
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("U1001", "사용자를 찾을 수 없습니다.", 404),
    DUPLICATE_EMAIL("U1002", "이미 등록된 이메일입니다.", 400),
    INVALID_PASSWORD("U1003", "비밀번호가 올바르지 않습니다.", 401);
}
```
---

#### CustomException
```
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
```

---

#### DTO & Validation
- Controller 계층에서는 Entity 직접 반환 금지 → DTO 사용
- DTO 네이밍: `UserRequest`, `UserResponse`
- Validation: `javax.validation` 사용
```
@NotNull
@Email
private String email;
```

---

### 로깅 (Logging)
- `System.out.println` 금지 → `Slf4j` 사용
- 로그 레벨 규칙
    - `DEBUG`: 개발용 상세 로그
    - `INFO`: 상태/흐름
    - `WARN`: 비정상 징후
    - `ERROR`: 치명적 오류
- 민감정보(비밀번호, 토큰 등) 로그 출력 금지

---

### 테스트 (Test Convention)
- JUnit5 + AssertJ
- 테스트 메서드 네이밍: `should_동작_when_조건`
- Given-When-Then 패턴 유지
- **단위 테스트**: `@ExtendWith(MockitoExtension.class)`
- **통합 테스트**: `@SpringBootTest`
- Test DB: H2 또는 Docker 기반 MySQL
- MockMvc 사용 시 `jsonPath` 기반 검증

---

### 기타 규칙 (Misc)
- Lombok: `@Data` 금지 → `@Getter`, `@Builder` 조합 사용
- 상수는 `public static final` + UPPER_SNAKE_CASE
- 매직 넘버 금지 → 상수화
- `equals` & `hashCode`는 PK 기반 구현
- Controller 응답은 `ResponseEntity<T>`로 감싸기

## 2. 깃 워크플로우 (Git Workflow)

### 브랜치 전략
- `main`: 항상 배포 가능한 상태
- `develop`: 개발 통합 브랜치
- `feature/*`: 기능 개발 (`feature/login-api`)
- `release/*`: 배포 준비
- `hotfix/*`: 운영 긴급 수정

ex) feature/#{issue_number}_issue_title

**develop** 브랜치를 기반으로 이슈(기능)별 브랜치를 생성 후 커밋한다.
- 커밋 메시지, 이슈, PR 제목/본문은 모두 한글로 작성한다.
- PR 본문에는 반드시 "왜 이렇게 진행했는지"에 대한 배경과 선택 이유를 기록한다.
- issue, pr을 올릴 때 github mcp를 사용한다.

### PR 템플릿
```
# [{Feat|Docs|Bug 등}]{title}(#{issue_number})
## 📌 작업 내용
- [ ] 기능 구현
- [ ] 버그 수정
- [ ] 리팩토링

## 🔎 상세 내용
-  

## 🧠 진행 이유
-  

## ✅ 체크리스트
- [ ] 단위 테스트 통과
- [ ] 코드 컨벤션 준수
- [ ] 관련 문서 업데이트

```
### 이슈 템플릿

```
# [{Feat|Docs|Bug 등}]{title}
## 📄 작업 내용
-  

## 🎯 목표
-  

## ✅ 완료 조건
- [ ] 

```
### 커밋 컨벤션
```
feat: 새로운 기능 추가  
fix: 버그 수정  
docs: 문서 수정  
style: 코드 포맷 변경  
refactor: 코드 리팩토링  
test: 테스트 코드 추가  
chore: 빌드/환경 설정
```

---

## 3. TDD 원칙

- **테스트 코드 먼저 작성 후 기능 구현**
- 단위 테스트 커버리지: **70% 이상 목표**
- CI에서 `./gradlew test` 자동 실행, 실패 시 머지 불가

---

## 4. 태스크 완료 시 문서화

- 태스크 완료 후 Results.md에 기록
- API 변경 시 Swagger/OpenAPI 최신화

예시:
```
# [TASK] 로그인 기능 구현
## 개요
- JWT 기반 로그인 API 구현

## 상세 내용
- AccessToken/RefreshToken 발급
- Redis 기반 RefreshToken 관리

## 회고/이슈
- Redis TTL 설정 문제 → 해결 방안 기록
```

---

## 5. To-Do List 관리

- todolist.md에 관리
- 각 태스크는 이슈 생성 후 이슈넘버를 기록한다.
- 이슈 → 브랜치 → PR/머지 → 자동 종료
- 사용자가 지시한 태스크를 완료하면 todolist.md에서 해당 항목을 `- [x]`로 즉시 반영하고 변경 사항을 공유한다.

예시:

```
- [ ] 로그인 API 설계 (#1)
- [ ] JWT 발급 로직 테스트 작성 (#2)
- [ ] Redis TTL 검증 (#3)
- [ ] Swagger 문서 업데이트 (#4)
```
---


## 6. 환경변수 관리

- 환경변수는 yml 포맷을 사용한다.
- application-local.yml / application-dev.yml / application-prod.yml을 따로 관리하며 이는 깃에 반영하지 않는다.
- application.yml에서 분기를 설정하고, 현재 진행하는 개발에서 application-local.yml만 작성, 사용한다.


## 7. 보안 규칙

- 비밀번호, 토큰, 개인정보는 로그 출력 금지
- 환경변수는 `.env` 또는 Secret Manager에서 관리
- JWT/세션 키, API Key 등 민감정보는 `application-*.yml`이 아닌 OS 환경변수로 주입
- DB 계정은 최소 권한 원칙 적용
- 외부 서비스 연동 시 반드시 HTTPS 사용

## 8. DB & 마이그레이션 규칙

- 모든 DDL은 `resources/db/migration` 경로에서 관리 (Flyway 또는 Liquibase 사용)
- DB 스키마 변경 시 PR에 반드시 마이그레이션 파일 포함
- PK는 UUID 사용 → DB에는 `BINARY(16)`로 저장
- FK 제약조건은 반드시 명시
- 인덱스 생성 시 목적을 주석으로 함께 기록  

