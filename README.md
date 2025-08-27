# Study Room Backend API

독서실 관리 시스템 백엔드 API 서버입니다. Spring WebFlux를 기반으로 한 리액티브 웹 애플리케이션입니다.

## 🛠 기술 스택

- **Framework**: Spring Boot 3.5.3, Spring WebFlux
- **Language**: Java 21
- **Database**: PostgreSQL with R2DBC
- **Authentication**: JWT + Spring Security
- **Build Tool**: Gradle
- **Testing**: JUnit 5, Mockito, Reactor Test

## 📋 주요 기능

- 사용자 관리 (회원가입, 로그인, 프로필 관리)
- 좌석 관리 (좌석 생성, 할당, 조회)
- 스터디 세션 관리 (세션 생성, 상태 변경, 히스토리 조회)
- 실시간 모니터링 (Server-Sent Events)
- 카카오 소셜 로그인

## 🚀 API 엔드포인트

### 🔐 인증 (Authentication)

#### 카카오 로그인

```http
POST /auth/kakao/login
```

**요청**:

```json
{
  "code": "authorization_code_from_kakao"
}
```

**응답**:

```json
{
  "token": "jwt_token_here",
  "user": {
    "name": "사용자명",
    "email": "user@example.com"
  }
}
```

#### 프로필 조회

```http
GET /auth/kakao/profile
Authorization: Bearer {token}
```

**응답**:

```json
{
  "name": "사용자명",
  "email": "user@example.com"
}
```

---

### 👥 사용자 관리 (Users)

#### 전체 사용자 목록 조회

```http
GET /users
```

**요청**:

```json
{
  "page": 0,
  "size": 20
}
```

**응답**:

```json
{
  "content": [
    {
      "userId": "uuid",
      "email": "user@example.com",
      "name": "사용자명",
      "phoneNumber": "010-1234-5678",
      "role": "STUDENT",
      "accountStatus": "ACTIVE"
    }
  ],
  "totalPages": 5,
  "totalElements": 100
}
```

#### 특정 사용자 조회

```http
GET /users/{userId}
```

**응답**:

```json
{
  "userId": "uuid",
  "email": "user@example.com",
  "name": "사용자명",
  "phoneNumber": "010-1234-5678",
  "role": "STUDENT",
  "accountStatus": "ACTIVE"
}
```

#### 역할별 사용자 목록 조회

```http
GET /users/role
```

**요청**:

```json
{
  "role": "STUDENT",
  "page": 0,
  "size": 20
}
```

#### 사용자 상태 수정

```http
PUT /users/{userId}/status
```

**요청**:

```json
{
  "accountStatus": "INACTIVE"
}
```

#### 사용자 정보 수정

```http
PUT /users/{userId}
```

**요청**:

```json
{
  "name": "새 이름",
  "phoneNumber": "010-9999-8888"
}
```

#### 내 정보 조회

```http
GET /users/me
Authorization: Bearer {token}
```

---

### 💺 좌석 관리 (Seats)

#### 전체 좌석 목록 조회

```http
GET /seats
```

**요청**:

```json
{
  "page": 0,
  "size": 20
}
```

**응답**:

```json
{
  "content": [
    {
      "seatId": "uuid",
      "seatNumber": "A01",
      "isAvailable": true,
      "assignedStudentId": null
    }
  ],
  "totalPages": 3,
  "totalElements": 50
}
```

#### 특정 좌석 조회

```http
GET /seats/{seatId}
```

**응답**:

```json
{
  "seatId": "uuid",
  "seatNumber": "A01",
  "isAvailable": false,
  "assignedStudentId": "student_uuid",
  "assignedStudentName": "학생명",
  "assignedAt": "2023-08-27T10:00:00Z"
}
```

#### 좌석 생성

```http
POST /seats
```

**요청**:

```json
{
  "seatNumber": "A01",
  "isAvailable": true
}
```

#### 좌석 할당

```http
PUT /seats/{seatId}/assignment
```

**요청**:

```json
{
  "studentId": "student_uuid"
}
```

#### 좌석 삭제

```http
DELETE /seats/{seatId}
```

---

### 📚 스터디 세션 관리 (Study Sessions)

#### 전체 학습 세션 목록 조회

```http
GET /study/sessions?sort=createdAt,desc
```

**응답**:

```json
[
  {
    "studentName": "학생명",
    "seatNumber": "A01"
  }
]
```

#### 특정 세션 상세 조회

```http
GET /study/sessions/{sessionId}
```

**응답**:

```json
{
  "studentName": "학생명",
  "seatNumber": "A01"
}
```

#### 새 학습 세션 생성

```http
POST /study/sessions/new
Authorization: Bearer {token}
```

**요청**:

```json
{
  "duration": "TWO_HOURS"
}
```

**응답**:

```json
{
  "sessionId": "uuid"
}
```

