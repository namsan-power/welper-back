# Auth API 명세서

## 목차
- [1. 사용자 등록](#1-사용자-등록)
- [2. 로그인](#2-로그인)
- [3. 토큰 갱신](#3-토큰-갱신)
- [4. 현재 사용자 정보 조회](#4-현재-사용자-정보-조회)
- [5. 비밀번호 변경](#5-비밀번호-변경)
- [6. 로그아웃](#6-로그아웃)

---

# 1. 사용자 등록

**Endpoint**: `POST /api/v1/auth/register`

## **기능 설명**

새로운 사용자를 시스템에 등록하는 API입니다. 

**백엔드 처리 로직:**
1. 요청받은 `userId`의 중복 여부를 확인합니다
2. 비밀번호를 BCrypt로 암호화하여 저장합니다
3. 사용자 정보를 `users` 테이블에 저장합니다
4. 생성된 사용자 정보를 반환합니다

**프론트엔드 연동:**
- 회원가입 페이지에서 사용자 정보를 입력받아 이 API를 호출합니다
- 등록 성공 시 로그인 페이지로 이동하거나 자동 로그인을 진행합니다

**사용자 시나리오:**
1. 사용자가 회원가입 페이지에서 정보를 입력합니다
2. "가입하기" 버튼을 클릭합니다
3. 시스템이 사용자를 등록하고 성공 메시지를 표시합니다

## **BE 구현 주의 사항**

- **중복 체크**: `userId`가 이미 존재하는 경우 `DUPLICATE_USER_ID` 에러를 반환해야 합니다
- **비밀번호 정책**: 
  - 최소 8자 이상
  - 영문, 숫자, 특수문자(@$!%*#?&) 포함 필수
- **Role 검증**: `role`은 반드시 `SUPERVISOR` 또는 `CASE_MANAGER`만 허용됩니다
- **비밀번호 암호화**: BCryptPasswordEncoder를 사용하여 암호화 후 저장합니다
- **기본 상태**: 생성 시 `status`는 자동으로 `ACTIVE`로 설정됩니다

---

## **Request (요청)**

### **Headers**

```json
Content-Type: application/json
```

### **Request Body (JSON)**

```json
{
  "userId": "string",
  "password": "string",
  "name": "string",
  "role": "string",
  "agencyName": "string"
}
```

### **유효성 검사 규칙**

```json
{
  "userId": {
    "required": true,
    "minLength": 4,
    "maxLength": 20,
    "pattern": "^[a-zA-Z0-9_-]+$",
    "description": "영문, 숫자, -, _만 사용 가능"
  },
  "password": {
    "required": true,
    "minLength": 8,
    "pattern": "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
    "description": "영문, 숫자, 특수문자 포함 필수"
  },
  "name": {
    "required": true,
    "description": "사용자 이름"
  },
  "role": {
    "required": true,
    "enum": ["SUPERVISOR", "CASE_MANAGER"],
    "description": "사용자 역할"
  },
  "agencyName": {
    "required": false,
    "description": "소속 기관명"
  }
}
```

### **Request Body 예시**

```json
{
  "userId": "testuser",
  "password": "Test1234!@",
  "name": "홍길동",
  "role": "CASE_MANAGER",
  "agencyName": "서울시 복지센터"
}
```

---

## **Response (응답)**

### **성공 응답 (Status: 200 OK)**

사용자 등록이 성공적으로 완료되었습니다.

### **Response Body (JSON)**

```json
{
  "success": true,
  "data": {
    "userId": "string",
    "name": "string",
    "role": "string",
    "agencyName": "string",
    "status": "string"
  },
  "error": null
}
```

### **Response Body 예시**

```json
{
  "success": true,
  "data": {
    "userId": "testuser",
    "name": "홍길동",
    "role": "CASE_MANAGER",
    "agencyName": "서울시 복지센터",
    "status": "ACTIVE"
  },
  "error": null
}
```

### **오류 응답**

#### **400 Bad Request - 유효성 검사 실패**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "잘못된 요청입니다.",
    "details": "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
  }
}
```

#### **409 Conflict - 중복된 사용자 ID**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "DUPLICATE_USER_ID",
    "message": "이미 사용 중인 사용자 ID입니다.",
    "details": null
  }
}
```

---

## **기타**

### **비고**

- 비밀번호는 절대 평문으로 저장되지 않습니다 (BCrypt 암호화)
- 등록 후 자동 로그인을 원하는 경우 프론트엔드에서 등록 성공 후 로그인 API를 추가로 호출해야 합니다
- Admin 계정은 이 API로 생성할 수 없습니다 (별도 관리)

### **관련 문서/링크**

- [로그인 API](#2-로그인)
- 데이터베이스 스키마: `users` 테이블

---

# 2. 로그인

**Endpoint**: `POST /api/v1/auth/login`

## **기능 설명**

사용자(User) 또는 관리자(Admin) 인증을 처리하고 JWT 토큰을 발급하는 API입니다.

**백엔드 처리 로직:**
1. `userId`로 User 테이블에서 사용자를 조회합니다
2. User가 없으면 Admin 테이블에서 관리자를 조회합니다
3. 입력받은 비밀번호와 저장된 해시 비밀번호를 비교합니다
4. User의 경우 `INACTIVE` 상태이면 로그인을 거부합니다
5. JWT Access Token(1시간 유효)과 Refresh Token(7일 유효)을 생성합니다
6. Refresh Token을 데이터베이스에 저장합니다
7. 토큰과 사용자 정보를 반환합니다

**프론트엔드 연동:**
- 로그인 페이지에서 사용자 ID와 비밀번호를 입력받아 이 API를 호출합니다
- 받은 `accessToken`을 로컬 스토리지 또는 쿠키에 저장합니다
- `refreshToken`은 안전한 저장소(HttpOnly 쿠키 권장)에 저장합니다
- 이후 모든 API 요청 시 `Authorization: Bearer {accessToken}` 헤더에 포함합니다

**사용자 시나리오:**
1. 사용자가 로그인 페이지에서 ID와 비밀번호를 입력합니다
2. "로그인" 버튼을 클릭합니다
3. 시스템이 인증을 처리하고 토큰을 발급합니다
4. 프론트엔드가 토큰을 저장하고 메인 페이지로 이동합니다

## **BE 구현 주의 사항**

- **통합 로그인**: User와 Admin 계정 모두 동일한 엔드포인트로 로그인 가능합니다
- **비밀번호 비교**: BCrypt의 `matches()` 메서드를 사용하여 안전하게 비교합니다
- **상태 확인**: User의 경우 `status`가 `INACTIVE`이면 `FORBIDDEN` 에러를 반환합니다
- **토큰 생성**:
  - Access Token: 사용자 ID와 Role을 포함하며 1시간 유효
  - Refresh Token: 사용자 ID만 포함하며 7일 유효
- **Refresh Token 관리**: 
  - 기존 Refresh Token이 있으면 삭제하고 새로 생성합니다
  - 데이터베이스에 저장하여 검증 시 사용합니다
- **보안**: 로그인 실패 시 "사용자를 찾을 수 없음"과 "비밀번호 틀림"을 구분하지 않고 동일한 에러 메시지를 반환합니다

---

## **Request (요청)**

### **Headers**

```json
Content-Type: application/json
```

### **Request Body (JSON)**

```json
{
  "userId": "string",
  "password": "string"
}
```

### **유효성 검사 규칙**

```json
{
  "userId": {
    "required": true,
    "description": "사용자 ID 또는 관리자 ID"
  },
  "password": {
    "required": true,
    "description": "비밀번호"
  }
}
```

### **Request Body 예시**

```json
{
  "userId": "testuser",
  "password": "Test1234!@"
}
```

---

## **Response (응답)**

### **성공 응답 (Status: 200 OK)**

로그인이 성공적으로 완료되었습니다.

### **Response Body (JSON)**

```json
{
  "success": true,
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "user": {
      "userId": "string",
      "name": "string",
      "role": "string",
      "agencyName": "string",
      "status": "string"
    }
  },
  "error": null
}
```

### **Response Body 예시**

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJDQVNFX01BTkFHRVIiLCJ0b2tlblR5cGUiOiJBQ0NFU1MiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0.xxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInRva2VuVHlwZSI6IlJFRlJFU0giLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDYwNDgwMH0.yyy",
    "user": {
      "userId": "testuser",
      "name": "홍길동",
      "role": "CASE_MANAGER",
      "agencyName": "서울시 복지센터",
      "status": "ACTIVE"
    }
  },
  "error": null
}
```

### **오류 응답**

#### **401 Unauthorized - 인증 실패**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "잘못된 인증 정보입니다.",
    "details": null
  }
}
```

#### **404 Not Found - 사용자 없음**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "존재하지 않는 사용자입니다.",
    "details": null
  }
}
```

#### **403 Forbidden - 비활성 계정**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "FORBIDDEN",
    "message": "해당 요청에 대한 권한이 없습니다.",
    "details": null
  }
}
```

---

## **기타**

### **비고**

- Access Token 유효기간: **1시간**
- Refresh Token 유효기간: **7일**
- JWT Secret은 최소 256bits(32자) 이상이어야 합니다
- 토큰에는 사용자 ID, Role, 토큰 타입이 포함됩니다
- Admin 로그인도 동일한 엔드포인트를 사용합니다

### **관련 문서/링크**

- [토큰 갱신 API](#3-토큰-갱신)
- [로그아웃 API](#6-로그아웃)

---

# 3. 토큰 갱신

**Endpoint**: `POST /api/v1/auth/refresh`

## **기능 설명**

만료된 Access Token을 Refresh Token을 사용하여 갱신하는 API입니다.

**백엔드 처리 로직:**
1. Refresh Token의 유효성을 검증합니다 (서명, 만료 시간)
2. Token Type이 "REFRESH"인지 확인합니다
3. 데이터베이스에서 해당 Refresh Token을 조회합니다
4. 토큰이 만료되었는지 확인합니다
5. 사용자 정보를 조회합니다
6. 새로운 Access Token과 Refresh Token을 생성합니다
7. 기존 Refresh Token을 삭제하고 새 Refresh Token을 저장합니다
8. 새로운 토큰들을 반환합니다

**프론트엔드 연동:**
- Access Token이 만료되었을 때 (401 Unauthorized 응답) 자동으로 이 API를 호출합니다
- 새로운 토큰을 받아 저장하고 원래 요청을 다시 시도합니다
- Refresh Token도 만료된 경우 로그인 페이지로 리다이렉트합니다

**사용자 시나리오:**
1. 사용자가 API를 호출할 때 Access Token이 만료되어 401 에러가 발생합니다
2. 프론트엔드가 자동으로 Refresh Token으로 토큰 갱신 API를 호출합니다
3. 새로운 토큰을 받아 저장하고 원래 요청을 재시도합니다
4. 사용자는 로그인 상태가 유지되는 것을 경험합니다

## **BE 구현 주의 사항**

- **토큰 타입 검증**: 반드시 Refresh Token만 허용해야 합니다 (Access Token으로 갱신 불가)
- **데이터베이스 검증**: JWT 검증뿐만 아니라 DB에 저장된 Refresh Token과 일치하는지 확인해야 합니다
- **만료 처리**: 만료된 Refresh Token은 DB에서 삭제합니다
- **토큰 로테이션**: 보안을 위해 Refresh Token도 함께 갱신합니다 (Refresh Token Rotation)
- **원자성**: 기존 토큰 삭제와 새 토큰 저장은 트랜잭션으로 처리되어야 합니다

---

## **Request (요청)**

### **Headers**

```json
Content-Type: application/json
```

### **Request Body (JSON)**

```json
{
  "refreshToken": "string"
}
```

### **유효성 검사 규칙**

```json
{
  "refreshToken": {
    "required": true,
    "description": "로그인 시 발급받은 Refresh Token"
  }
}
```

### **Request Body 예시**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInRva2VuVHlwZSI6IlJFRlJFU0giLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDYwNDgwMH0.yyy"
}
```

---

## **Response (응답)**

### **성공 응답 (Status: 200 OK)**

토큰 갱신이 성공적으로 완료되었습니다.

### **Response Body (JSON)**

```json
{
  "success": true,
  "data": {
    "accessToken": "string",
    "refreshToken": "string",
    "user": {
      "userId": "string",
      "name": "string",
      "role": "string",
      "agencyName": "string",
      "status": "string"
    }
  },
  "error": null
}
```

### **Response Body 예시**

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.newAccessToken.xxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.newRefreshToken.yyy",
    "user": {
      "userId": "testuser",
      "name": "홍길동",
      "role": "CASE_MANAGER",
      "agencyName": "서울시 복지센터",
      "status": "ACTIVE"
    }
  },
  "error": null
}
```

### **오류 응답**

#### **401 Unauthorized - 유효하지 않은 토큰**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_TOKEN",
    "message": "유효하지 않은 토큰입니다.",
    "details": null
  }
}
```

#### **401 Unauthorized - 만료된 토큰**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "TOKEN_EXPIRED",
    "message": "토큰이 만료되었습니다.",
    "details": null
  }
}
```

---

## **기타**

### **비고**

- Refresh Token Rotation을 적용하여 보안을 강화했습니다
- 갱신 시 Access Token과 Refresh Token 모두 새로 발급됩니다
- 기존 Refresh Token은 무효화됩니다
- Refresh Token도 7일이 지나면 만료되며, 이 경우 재로그인이 필요합니다

### **관련 문서/링크**

- [로그인 API](#2-로그인)
- JWT Refresh Token Rotation 패턴

---

# 4. 현재 사용자 정보 조회

**Endpoint**: `GET /api/v1/auth/me`

## **기능 설명**

현재 로그인한 사용자의 정보를 조회하는 API입니다.

**백엔드 처리 로직:**
1. Authorization 헤더에서 JWT Access Token을 추출합니다
2. 토큰의 유효성을 검증합니다
3. 토큰에서 사용자 ID를 추출합니다
4. 데이터베이스에서 사용자 정보를 조회합니다
5. 사용자 정보를 반환합니다

**프론트엔드 연동:**
- 페이지 로드 시 현재 로그인한 사용자 정보를 가져올 때 사용합니다
- 프로필 페이지에서 사용자 정보를 표시할 때 사용합니다
- 권한별로 다른 UI를 보여줘야 할 때 Role 정보를 확인하는 용도로 사용합니다

**사용자 시나리오:**
1. 사용자가 프로필 페이지에 접속합니다
2. 프론트엔드가 이 API를 호출하여 사용자 정보를 가져옵니다
3. 화면에 사용자 이름, 역할, 소속 기관 등의 정보가 표시됩니다

## **BE 구현 주의 사항**

- **인증 필수**: 반드시 유효한 Access Token이 필요합니다
- **보안**: 비밀번호 해시는 절대 반환하지 않습니다
- **SecurityContext 활용**: Spring Security의 `SecurityContextHolder`에서 인증 정보를 가져옵니다
- **캐싱 고려**: 자주 호출되는 API이므로 필요시 Redis 등을 활용한 캐싱을 고려할 수 있습니다

---

## **Request (요청)**

### **Headers**

```json
Content-Type: application/json
Authorization: Bearer {accessToken}
```

### **Request Body**

이 API는 Request Body가 필요하지 않습니다.

---

## **Response (응답)**

### **성공 응답 (Status: 200 OK)**

현재 사용자 정보 조회에 성공했습니다.

### **Response Body (JSON)**

```json
{
  "success": true,
  "data": {
    "userId": "string",
    "name": "string",
    "role": "string",
    "agencyName": "string",
    "status": "string"
  },
  "error": null
}
```

### **Response Body 예시**

```json
{
  "success": true,
  "data": {
    "userId": "testuser",
    "name": "홍길동",
    "role": "CASE_MANAGER",
    "agencyName": "서울시 복지센터",
    "status": "ACTIVE"
  },
  "error": null
}
```

### **오류 응답**

#### **401 Unauthorized - 토큰 없음 또는 유효하지 않음**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_TOKEN",
    "message": "유효하지 않은 토큰입니다.",
    "details": null
  }
}
```

#### **404 Not Found - 사용자 없음**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "존재하지 않는 사용자입니다.",
    "details": null
  }
}
```

