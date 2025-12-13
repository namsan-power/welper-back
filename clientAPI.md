# Client Management API

클라이언트 관리를 위한 CRUD API 문서입니다.

---

# 1. 클라이언트 목록 조회

```
GET /api/v1/clients
```

## **기능 설명**

로그인한 사용자의 역할에 따라 클라이언트 목록을 조회합니다.

**백엔드 로직:**
1. Spring Security에서 현재 로그인한 사용자의 userId를 추출
2. User 테이블에서 해당 사용자 조회 및 role 확인
3. 역할별 필터링:
   - **SUPERVISOR**: `deletedAt IS NULL`인 모든 클라이언트 조회
   - **CASE_MANAGER**: `assignedManagerId = userId AND deletedAt IS NULL`인 클라이언트만 조회
4. `registrationDate` 기준 내림차순 정렬
5. Entity를 DTO로 변환하여 응답

**프론트엔드 연동:**
- 로그인 후 메인 화면에서 자동으로 호출되어 클라이언트 리스트 표시
- 테이블 형태로 표시 (번호, 이름, 생년월일, 성별, 연락처, 주소, 수정, 상태, 상세 버튼)

## **BE 구현 주의 사항**

1. **인증 필수**: JWT 토큰이 없으면 401 Unauthorized 반환
2. **역할 기반 필터링**: SUPERVISOR와 CASE_MANAGER의 조회 범위가 다름
3. **Soft Delete**: `deletedAt IS NULL` 조건으로 삭제된 클라이언트 제외
4. **N+1 문제 주의**: `assignedManager` 관계를 fetch join으로 최적화 권장
5. **정렬**: 최신 접수 순으로 정렬 (registrationDate DESC)

---

## **Request (요청)**

### **Headers**

```
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

### **Request Body (JSON)**

없음 (GET 요청)

---

## Response (응답)

### 성공 응답 (Status)

- **200 OK**: 정상적으로 클라이언트 목록 조회 성공

### Response Body (JSON)

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": [
    {
      "caseNumber": "string",
      "clientName": "string",
      "birthDate": "YYYY-MM-DD",
      "gender": "string",
      "contactNumber": "string",
      "address": "string",
      "assignedManagerName": "string",
      "caseStatus": "string"
    }
  ],
  "error": null
}
```

### Response Body 예시

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": [
    {
      "caseNumber": "2024-001",
      "clientName": "홍길동",
      "birthDate": "1950-01-01",
      "gender": "남",
      "contactNumber": "010-1234-5678",
      "address": "서울시 강남구",
      "assignedManagerName": "김복지",
      "caseStatus": "RECEPTION"
    },
    {
      "caseNumber": "2024-002",
      "clientName": "김철수",
      "birthDate": "1960-05-15",
      "gender": "남",
      "contactNumber": "010-9876-5432",
      "address": "서울시 서초구",
      "assignedManagerName": "이담당",
      "caseStatus": "SELECTED"
    }
  ],
  "error": null
}
```

### 오류 응답 (Status)

- **401 Unauthorized**: 인증 토큰이 없거나 유효하지 않음
- **404 Not Found**: 사용자 정보를 찾을 수 없음

### 오류 응답 Body (JSON)

```json
{
  "success": false,
  "message": "유효하지 않은 토큰입니다.",
  "code": 401,
  "data": null,
  "error": {
    "code": "AUTH004",
    "message": "유효하지 않은 토큰입니다."
  }
}
```

---

## 기타

### 비고

- SUPERVISOR는 전체 클라이언트를 볼 수 있으므로 통계나 관리 목적으로 활용
- CASE_MANAGER는 자신의 클라이언트만 보므로 업무 집중 가능
- 빈 배열(`[]`) 반환도 정상 응답 (아직 배정된 클라이언트가 없는 경우)

### 관련 문서/링크

- Entity: `Client.java`, `User.java`
- DTO: `ClientListDto.java`
- Service: `ClientService.getClientList()`

---

# 2. 클라이언트 생성

```
POST /api/v1/clients
```

## **기능 설명**

새로운 클라이언트를 등록합니다. 사례번호는 백엔드에서 자동으로 생성됩니다.

**백엔드 로직:**
1. Request Body 유효성 검사 (Jakarta Validation)
2. 개인정보 동의 여부 확인 (`privacyConsent == true`)
3. 담당자(assignedManagerId) 존재 여부 확인
4. 사례번호 자동 생성:
   - `registrationDate`의 연도 추출 (예: 2024)
   - 해당 연도의 최신 사례번호 조회 (예: 2024-005)
   - 다음 번호로 증가 (예: 2024-006)
   - 해당 연도 첫 케이스면 YYYY-001로 시작
5. Client 엔티티 생성 및 저장
   - `caseStatus`: "RECEPTION" (초기 상태)
   - 나머지 필드는 요청값으로 설정
6. 저장된 엔티티를 DetailResponse DTO로 변환하여 응답

**프론트엔드 연동:**
- "클라이언트 신규 등록" 폼에서 사용
- 접수번호 필드는 자동생성되므로 UI에서 입력받지 않음 (또는 읽기 전용)
- 성공 후 클라이언트 목록 페이지로 리다이렉트 또는 상세 페이지 이동

## **BE 구현 주의 사항**

1. **사례번호 동시성 제어**: 동시에 여러 요청이 들어올 경우 중복 번호 방지 필요 (트랜잭션 격리 레벨 고려)
2. **개인정보 동의 필수**: `privacyConsent`가 `false`이면 `CLIENT002` 에러 반환
3. **담당자 유효성**: `assignedManagerId`가 User 테이블에 존재하지 않으면 `CLIENT003` 에러 반환
4. **자동 생성 필드**: `caseNumber`, `caseStatus`는 백엔드에서 설정
5. **초기 상태**: 신규 클라이언트는 항상 `RECEPTION` 상태로 시작
6. **연도 기준 번호**: 매년 001부터 다시 시작 (YYYY-001, YYYY-002, ...)

---

## **Request (요청)**

### **Headers**

```
Content-Type: application/json
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