**Duration 옵션**:

- `ONE_HOUR`: 1시간
- `TWO_HOURS`: 2시간
- `THREE_HOURS`: 3시간
- `FOUR_HOURS`: 4시간

#### 세션 상태 변경

```http
POST /study/sessions/change-status
Authorization: Bearer {token}
```

**요청**:

```json
{
  "sessionId": "uuid",
  "newStatus": "PAUSED"
}
```

**Status 옵션**:

- `READY`: 준비
- `ACTIVE`: 활성
- `PAUSED`: 일시정지
- `COMPLETED`: 완료
- `INTERRUPTED`: 중단

#### 세션 히스토리 조회 (관리자)

```http
GET /study/sessions/session-history/{studentId}
```

**응답**:

```json
[
  {
    "sessionId": "uuid",
    "studentId": "uuid",
    "seatId": "uuid",
    "startTime": "2023-08-27T10:00:00Z",
    "endTime": "2023-08-27T12:00:00Z",
    "plannedEndTime": "2023-08-27T12:00:00Z",
    "sessionStatus": "COMPLETED",
    "totalStudyMinutes": 120,
    "totalBreakMinutes": 10,
    "pauseCount": 1,
    "endReason": "NORMAL"
  }
]
```

#### 내 세션 히스토리 조회

```http
GET /study/sessions/session-history
Authorization: Bearer {token}
```

#### 현재 세션 조회 (관리자)

```http
GET /study/sessions/current/{studentId}
```

#### 내 현재 세션 조회

```http
GET /study/sessions/current
Authorization: Bearer {token}
```

---

### 📊 실시간 모니터링 (Admin Monitoring)

#### 실시간 세션 목록 스트리밍 (요약)

```http
GET /admin/monitoring/sessions/list/realtime
Accept: text/event-stream
```

**Server-Sent Event 응답**:

```
event: sessions-list-update
data: [{"studentName": "학생1", "seatNumber": "A01", "status": "ACTIVE"}]
```

#### 당일 활성 세션 목록 (요약)

```http
GET /admin/monitoring/sessions/list
```

**응답**:

```json
{
  "success": true,
  "message": "당일 활성 세션 목록 5개 조회 성공",
  "data": [
    {
      "studentName": "학생명",
      "seatNumber": "A01",
      "status": "ACTIVE",
      "startTime": "2023-08-27T10:00:00Z"
    }
  ]
}
```

#### 실시간 세션 상세 스트리밍

```http
GET /admin/monitoring/sessions/details/realtime
Accept: text/event-stream
```

#### 당일 활성 세션 상세

```http
GET /admin/monitoring/sessions/details
```

**응답**:

```json
{
  "success": true,
  "message": "당일 활성 세션 상세 5개 조회 성공",
  "data": [
    {
      "sessionId": "uuid",
      "studentId": "uuid",
      "studentName": "학생명",
      "seatNumber": "A01",
      "startTime": "2023-08-27T10:00:00Z",
      "currentStatus": "ACTIVE",
      "totalStudyMinutes": 45,
      "totalBreakMinutes": 5,
      "pauseCount": 1,
      "plannedEndTime": "2023-08-27T12:00:00Z"
    }
  ]
}
```

#### 활성 세션 개수 조회

```http
GET /admin/monitoring/sessions/count
```

**응답**:

```json
{
  "success": true,
  "message": "활성 세션 개수 조회 성공",
  "data": 15
}
```

---

## 🔧 개발 환경 설정

### 사전 요구사항

- Java 21+
- PostgreSQL 13+
- Gradle 8.0+

### 환경 변수

```yaml
# application.yaml
spring:
  r2dbc:
    url:
    username:
    password:

jwt:
  secret:
  expiration:

kakao:
  client-id:
  client-secret:
  redirect-uri: 
```

### 실행 방법

```bash
# 프로젝트 클론
git clone [repository-url]

# 의존성 설치 및 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 도메인 테스트 실행
./gradlew test --tests "com.jvc.studyroom.domain.studySession.*"
```

---

## 🛡 보안

- JWT 기반 인증
- Spring Security를 통한 엔드포인트 보호
- CORS 설정
- 입력값 검증 및 예외 처리

---

## 📚 API 응답 형태

### 성공 응답

```json
{
  "success": true,
  "message": "요청이 성공했습니다",
  "data": {
    ...
  }
}
```

### 오류 응답

```json
{
  "success": false,
  "message": "오류가 발생했습니다",
  "errorCode": "ERROR_CODE",
  "timestamp": "2023-08-27T10:00:00Z"
}
```

---

## 📞 문의

프로젝트 관련 문의사항이 있으시면 이슈를 등록해 주세요.

---

*이 API 문서는 현재 개발된 엔드포인트를 기반으로 작성되었습니다. 지속적으로 업데이트됩니다.*
2025.08.27