---

## **기타**

### **비고**

- 이 API는 인증이 필요한 모든 페이지에서 사용자 정보를 확인할 때 사용됩니다
- Role 정보를 통해 프론트엔드에서 권한별 UI를 분기 처리할 수 있습니다
- 토큰이 만료된 경우 먼저 토큰 갱신 API를 호출한 후 재시도해야 합니다

### **관련 문서/링크**

- [로그인 API](#2-로그인)
- [토큰 갱신 API](#3-토큰-갱신)

---

# 5. 비밀번호 변경

**Endpoint**: `POST /api/v1/auth/change-password`

## **기능 설명**

현재 로그인한 사용자의 비밀번호를 변경하는 API입니다.

**백엔드 처리 로직:**
1. Authorization 헤더에서 JWT Access Token을 추출하여 사용자를 식별합니다
2. 현재 비밀번호가 올바른지 확인합니다
3. 새 비밀번호가 현재 비밀번호와 동일한지 확인합니다 (동일하면 거부)
4. 새 비밀번호를 BCrypt로 암호화합니다
5. 데이터베이스에 새 비밀번호를 저장합니다
6. 보안을 위해 해당 사용자의 모든 Refresh Token을 삭제합니다 (재로그인 필요)

**프론트엔드 연동:**
- 설정 페이지나 프로필 페이지에서 "비밀번호 변경" 기능을 제공할 때 사용합니다
- 비밀번호 변경 성공 시 사용자를 로그아웃시키고 로그인 페이지로 리다이렉트합니다
- 성공 메시지를 표시하고 재로그인을 안내합니다

**사용자 시나리오:**
1. 사용자가 설정 페이지에서 "비밀번호 변경"을 선택합니다
2. 현재 비밀번호와 새 비밀번호를 입력합니다
3. "변경하기" 버튼을 클릭합니다
4. 비밀번호가 변경되고 자동으로 로그아웃됩니다
5. 새 비밀번호로 재로그인합니다

## **BE 구현 주의 사항**

- **현재 비밀번호 확인**: 반드시 현재 비밀번호를 확인하여 본인 인증을 해야 합니다
- **동일 비밀번호 체크**: 새 비밀번호가 현재 비밀번호와 같으면 거부합니다
- **비밀번호 정책**: 새 비밀번호도 등록 시와 동일한 정책을 적용합니다 (8자 이상, 영문+숫자+특수문자)
- **Refresh Token 무효화**: 보안을 위해 비밀번호 변경 시 모든 Refresh Token을 삭제하여 다른 기기에서도 재로그인을 강제합니다
- **트랜잭션**: 비밀번호 변경과 Refresh Token 삭제는 하나의 트랜잭션으로 처리되어야 합니다

---

## **Request (요청)**

### **Headers**

```json
Content-Type: application/json
Authorization: Bearer {accessToken}
```

### **Request Body (JSON)**

```json
{
  "currentPassword": "string",
  "newPassword": "string"
}
```

### **유효성 검사 규칙**

```json
{
  "currentPassword": {
    "required": true,
    "description": "현재 비밀번호"
  },
  "newPassword": {
    "required": true,
    "minLength": 8,
    "pattern": "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
    "description": "새 비밀번호 (영문, 숫자, 특수문자 포함)"
  }
}
```

### **Request Body 예시**

```json
{
  "currentPassword": "Test1234!@",
  "newPassword": "NewPass1234!@"
}
```

---

## **Response (응답)**

### **성공 응답 (Status: 200 OK)**

비밀번호 변경이 성공적으로 완료되었습니다.

### **Response Body (JSON)**

```json
{
  "success": true,
  "data": "비밀번호가 변경되었습니다.",
  "error": null
}
```

### **Response Body 예시**

```json
{
  "success": true,
  "data": "비밀번호가 변경되었습니다.",
  "error": null
}
```

### **오류 응답**

#### **401 Unauthorized - 현재 비밀번호 불일치**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "잘못된 인증 정보입니다.",
    "details": null
  }
}
```

#### **400 Bad Request - 새 비밀번호가 현재 비밀번호와 동일**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "잘못된 요청입니다.",
    "details": null
  }
}
```

