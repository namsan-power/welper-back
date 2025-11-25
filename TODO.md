# TODO List - Welper Backend

## ✅ 완료된 작업
- 공통 응답 포맷 (`ApiResponse`) 구현
- 인증/유저 기능 재구축 (User/Admin 로그인 분리)
- 클라이언트 관리 기능 재구축
- 보고서 관리 기능 기본 구조 재구축

---

## 🔧 1. 개발 환경 설정

### 데이터베이스 설정
- [ ] `src/main/resources/application.properties` 또는 `application.yml` 파일 생성
  ```properties
  # MySQL 설정
  spring.datasource.url=jdbc:mysql://localhost:3306/welper_db?useSSL=false&serverTimezone=Asia/Seoul
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  
  # JWT 설정
  jwt.secret=your-secret-key-here
  jwt.expiration=86400000
  ```

### 초기 데이터 설정
- [ ] 첫 Admin 계정을 DB에 직접 삽입하거나 migration script 작성
  ```sql
  INSERT INTO users (email, password, name, role, agency_name, status, created_at)
  VALUES ('admin@welper.com', '$2a$10$encoded_password', '관리자', 'AGENCY_ADMIN', '서울복지관', 'ACTIVE', NOW());
  ```

---

## 🧪 2. 서버 실행 및 기본 테스트

### 빌드 및 실행
- [ ] `gradlew clean build` - 빌드 성공 확인
- [ ] `gradlew bootRun` - 서버 실행 (포트: 8080)
- [ ] Swagger UI 접속: `http://localhost:8080/swagger-ui.html`

### API 기본 테스트 (Swagger 또는 Postman)
**인증**
- [ ] `POST /api/v1/auth/admin/login` - 관리자 로그인
- [ ] `POST /api/v1/auth/user/login` - 일반 유저 로그인
- [ ] `POST /api/v1/users` - 관리자가 유저 생성 (Header: Authorization: Bearer {token})

**클라이언트**
- [ ] `POST /api/v1/clients` - 클라이언트 등록
- [ ] `GET /api/v1/users/{userId}/clients` - 내 클라이언트 목록
- [ ] `GET /api/v1/clients/{clientId}` - 클라이언트 상세 조회
- [ ] `PATCH /api/v1/clients/{clientId}` - 클라이언트 정보 수정
- [ ] `DELETE /api/v1/clients/{clientId}` - 클라이언트 삭제

**보고서**
- [ ] `POST /api/v1/reports` - 보고서 생성
- [ ] `GET /api/v1/reports/{reportId}` - 보고서 조회
- [ ] `DELETE /api/v1/reports/{reportId}` - 보고서 삭제

---

## 🚀 3. 추가 구현 필요 사항

### 파일 업로드 구현
현재 `voiceFileUrl`로 URL만 받고 있음. 실제 파일 업로드 로직 구현 필요:
- [ ] AWS S3 버킷 설정 또는 로컬 파일 스토리지 구현
- [ ] `POST /api/v1/files/upload` 엔드포인트 생성
- [ ] Multipart 파일 업로드 처리 로직 구현

### AI 모델 연동
- [ ] `ReportService.createReport()`의 Mock AI 분석 로직을 실제 AI API와 연동
- [ ] AI 응답 데이터를 `aiAnalysisData` 필드에 JSON 형태로 저장
- [ ] 에러 핸들링 (AI API 호출 실패 시 처리)

### Security 설정 정교화
- [ ] `SecurityConfig` 파일에서 URL별 권한 설정
  ```java
  .requestMatchers("/api/v1/auth/**").permitAll()
  .requestMatchers("/api/v1/users").hasRole("AGENCY_ADMIN")
  .requestMatchers("/api/v1/clients/**").hasAnyRole("CASE_MANAGER", "AGENCY_ADMIN")
  ```
- [ ] JWT 토큰에서 User 정보 추출하여 SecurityContext에 저장하는 로직 검증
- [ ] `@AuthenticationPrincipal`에서 실제 User email 추출 테스트

### 이메일 발송 기능
- [ ] 비밀번호 재설정 기능 구현
  - `POST /api/v1/auth/reset-password` (이메일 입력)
  - `POST /api/v1/auth/update-password` (새 비밀번호 설정)
- [ ] JavaMailSender 설정 및 SMTP 연동

### 보고서 상세 필드 구현
API 명세의 "욕구사정" 관련 상세 필드들을 별도 Entity로 구현:
- [ ] `NeedsAssessment` Entity 생성 (의식주, 건강, 경제 등)
- [ ] Report와 1:N 관계 설정
- [ ] DTO 및 Service 로직 추가

---

## 🌐 4. 프론트엔드 연동 준비

- [ ] CORS 설정 확인 (`WebMvcConfigurer` 또는 `SecurityConfig`)
- [ ] API 명세서 프론트엔드 팀과 공유
- [ ] 프론트엔드 개발 서버와 연동 테스트

---

## 📝 5. 코드 정리 및 문서화

- [ ] 사용하지 않는 DTO 파일 정리 확인
- [ ] README.md 업데이트 (프로젝트 설명, 실행 방법)
- [ ] API 명세서 최종 검토 및 Swagger 주석 추가

---

## ⚠️ 주의사항

1. **MongoDB 의존성**: `build.gradle`에 MongoDB 의존성이 남아있지만, 현재 MySQL만 사용 중입니다. 필요 없다면 제거하세요.
2. **보안**: JWT Secret Key는 환경변수 또는 외부 설정 파일로 관리하세요.
3. **테스트 코드**: 주요 Service 로직에 대한 단위 테스트 작성을 권장합니다.
