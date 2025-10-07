# OAuth2 Practice Backend

Spring Boot 기반으로 이메일 및 소셜(OAuth2) 인증을 통합 제공하기 위한 백엔드 프로젝트입니다. JWT + Redis를 토큰 관리의 핵심 축으로 삼으며, 사용자 계정과 프로바이더 매핑을 명확히 분리하여 확장성을 확보합니다.

## 📈 개발 진행 현황
| 구분 | 이슈 | 상태 | 비고 |
| --- | --- | --- | --- |

| 0. Foundation | #1, #36 관련 선행 작업 | ✅ 완료 | 패키지 구조, Gradle 의존성, 프로필 설정 |
| 1. 데이터 모델링 | [#1](https://github.com/EomYoosang/oauth2-practice-opencode/issues/1)~[#3](https://github.com/EomYoosang/oauth2-practice-opencode/issues/3) | ✅ 완료 | UUID PK, 패스워드 정책/해싱 (PR [#39](https://github.com/EomYoosang/oauth2-practice-opencode/pull/39), [#40](https://github.com/EomYoosang/oauth2-practice-opencode/pull/40), [#41](https://github.com/EomYoosang/oauth2-practice-opencode/pull/41)) |
| 2. 이메일 인증 흐름 | [#4](https://github.com/EomYoosang/oauth2-practice-opencode/issues/4)~[#8](https://github.com/EomYoosang/oauth2-practice-opencode/issues/8) | 🚧 진행 중 | 이메일 회원가입 초안 완료, 인증 토큰 발급·검증 구현 (Issue [#5](https://github.com/EomYoosang/oauth2-practice-opencode/issues/5), PR [#48](https://github.com/EomYoosang/oauth2-practice-opencode/pull/48)) |
| 3. 소셜 로그인 | [#9](https://github.com/EomYoosang/oauth2-practice-opencode/issues/9)~[#12](https://github.com/EomYoosang/oauth2-practice-opencode/issues/12) | ⏳ 예정 | OAuth 클라이언트 설정, 프로바이더 매핑 |
| 4. 토큰 & 세션 관리 | [#13](https://github.com/EomYoosang/oauth2-practice-opencode/issues/13)~[#17](https://github.com/EomYoosang/oauth2-practice-opencode/issues/17) | ⏳ 예정 | JWT 전략, Redis 키 구조, device 관리 |
| 5. 보안 & 레이트 리밋 | [#18](https://github.com/EomYoosang/oauth2-practice-opencode/issues/18)~[#21](https://github.com/EomYoosang/oauth2-practice-opencode/issues/21) | ⏳ 예정 | 레이트 리밋, CSRF, 감사 로그, MFA |
| 6. 이메일 전송 | [#22](https://github.com/EomYoosang/oauth2-practice-opencode/issues/22)~[#24](https://github.com/EomYoosang/oauth2-practice-opencode/issues/24) | ⏳ 예정 | 템플릿, 재발송 제어 |
| 7. 운영 & 모니터링 | [#25](https://github.com/EomYoosang/oauth2-practice-opencode/issues/25)~[#27](https://github.com/EomYoosang/oauth2-practice-opencode/issues/27) | ⏳ 예정 | Redis HA, 모니터링, 카오스 테스트 |
| 8. 테스트 & 품질 | [#28](https://github.com/EomYoosang/oauth2-practice-opencode/issues/28)~[#31](https://github.com/EomYoosang/oauth2-practice-opencode/issues/31) | ⏳ 예정 | 단위/통합/보안 테스트, 커버리지 |
| 9. 문서 & 배포 준비 | [#32](https://github.com/EomYoosang/oauth2-practice-opencode/issues/32)~[#35](https://github.com/EomYoosang/oauth2-practice-opencode/issues/35) | ⏳ 예정 | OpenAPI, 운영 가이드, 템플릿 |

세부 태스크는 `todolist.md`와 GitHub 이슈로 동기화되어 있으며, 모든 커밋/PR은 한글로 관리합니다.

## 📚 현재 API 명세 스냅샷

| 엔드포인트 | 메서드 | 설명 | 상태 |
| --- | --- | --- | --- |
| `/auth/register/email` | POST | 이메일 회원가입 | ✅ 구현 (초안) |
| `/auth/verify?token=` | GET | 이메일 인증 | ✅ 구현 (토큰 소모 + 상태 갱신) |
| `/auth/login/email` | POST | 이메일 로그인 + 토큰 발급 | ⏳ 예정 |
| `/auth/token/refresh` | POST | 토큰 재발급 | ⏳ 예정 |
| `/auth/password/reset/request` | POST | 비밀번호 재설정 요청 | ⏳ 예정 |
| `/auth/password/reset/confirm` | POST | 비밀번호 재설정 확정 | ⏳ 예정 |
| `/auth/login/oauth2/{provider}` | GET | 소셜 로그인 콜백 (Google/Kakao/Apple) | ⏳ 예정 |

구현이 완료되면 Swagger/OpenAPI 명세와 함께 본 표를 계속 업데이트할 예정입니다.

## 🧱 엔티티 설계 요약
- **공통 UUID PK (`PrimaryKeyEntity`)**: 모든 엔티티 식별자를 `UUID` + `BINARY(16)`으로 관리합니다.
- **User (`src/main/java/com/eomyoosang/oauth2/user/domain/User.java`)**: `displayName`, `status`, `joinedAt`, `lastLoginAt` 등을 보유하며 `EmailAccount`(1:1), `OAuthAccount`(1:N) 관계를 유지합니다.
- **EmailAccount (`src/main/java/com/eomyoosang/oauth2/user/domain/EmailAccount.java`)**: 이메일, 해시된 비밀번호, 인증 여부를 관리하고 도메인 메서드로 검증/로그인 기록을 제공합니다.
- **OAuthAccount 상속 트리 (`provider/domain`)**: `GoogleAccount`, `KakaoAccount`, `AppleAccount`가 공통 속성을 공유하며 프로바이더별 확장을 대비합니다.

## 🔐 패스워드 정책 & 해싱 구성
- `support.security.PasswordHasher`가 BCrypt 기반 해싱/검증/재해싱 여부 판단을 담당합니다.
- 복잡도 기본값(최소 12자, 대/소문자·숫자·특수문자 포함)은 `security.password.policy.*` 프로퍼티로 제어하며, 환경변수(`SECURITY_PASSWORD_MIN_LENGTH` 등)로 재정의할 수 있습니다.
- `security.password.compromised.*` 설정은 유출 비밀번호 검사를 토글하며, 현재는 No-Op 구현이 기본입니다.
- `user.application.EmailAccountRegistrationService`는 해싱 이전에 정책 검증을 수행하고, Issue #4의 회원가입 서비스가 이를 호출합니다.

## 🧪 테스트 & 빌드
```bash
./gradlew test
./gradlew bootRun
```
- Gradle Wrapper가 JDK 17을 자동으로 탐색하므로 별도 `JAVA_HOME` 설정 없이 실행할 수 있습니다. (macOS는 `/usr/libexec/java_home`, Windows/Linux는 대표 설치 경로를 우선 탐색)
- `local` 프로필은 H2 인메모리 데이터베이스(MySQL 모드)를 사용하므로 별도 MySQL 인스턴스가 필요하지 않습니다. 테스트 환경 설정은 `src/test/resources/application.yml`에 정리되어 있습니다.

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