#### **400 Bad Request - 비밀번호 정책 위반**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "잘못된 요청입니다.",
    "details": "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
  }
}
```

---

## **기타**

### **비고**

- 비밀번호 변경 후 모든 Refresh Token이 삭제되므로 **재로그인이 필요**합니다
- 다른 기기에서 로그인한 세션도 모두 종료됩니다
- 프론트엔드는 비밀번호 변경 성공 후 로그인 페이지로 리다이렉트해야 합니다

### **관련 문서/링크**

- [로그인 API](#2-로그인)
- 보안 Best Practice: 비밀번호 변경 시 세션 무효화

---

# 6. 로그아웃

**Endpoint**: `POST /api/v1/auth/logout`

## **기능 설명**

현재 로그인한 사용자를 로그아웃 처리하는 API입니다.

**백엔드 처리 로직:**
1. Authorization 헤더에서 JWT Access Token을 추출하여 사용자를 식별합니다
2. 해당 사용자의 모든 Refresh Token을 데이터베이스에서 삭제합니다
3. 로그아웃 성공 메시지를 반환합니다

**프론트엔드 연동:**
- 사용자가 로그아웃 버튼을 클릭할 때 이 API를 호출합니다
- 로그아웃 성공 후 로컬 스토리지나 쿠키에서 Access Token과 Refresh Token을 삭제합니다
- 로그인 페이지로 리다이렉트합니다
- Redux나 Context API의 인증 상태를 초기화합니다

**사용자 시나리오:**
1. 사용자가 화면 상단의 "로그아웃" 버튼을 클릭합니다
2. 확인 다이얼로그가 표시됩니다 (선택사항)
3. 확인하면 로그아웃 API가 호출됩니다
4. 토큰이 삭제되고 로그인 페이지로 이동합니다

## **BE 구현 주의 사항**

- **Refresh Token 삭제**: 데이터베이스에서 해당 사용자의 Refresh Token을 삭제하여 토큰 갱신을 불가능하게 합니다
- **Access Token 처리**: JWT는 Stateless하므로 Access Token 자체를 무효화할 수는 없습니다. 프론트엔드에서 삭제하는 것으로 처리합니다
- **보안 강화**: 필요시 Redis 등을 활용하여 로그아웃된 Access Token을 블랙리스트에 추가할 수 있습니다
- **에러 처리**: 토큰이 이미 만료되었거나 유효하지 않아도 로그아웃은 성공으로 처리합니다

---

## **Request (요청)**

### **Headers**

```json
Content-Type: application/json
Authorization: Bearer {accessToken}
```

### **Request Body**

이 API는 Request Body가 필요하지 않습니다.

---

## **Response (응답)**

### **성공 응답 (Status: 200 OK)**

로그아웃이 성공적으로 완료되었습니다.

### **Response Body (JSON)**

```json
{
  "success": true,
  "data": "로그아웃되었습니다.",
  "error": null
}
```

### **Response Body 예시**

```json
{
  "success": true,
  "data": "로그아웃되었습니다.",
  "error": null
}
```

### **오류 응답**

#### **401 Unauthorized - 토큰 없음 또는 유효하지 않음**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "INVALID_TOKEN",
    "message": "유효하지 않은 토큰입니다.",
    "details": null
  }
}
```

