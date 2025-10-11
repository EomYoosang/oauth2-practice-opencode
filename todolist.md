# To-Do List

## 🔥 High Priority
- [x] (P0) 생성자 주입 방식 @RequiredArgsConstructor 통일 ([#51](https://github.com/EomYoosang/oauth2-practice-opencode/issues/51))

## 0. Foundation
- [x] 확정된 도메인 패키지 구조 수립 (`auth`, `user`, `token`, `provider`, `config`, `support`) 및 Rule 기반 DDD 디렉토리 반영 ([#53](https://github.com/EomYoosang/oauth2-practice-opencode/issues/53))
- [x] Spring Security, OAuth2 Client, Redis, Validation, Lombok 등 필수 의존성 검토 및 Gradle 설정 확정 ([#45](https://github.com/EomYoosang/oauth2-practice-opencode/issues/45))
- [x] 애플리케이션 공통 설정 작성 (`application.yml` 기본값, 프로필 분기, CORS 정책 뼈대) ([#45](https://github.com/EomYoosang/oauth2-practice-opencode/issues/45))

## 1. 데이터 모델링
- [x] `User`, `EmailAccount`, `GoogleAccount`, `KakaoAccount`, `AppleAccount` 엔티티 및 연관관계 설계 (LAZY, Builder 적용) ([#1](https://github.com/EomYoosang/oauth2-practice-opencode/issues/1))
- [x] 패스워드 해싱 처리용 지원 컴포넌트 정의 (BCrypt) 및 엔티티 저장 로직 연동 ([#2](https://github.com/EomYoosang/oauth2-practice-opencode/issues/2))
- [x] 비밀번호 정책/복잡도/유출 검사 정책 수립 및 검증기 구현 계획 수립 ([#3](https://github.com/EomYoosang/oauth2-practice-opencode/issues/3))

## 2. 인증 흐름 (이메일)
- [x] 이메일 회원가입 DTO/Validator/Controller/Service 작성 (테스트 선행) ([#4](https://github.com/EomYoosang/oauth2-practice-opencode/issues/4))
- [x] 이메일 인증 토큰 발급·저장·검증 로직 구현 (`auth:ev:{token}`, GETDEL 원자화) ([#5](https://github.com/EomYoosang/oauth2-practice-opencode/issues/5))
- [x] 이메일 로그인 및 Refresh Token 발급/회전 서비스 구현 (deviceId 고려) ([#6](https://github.com/EomYoosang/oauth2-practice-opencode/issues/6))
- [ ] 비밀번호 재설정 요청/확정 API 구현 (쿨다운 및 단일 사용 처리) ([#7](https://github.com/EomYoosang/oauth2-practice-opencode/issues/7))
- [ ] 비밀번호 변경 시 전체 세션 무효화 및 알림 UX 정의 ([#8](https://github.com/EomYoosang/oauth2-practice-opencode/issues/8))

## 3. 소셜 로그인 (OAuth2)
- [ ] Google/Kakao/Apple OAuth 클라이언트 설정 및 리디렉트 URI 화이트리스트 구성 ([#9](https://github.com/EomYoosang/oauth2-practice-opencode/issues/9))
- [ ] OIDC id_token 검증/nonce/PKCE 플로우 구현 및 프로바이더별 응답 매핑 ([#10](https://github.com/EomYoosang/oauth2-practice-opencode/issues/10))
- [ ] 기존 사용자 매핑/신규 통합 사용자 생성 시나리오 및 충돌 처리 구현 ([#11](https://github.com/EomYoosang/oauth2-practice-opencode/issues/11))
- [ ] 프로바이더 장애·동의 철회 이벤트 동기화 배치/웹훅 처리 설계 ([#12](https://github.com/EomYoosang/oauth2-practice-opencode/issues/12))

## 4. 토큰 & 세션 관리
- [ ] JWT Access/Refresh Token 생성 정책 및 서명키 관리 컴포넌트 구현 ([#13](https://github.com/EomYoosang/oauth2-practice-opencode/issues/13))
- [ ] Redis 키 전략 구현 (`auth:rt:{userId}:{deviceId}`, `auth:dev:{userId}` 등) 및 Lua 기반 RTR 원자성 확보 ([#14](https://github.com/EomYoosang/oauth2-practice-opencode/issues/14))
- [ ] deviceId 발급/최대 허용 개수/만료 정책 정의 및 레지스트리 관리 구현 ([#15](https://github.com/EomYoosang/oauth2-practice-opencode/issues/15))
- [ ] Refresh Token 재사용 감지 및 보안 알림 로직 구현 ([#16](https://github.com/EomYoosang/oauth2-practice-opencode/issues/16))
- [ ] Access Token 블랙리스트(선택) 기능 토글 가능하도록 설계 ([#17](https://github.com/EomYoosang/oauth2-practice-opencode/issues/17))

## 5. 보안 & 레이트 리밋
- [ ] 로그인/회원가입/비밀번호 재설정/CAPTCHA 연동을 포함한 레이트 리밋 규칙 구현 (Redis 기반) ([#18](https://github.com/EomYoosang/oauth2-practice-opencode/issues/18))
- [ ] CSRF 방어 전략 확정 (SameSite+Double Submit) 및 CORS 화이트리스트 적용 ([#19](https://github.com/EomYoosang/oauth2-practice-opencode/issues/19))
- [ ] 감사 로그 스키마 정의 및 민감 이벤트(로그인 실패, 계정 연결, 권한 변경) 로깅 구현 ([#20](https://github.com/EomYoosang/oauth2-practice-opencode/issues/20))
- [ ] MFA/스텝업 인증 확장 포인트 정의 및 민감 엔드포인트 보호 적용 ([#21](https://github.com/EomYoosang/oauth2-practice-opencode/issues/21))

## 6. 이메일 전송
- [ ] 이메일 템플릿(I18N)과 메일 발송 서비스 구현, SPF/DKIM/DMARC 설정 문서화 ([#22](https://github.com/EomYoosang/oauth2-practice-opencode/issues/22))
- [ ] 인증/비밀번호 재설정 메일 토큰 전달 링크 HTTPS/짧은 TTL 보장 여부 검증 ([#23](https://github.com/EomYoosang/oauth2-practice-opencode/issues/23))
- [ ] 메일 재발송 레이트 리밋 및 실패 알림 처리 구현 ([#24](https://github.com/EomYoosang/oauth2-practice-opencode/issues/24))

## 7. 운영/모니터링
- [ ] Redis Sentinel/Cluster 구성 가이드 및 장애 시나리오 대응 정책 문서화 ([#25](https://github.com/EomYoosang/oauth2-practice-opencode/issues/25))
- [ ] Correlation ID/요청 추적 로깅 구성, 토큰 회전 실패율 알림 지표 세팅 ([#26](https://github.com/EomYoosang/oauth2-practice-opencode/issues/26))
- [ ] Chaos/Failover 테스트 계획 수립 및 실행 체크리스트 추가 ([#27](https://github.com/EomYoosang/oauth2-practice-opencode/issues/27))

## 8. 테스트 & 품질
- [ ] TDD 플로우에 따라 단위 테스트(회원가입, 인증, 로그인, 토큰 회전 등) 작성 ([#28](https://github.com/EomYoosang/oauth2-practice-opencode/issues/28))
- [ ] 통합 테스트(이메일 인증→로그인, 소셜 로그인 플로우) 및 시간 의존 케이스 구현 ([#29](https://github.com/EomYoosang/oauth2-practice-opencode/issues/29))
- [ ] 보안 회귀 테스트/페네트레이션 시나리오 계획 수립 (Brute Force, CSRF, RT 재사용) ([#30](https://github.com/EomYoosang/oauth2-practice-opencode/issues/30))
- [ ] 테스트 커버리지 측정 및 70% 이상 달성 확인 ([#31](https://github.com/EomYoosang/oauth2-practice-opencode/issues/31))

## 9. 문서 & 배포 준비
- [ ] Swagger/OpenAPI 명세 작성 및 예외/에러코드 문서화 ([#32](https://github.com/EomYoosang/oauth2-practice-opencode/issues/32))
- [ ] 운영자용 가이드(토큰 무효화 절차, 계정 병합 정책) 정리 ([#33](https://github.com/EomYoosang/oauth2-practice-opencode/issues/33))
- [ ] 완료 조건(DoD) 체크리스트 기반 릴리즈 노트/위키 업데이트 ([#34](https://github.com/EomYoosang/oauth2-practice-opencode/issues/34))
- [ ] GitHub 이슈/브랜치/PR 템플릿과 todolist 연동 프로세스 적용 ([#35](https://github.com/EomYoosang/oauth2-practice-opencode/issues/35))