### **Request Body (JSON)**

```json
{
  "registrationDate": "YYYY-MM-DD",
  "clientName": "string",
  "gender": "string",
  "birthDate": "YYYY-MM-DD",
  "contactNumber": "string",
  "address": "string",
  "referralSource": "string",
  "initialNeedsSummary": "string",
  "assignedManagerId": "string",
  "privacyConsent": boolean
}
```

### **유효성 검사 규칙**

```json
{
  "registrationDate": "필수, 날짜 형식",
  "clientName": "필수, 공백 불가",
  "gender": "필수, '남' 또는 '여'만 가능",
  "birthDate": "필수, 날짜 형식",
  "contactNumber": "필수, 형식: 010-1234-5678 (정규식: ^\\d{2,3}-\\d{3,4}-\\d{4}$)",
  "address": "선택",
  "referralSource": "필수, 접수 경로 (예: DIRECT, REFERRAL 등)",
  "initialNeedsSummary": "선택, 접수 요청 내용",
  "assignedManagerId": "필수, 존재하는 User의 userId",
  "privacyConsent": "필수, true여야 함"
}
```

### **Request Body 예시**

```json
{
  "registrationDate": "2024-12-09",
  "clientName": "홍길동",
  "gender": "남",
  "birthDate": "1950-01-01",
  "contactNumber": "010-1234-5678",
  "address": "서울시 강남구",
  "referralSource": "DIRECT",
  "initialNeedsSummary": "경제적 어려움 호소",
  "assignedManagerId": "user123",
  "privacyConsent": true
}
```

---

## Response (응답)

### 성공 응답 (Status)

- **200 OK**: 클라이언트 생성 성공

### Response Body (JSON)

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": {
    "caseNumber": "string",
    "clientName": "string",
    "birthDate": "YYYY-MM-DD",
    "gender": "string",
    "contactNumber": "string",
    "registrationDate": "YYYY-MM-DD",
    "referralSource": "string",
    "address": "string",
    "assignedManagerId": "string",
    "assignedManagerName": "string",
    "privacyConsent": boolean,
    "initialNeedsSummary": "string",
    "caseStatus": "string"
  },
  "error": null
}
```

### Response Body 예시

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": {
    "caseNumber": "2024-006",
    "clientName": "홍길동",
    "birthDate": "1950-01-01",
    "gender": "남",
    "contactNumber": "010-1234-5678",
    "registrationDate": "2024-12-09",
    "referralSource": "DIRECT",
    "address": "서울시 강남구",
    "assignedManagerId": "user123",
    "assignedManagerName": "김복지",
    "privacyConsent": true,
    "initialNeedsSummary": "경제적 어려움 호소",
    "caseStatus": "RECEPTION"
  },
  "error": null
}
```

### 오류 응답 (Status)

- **400 Bad Request**: 유효성 검사 실패, 개인정보 동의 없음
- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 담당자 ID가 존재하지 않음

### 오류 응답 Body (JSON)

