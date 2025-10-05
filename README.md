# OAuth2 Practice Backend

Spring Boot 기반으로 이메일 및 소셜(OAuth2) 인증을 통합 제공하기 위한 백엔드 프로젝트입니다. JWT + Redis를 토큰 관리의 핵심 축으로 삼으며, 사용자 계정과 프로바이더 매핑을 명확히 분리하여 확장성을 확보합니다.

## 📈 개발 진행 현황
| 구분 | 이슈 | 상태 | 비고 |
| --- | --- | --- | --- |
| 0. Foundation | #1, #36 관련 선행 작업 | ✅ 완료 | 패키지 구조, Gradle 의존성, 프로필 설정 | 
| 1. 데이터 모델링 | [#1](https://github.com/EomYoosang/oauth2-practice-opencode/issues/1) | 🚧 진행 중 | `feature/issue-1-user-domain` (PR [#37](https://github.com/EomYoosang/oauth2-practice-opencode/pull/37)) |
| 2. 이메일 인증 흐름 | [#4](https://github.com/EomYoosang/oauth2-practice-opencode/issues/4)~[#8](https://github.com/EomYoosang/oauth2-practice-opencode/issues/8) | ⏳ 예정 | DTO/Service/Token/세션 무효화 | 
| 3. 소셜 로그인 | [#9](https://github.com/EomYoosang/oauth2-practice-opencode/issues/9)~[#12](https://github.com/EomYoosang/oauth2-practice-opencode/issues/12) | ⏳ 예정 | OAuth 클라이언트 설정, 프로바이더 매핑 |
| 4. 토큰 & 세션 관리 | [#13](https://github.com/EomYoosang/oauth2-practice-opencode/issues/13)~[#17](https://github.com/EomYoosang/oauth2-practice-opencode/issues/17) | ⏳ 예정 | JWT 전략, Redis 키 구조, device 관리 |
| 5. 보안 & 레이트 리밋 | [#18](https://github.com/EomYoosang/oauth2-practice-opencode/issues/18)~[#21](https://github.com/EomYoosang/oauth2-practice-opencode/issues/21) | ⏳ 예정 | 레이트 리밋, CSRF, 감사 로그, MFA |
| 6. 이메일 전송 | [#22](https://github.com/EomYoosang/oauth2-practice-opencode/issues/22)~[#24](https://github.com/EomYoosang/oauth2-practice-opencode/issues/24) | ⏳ 예정 | 템플릿, 재발송 제어 |
| 7. 운영 & 모니터링 | [#25](https://github.com/EomYoosang/oauth2-practice-opencode/issues/25)~[#27](https://github.com/EomYoosang/oauth2-practice-opencode/issues/27) | ⏳ 예정 | Redis HA, 모니터링, 카오스 테스트 |
| 8. 테스트 & 품질 | [#28](https://github.com/EomYoosang/oauth2-practice-opencode/issues/28)~[#31](https://github.com/EomYoosang/oauth2-practice-opencode/issues/31) | ⏳ 예정 | 단위/통합/보안 테스트, 커버리지 |
| 9. 문서 & 배포 준비 | [#32](https://github.com/EomYoosang/oauth2-practice-opencode/issues/32)~[#35](https://github.com/EomYoosang/oauth2-practice-opencode/issues/35) | ⏳ 예정 | OpenAPI, 운영 가이드, 템플릿 |

세부 태스크는 `todolist.md`와 GitHub 이슈로 동기화되어 있으며, 모든 커밋/PR은 한글로 관리합니다.

## 📚 현재 API 명세 스냅샷
아래 명세는 PRD를 기반으로 하며, 구현 상태는 **진행 예정**입니다.

| 엔드포인트 | 메서드 | 설명 | 상태 |
| --- | --- | --- | --- |
| `/auth/register/email` | POST | 이메일 회원가입 | ⏳ 예정 |
| `/auth/verify?token=` | GET | 이메일 인증 | ⏳ 예정 |
| `/auth/login/email` | POST | 이메일 로그인 + 토큰 발급 | ⏳ 예정 |
| `/auth/token/refresh` | POST | 토큰 재발급 | ⏳ 예정 |
| `/auth/password/reset/request` | POST | 비밀번호 재설정 요청 | ⏳ 예정 |
| `/auth/password/reset/confirm` | POST | 비밀번호 재설정 확정 | ⏳ 예정 |
| `/auth/login/oauth2/{provider}` | GET | 소셜 로그인 콜백 (Google/Kakao/Apple) | ⏳ 예정 |

구현이 완료되면 Swagger/OpenAPI 명세와 함께 본 표를 업데이트할 예정입니다.

## 🧱 엔티티 설계 요약 (진행 중)
현재 PR #37에서 다루고 있는 핵심 도메인 모델입니다.

- **공통 UUID PK (`PrimaryKeyEntity`)**
  - `UUID` + `BINARY(16)` 칼럼으로 모든 엔티티의 식별자를 관리합니다.
- **User (`src/main/java/com/eomyoosang/oauth2/user/domain/User.java`)**
  - 필수 필드: `displayName`, `status`, `joinedAt`, `lastLoginAt`
  - 연관관계: `EmailAccount` (1:1), `OAuthAccount` (1:N)
  - 도메인 메서드: 표시 이름 변경, 상태 전환, 마지막 로그인 갱신, 계정 등록/제거
- **EmailAccount (`src/main/java/com/eomyoosang/oauth2/user/domain/EmailAccount.java`)**
  - 필드: `email`, `passwordHash`, `verified`, `registeredAt`, `lastLoginAt`
  - `User`와 1:1 연관, 비밀번호 변경/검증/로그인 기록 메서드 제공
- **OAuthAccount 상속 트리 (`provider/domain`)**
  - 기반 클래스 `OAuthAccount`: `providerType`, `providerUserId`, `email`, `displayName`, `profileImageUrl`, `linkedAt`, `lastLoginAt`
  - 파생 클래스: `GoogleAccount`, `KakaoAccount`, `AppleAccount`
  - 모든 연관 키 역시 `UUID` 기반으로 정렬

추후 엔티티 확장 시 이 구조를 유지하며 DDD 레이어를 세분화합니다.

## 🔐 패스워드 해싱 구성
- `support.security.PasswordHasher`가 BCrypt 기반 해싱/검증/재해싱 여부 판단을 담당합니다.
- `security.password.bcrypt-strength`(기본 12, 테스트 4) 프로퍼티로 강도를 제어하며, 환경 변수 `SECURITY_BCRYPT_STRENGTH`로 재정의할 수 있습니다.
- `user.application.EmailAccountRegistrationService`가 이메일 계정 등록 시 해싱을 적용하고, Issue #3에서 복잡도·유출 검증 절차를 추가할 계획입니다.

## 🧪 테스트 & 빌드
```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew test
```
- 테스트 환경은 H2 (MySQL 호환 모드)를 사용하며, `src/test/resources/application.yml`에서 설정합니다.

## 🔀 브랜치 & .gitignore 정책
- 이슈별 `feature/*` 브랜치를 사용하고, 모든 PR은 `develop`을 대상으로 생성합니다.
- 검토 완료 후 `develop` → `main` 순으로 머지합니다.
- `.gitignore` 정책은 절대적으로 준수하며, `git add -f` 등으로 예외를 두지 않습니다.
- 커밋 메시지·이슈·PR 제목/본문은 모두 한글로 작성, PR 본문에는 반드시 진행 이유를 포함합니다.

## 📄 참고 문서
- [PRD](prd.md)
- [개발 규칙 (rule/development_rules.md)](rule/development_rules.md) *(Git 추적 대상이 아니므로 로컬에서 확인)*
- [To-Do List](todolist.md)

---
문의나 개선 제안은 GitHub 이슈로 남겨 주세요.