---

## **기타**

### **비고**

- 로그아웃 후 해당 사용자의 모든 Refresh Token이 삭제됩니다
- Access Token은 만료 시간까지 기술적으로는 유효하지만, 프론트엔드에서 삭제하므로 사용할 수 없습니다
- 보안을 강화하려면 Redis를 활용한 토큰 블랙리스트를 구현할 수 있습니다

### **관련 문서/링크**

- [로그인 API](#2-로그인)
- JWT Stateless 인증 방식

---

## 부록

### JWT Token 구조

**Access Token Payload:**
```json
{
  "sub": "userId",
  "role": "CASE_MANAGER",
  "tokenType": "ACCESS",
  "iat": 1700000000,
  "exp": 1700003600
}
```

**Refresh Token Payload:**
```json
{
  "sub": "userId",
  "tokenType": "REFRESH",
  "iat": 1700000000,
  "exp": 1700604800
}
```

### 공통 에러 코드

| 에러 코드 | HTTP 상태 | 설명 |
|----------|-----------|------|
| `INVALID_CREDENTIALS` | 401 | 잘못된 인증 정보 |
| `USER_NOT_FOUND` | 404 | 존재하지 않는 사용자 |
| `DUPLICATE_USER_ID` | 409 | 중복된 사용자 ID |
| `INVALID_TOKEN` | 401 | 유효하지 않은 토큰 |
| `TOKEN_EXPIRED` | 401 | 만료된 토큰 |
| `FORBIDDEN` | 403 | 권한 없음 |
| `INVALID_REQUEST` | 400 | 잘못된 요청 |

### 보안 고려사항

1. **HTTPS 필수**: 모든 API는 반드시 HTTPS를 통해 호출되어야 합니다
2. **토큰 저장**: 
   - Access Token: 메모리 또는 로컬 스토리지
   - Refresh Token: HttpOnly 쿠키 (권장)
3. **CORS 설정**: 허용된 도메인만 API 접근 가능
4. **Rate Limiting**: 로그인 API는 Rate Limiting 적용 권장
5. **비밀번호 정책**: 최소 8자 이상, 영문+숫자+특수문자 조합

### 프론트엔드 가이드

#### 토큰 관리 예시 (React)

```javascript
// 로그인
const login = async (userId, password) => {
  const response = await fetch('/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, password })
  });
  
  const data = await response.json();
  
  if (data.success) {
    localStorage.setItem('accessToken', data.data.accessToken);
    localStorage.setItem('refreshToken', data.data.refreshToken);
    // 사용자 정보 저장
    setUser(data.data.user);
  }
};

// API 요청 시 토큰 포함
const fetchWithAuth = async (url) => {
  const token = localStorage.getItem('accessToken');
  
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  // 401 에러 시 토큰 갱신
  if (response.status === 401) {
    await refreshToken();
    // 원래 요청 재시도
    return fetchWithAuth(url);
  }
  
  return response;
};

// 토큰 갱신
const refreshToken = async () => {
  const refreshToken = localStorage.getItem('refreshToken');
  
  const response = await fetch('/api/v1/auth/refresh', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  });
  
  const data = await response.json();
  
  if (data.success) {
    localStorage.setItem('accessToken', data.data.accessToken);
    localStorage.setItem('refreshToken', data.data.refreshToken);
  } else {
    // Refresh Token도 만료됨 - 로그인 페이지로
    logout();
  }
};

// 로그아웃
const logout = async () => {
  const token = localStorage.getItem('accessToken');
  
  await fetch('/api/v1/auth/logout', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  // 로그인 페이지로 리다이렉트
  window.location.href = '/login';
};
```

---

**작성일**: 2025-11-26  
**작성자**: Backend Team  
**버전**: 1.0.0