```json
{
  "success": false,
  "message": "개인정보 수집 및 이용 동의가 필요합니다.",
  "code": 400,
  "data": null,
  "error": {
    "code": "CLIENT002",
    "message": "개인정보 수집 및 이용 동의가 필요합니다."
  }
}
```

```json
{
  "success": false,
  "message": "유효하지 않은 담당자 ID입니다.",
  "code": 400,
  "data": null,
  "error": {
    "code": "CLIENT003",
    "message": "유효하지 않은 담당자 ID입니다."
  }
}
```

---

## 기타

### 비고

- **사례번호는 백엔드 자동 생성**: 프론트엔드에서 입력받지 않음
- **연도별 일련번호**: 2024-001, 2024-002, ..., 2025-001, 2025-002, ...
- **initialNeedsSummary**: IntakeRecord와 연동되지만 현재는 응답에만 포함

### 관련 문서/링크

- Entity: `Client.java`
- DTO: `ClientCreateRequest.java`, `ClientDetailResponse.java`
- Service: `ClientService.createClient()`, `ClientService.generateCaseNumber()`

---

# 3. 클라이언트 수정

```
PUT /api/v1/clients/{caseNumber}
```

## **기능 설명**

기존 클라이언트의 정보를 수정합니다. null이 아닌 필드만 업데이트됩니다 (Partial Update).

**백엔드 로직:**
1. Path Variable에서 `caseNumber` 추출
2. `caseNumber`와 `deletedAt IS NULL`로 클라이언트 조회
3. 존재하지 않으면 `CLIENT001` 에러 반환
4. Request Body의 각 필드를 확인하여 null이 아닌 경우만 업데이트:
   - `assignedManagerId`: User 존재 여부 확인 후 업데이트
   - `registrationDate`, `clientName`, `gender`, `birthDate`, `contactNumber`, `address`: 직접 업데이트
5. 변경된 엔티티 저장
6. DetailResponse DTO로 변환하여 응답

**프론트엔드 연동:**
- 클라이언트 정보 수정 폼에서 사용
- 일부 필드만 변경해도 가능 (전체 필드를 보낼 필요 없음)
- 접수번호는 수정 불가 (PK이므로 변경 불가)

## **BE 구현 주의 사항**

1. **사례번호 변경 불가**: PK이므로 변경할 수 없음
2. **Partial Update**: null인 필드는 업데이트하지 않음
3. **담당자 변경 시 유효성**: `assignedManagerId`가 null이 아니면 User 존재 확인
4. **Soft Delete 확인**: 삭제된 클라이언트는 수정 불가
5. **유효성 검사**: `gender`, `contactNumber` 패턴 검사

---

## **Request (요청)**

### **Headers**

```
Content-Type: application/json
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

### **Path Parameters**

- `caseNumber` (string, required): 수정할 클라이언트의 사례번호

### **Request Body (JSON)**

```json
{
  "registrationDate": "YYYY-MM-DD",
  "clientName": "string",
  "gender": "string",
  "birthDate": "YYYY-MM-DD",
  "contactNumber": "string",
  "address": "string",
  "referralSource": "string",
  "initialNeedsSummary": "string",
  "assignedManagerId": "string"
}
```

**모든 필드 선택 (Optional)**: 변경하고 싶은 필드만 포함

### **유효성 검사 규칙**

```json
{
  "gender": "선택, '남' 또는 '여'만 가능",
  "contactNumber": "선택, 형식: 010-1234-5678 (정규식: ^\\d{2,3}-\\d{3,4}-\\d{4}$)",
  "assignedManagerId": "선택, 존재하는 User의 userId"
}
```

### **Request Body 예시**

```json
{
  "clientName": "홍길동 수정",
  "contactNumber": "010-9999-8888",
  "address": "서울시 서초구"
}
```

---

## Response (응답)

### 성공 응답 (Status)

- **200 OK**: 클라이언트 수정 성공

### Response Body (JSON)

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": {
    "caseNumber": "string",
    "clientName": "string",
    "birthDate": "YYYY-MM-DD",
    "gender": "string",
    "contactNumber": "string",
    "registrationDate": "YYYY-MM-DD",
    "referralSource": "string",
    "address": "string",
    "assignedManagerId": "string",
    "assignedManagerName": "string",
    "privacyConsent": boolean,
    "initialNeedsSummary": "string",
    "caseStatus": "string"
  },
  "error": null
}
```

