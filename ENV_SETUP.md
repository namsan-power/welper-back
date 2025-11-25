# 환경 변수 설정 가이드

프로젝트를 실행하기 위해서는 다음 환경 변수들이 필요합니다.

## 필수 환경 변수

### 1. JWT 설정
```bash
JWT_SECRET=your-secret-key-here-minimum-256-bits
```
**권장**: 최소 32자 이상의 랜덤 문자열 사용

### 2. 데이터베이스 (Local)
```bash
DB_LOCAL_PASSWORD=your-mysql-password
```

### 3. AI 모델 URL
```bash
AI_MODEL_URL=http://your-ai-model-url:port
```

### 4. 파일 업로드 디렉토리
```bash
FILE_UPLOAD_DIR=./uploads
```

## 환경 변수 설정 방법

### Windows (IntelliJ IDEA)
1. Run > Edit Configurations
2. Environment variables 추가
3. 위 환경 변수들을 입력

### Windows (Command Line)
```cmd
set JWT_SECRET=your-secret-key-here
set DB_LOCAL_PASSWORD=your-password
set AI_MODEL_URL=http://localhost:8000
set FILE_UPLOAD_DIR=./uploads
```

### Mac/Linux
```bash
export JWT_SECRET=your-secret-key-here
export DB_LOCAL_PASSWORD=your-password
export AI_MODEL_URL=http://localhost:8000
export FILE_UPLOAD_DIR=./uploads
```

## 데이터베이스 초기 설정

MySQL 데이터베이스를 생성합니다:
```sql
CREATE DATABASE welper CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 프로덕션 환경 변수

프로덕션 환경에서는 추가로 다음 변수들이 필요합니다:
```bash
DB_URL=jdbc:mysql://your-db-host:3306/welper
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password
```