### Response Body 예시

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": {
    "caseNumber": "2024-001",
    "clientName": "홍길동 수정",
    "birthDate": "1950-01-01",
    "gender": "남",
    "contactNumber": "010-9999-8888",
    "registrationDate": "2024-11-20",
    "referralSource": "DIRECT",
    "address": "서울시 서초구",
    "assignedManagerId": "user123",
    "assignedManagerName": "김복지",
    "privacyConsent": true,
    "initialNeedsSummary": "경제적 어려움 호소",
    "caseStatus": "RECEPTION"
  },
  "error": null
}
```

### 오류 응답 (Status)

- **400 Bad Request**: 유효성 검사 실패, 담당자 ID 오류
- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 클라이언트를 찾을 수 없음

### 오류 응답 Body (JSON)

```json
{
  "success": false,
  "message": "존재하지 않는 클라이언트입니다.",
  "code": 404,
  "data": null,
  "error": {
    "code": "CLIENT001",
    "message": "존재하지 않는 클라이언트입니다."
  }
}
```

---

## 기타

### 비고

- Partial Update를 지원하므로 변경하고 싶은 필드만 보내면 됨
- `caseNumber`는 수정 불가 (PK)
- 삭제된 클라이언트는 수정 불가

### 관련 문서/링크

- Entity: `Client.java`
- DTO: `ClientUpdateRequest.java`, `ClientDetailResponse.java`
- Service: `ClientService.updateClient()`

---

# 4. 클라이언트 상세 조회

```
GET /api/v1/clients/{caseNumber}
```

## **기능 설명**

특정 클라이언트의 상세 정보를 조회합니다.

**백엔드 로직:**
1. Path Variable에서 `caseNumber` 추출
2. `caseNumber`와 `deletedAt IS NULL`로 클라이언트 조회
3. 존재하지 않으면 `CLIENT001` 에러 반환
4. 관련 정보(assignedManager) fetch
5. DetailResponse DTO로 변환하여 응답

**프론트엔드 연동:**
- 클라이언트 목록에서 "상세" 버튼 클릭 시 호출
- 상세 페이지에 모든 정보 표시 (기본 정보 + 보고서 목록)
- 현재는 보고서 목록 제외하고 기본 정보만 제공

## **BE 구현 주의 사항**

1. **Soft Delete 확인**: 삭제된 클라이언트는 조회 불가
2. **연관 엔티티 로딩**: `assignedManager`를 함께 로딩하여 N+1 문제 방지
3. **권한 확인 고려**: 현재는 인증만 확인하지만, 향후 자신의 클라이언트만 조회하도록 제한 가능

---

## **Request (요청)**

### **Headers**

```
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

### **Path Parameters**

- `caseNumber` (string, required): 조회할 클라이언트의 사례번호

### **Request Body (JSON)**

없음 (GET 요청)

---

## Response (응답)

### 성공 응답 (Status)

- **200 OK**: 클라이언트 상세 조회 성공

### Response Body (JSON)

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": {
    "caseNumber": "string",
    "clientName": "string",
    "birthDate": "YYYY-MM-DD",
    "gender": "string",
    "contactNumber": "string",
    "registrationDate": "YYYY-MM-DD",
    "referralSource": "string",
    "address": "string",
    "assignedManagerId": "string",
    "assignedManagerName": "string",
    "privacyConsent": boolean,
    "initialNeedsSummary": "string",
    "caseStatus": "string"
  },
  "error": null
}
```

### Response Body 예시

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": {
    "caseNumber": "2024-001",
    "clientName": "홍길동",
    "birthDate": "1950-01-01",
    "gender": "남",
    "contactNumber": "010-1234-5678",
    "registrationDate": "2024-11-20",
    "referralSource": "DIRECT",
    "address": "서울시 강남구",
    "assignedManagerId": "user123",
    "assignedManagerName": "김복지",
    "privacyConsent": true,
    "initialNeedsSummary": "경제적 어려움 호소",
    "caseStatus": "RECEPTION"
  },
  "error": null
}
```

### 오류 응답 (Status)

- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 클라이언트를 찾을 수 없음

### 오류 응답 Body (JSON)

```json
{
  "success": false,
  "message": "존재하지 않는 클라이언트입니다.",
  "code": 404,
  "data": null,
  "error": {
    "code": "CLIENT001",
    "message": "존재하지 않는 클라이언트입니다."
  }
}
```

---

## 기타

### 비고

- 보고서 목록은 향후 구현 예정 (현재는 기본 정보만 제공)
- 삭제된 클라이언트는 조회 불가

### 관련 문서/링크

- Entity: `Client.java`
- DTO: `ClientDetailResponse.java`
- Service: `ClientService.getClientDetail()`

---

# 5. 클라이언트 삭제

```
DELETE /api/v1/clients/{caseNumber}
```

## **기능 설명**

클라이언트를 삭제합니다 (Soft Delete). 실제 데이터는 삭제하지 않고 `deletedAt` 필드를 설정합니다.

**백엔드 로직:**
1. Path Variable에서 `caseNumber` 추출
2. `caseNumber`와 `deletedAt IS NULL`로 클라이언트 조회
3. 존재하지 않으면 `CLIENT001` 에러 반환
4. `deletedAt` 필드에 현재 시각 설정
5. 엔티티 저장
6. 성공 메시지 응답

**프론트엔드 연동:**
- 클라이언트 목록 또는 상세 페이지에서 "삭제" 버튼 클릭 시 호출
- 삭제 확인 모달 표시 권장
- 성공 후 목록 페이지로 리다이렉트

## **BE 구현 주의 사항**

1. **Soft Delete**: 물리적 삭제가 아닌 논리적 삭제 (`deletedAt` 필드 사용)
2. **연관 데이터**: 클라이언트와 연결된 IntakeRecord, AssessmentRecord 등은 그대로 유지
3. **복구 가능성**: 향후 복구 기능 구현 시 `deletedAt`을 NULL로 변경하면 복구 가능
4. **이미 삭제된 경우**: 이미 삭제된 클라이언트는 `CLIENT001` 에러 반환

---

## **Request (요청)**

### **Headers**

```
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

### **Path Parameters**

- `caseNumber` (string, required): 삭제할 클라이언트의 사례번호

### **Request Body (JSON)**

없음 (DELETE 요청)

---

## Response (응답)

### 성공 응답 (Status)

- **200 OK**: 클라이언트 삭제 성공

### Response Body (JSON)

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": "클라이언트가 삭제되었습니다.",
  "error": null
}
```

### Response Body 예시

```json
{
  "success": true,
  "message": null,
  "code": 200,
  "data": "클라이언트가 삭제되었습니다.",
  "error": null
}
```

### 오류 응답 (Status)

- **401 Unauthorized**: 인증 실패
- **404 Not Found**: 클라이언트를 찾을 수 없음

### 오류 응답 Body (JSON)

```json
{
  "success": false,
  "message": "존재하지 않는 클라이언트입니다.",
  "code": 404,
  "data": null,
  "error": {
    "code": "CLIENT001",
    "message": "존재하지 않는 클라이언트입니다."
  }
}
```

---

## 기타

### 비고

- **Soft Delete**: 실제 데이터는 삭제되지 않음, `deletedAt` 필드만 설정
- **복구 가능**: 향후 관리자 기능으로 복구 API 추가 가능
- **연관 데이터 보존**: 클라이언트 관련 모든 기록(IntakeRecord, AssessmentRecord 등)은 유지됨

### 관련 문서/링크

- Entity: `Client.java` (`deletedAt` 필드)
- Service: `ClientService.deleteClient()`

---

# 부록: 공통 사항

## 인증 (Authentication)

모든 API는 JWT Bearer Token 인증이 필요합니다.

**헤더 형식:**
```
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

**토큰 획득:**
- `POST /api/v1/auth/login`으로 로그인하여 accessToken 획득
- 토큰이 만료되면 `POST /api/v1/auth/refresh`로 갱신

## 에러 코드 목록

| 에러 코드 | HTTP Status | 메시지 | 설명 |
|---------|-------------|--------|------|
| AUTH001 | 401 | 잘못된 인증 정보입니다. | 로그인 실패 |
| AUTH004 | 401 | 유효하지 않은 토큰입니다. | JWT 토큰 오류 |
| AUTH005 | 401 | 토큰이 만료되었습니다. | JWT 토큰 만료 |
| CLIENT001 | 404 | 존재하지 않는 클라이언트입니다. | 클라이언트 조회 실패 |
| CLIENT002 | 400 | 개인정보 수집 및 이용 동의가 필요합니다. | 동의 누락 |
| CLIENT003 | 400 | 유효하지 않은 담당자 ID입니다. | 담당자 조회 실패 |
| COMMON001 | 400 | 잘못된 요청입니다. | 유효성 검사 실패 |

## API Base URL

- 개발 환경: `http://localhost:8080`
- 운영 환경: (추후 결정)

## 응답 공통 구조

모든 API는 다음과 같은 공통 응답 구조를 사용합니다:

```json
{
  "success": boolean,
  "message": string | null,
  "code": number,
  "data": object | array | string | null,
  "error": {
    "code": string,
    "message": string
  } | null
}
```

## 날짜 형식

- 날짜: `YYYY-MM-DD` (예: 2024-12-09)
- 날짜/시간: ISO 8601 형식 (예: 2024-12-09T00:12:00+09:00)
