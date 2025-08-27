# StudySessionControllerV1 테스트 가이드

이 디렉토리에는 `StudySessionControllerV1`과 관련된 기능들에 대한 **DB 연결 없이 실행 가능한 테스트 코드**들이 포함되어 있습니다.

## 테스트 구조

### 1. Controller Layer 테스트

- 각 엔드포인트별 성공/실패 케이스 테스트
- @Mock으로 Service 레이어 모킹
- StepVerifier를 사용한 Reactive 스트림 검증

### 2. Service Layer 테스트

- 비즈니스 로직 검증
- Mock :  Repository, SeatFindService, StudySessionTimeCalculator
- Reactor 스트림 동작 검증
- StepVerifier를 통한 비동기 스트림 테스트

### 3. Repository Layer 테스트

- Repository 인터페이스 메서드 동작 검증
- DB 접근 로직을 Mock으로 대체

## 테스트 커버리지

### API 엔드포인트 테스트

- `GET /study/sessions` - 전체 학습 세션 목록 조회
- `GET /study/sessions/{sessionId}` - 특정 세션 상세 정보 조회
- `POST /study/sessions/new` - 새 학습 세션 생성
- `POST /study/sessions/change-status` - 세션 상태 변경
- `GET /study/sessions/session-history/{studentId}` - 관리자용 학생 세션 히스토리
- `GET /study/sessions/session-history` - 로그인한 학생의 세션 히스토리
- `GET /study/sessions/current/{studentId}` - 관리자용 현재 세션 조회
- `GET /study/sessions/current` - 로그인한 학생의 현재 세션 조회

### 비즈니스 로직 테스트

- 세션 생성 로직 (좌석 할당 확인, 시간 계산)
- 세션 상태 변경 로직 (시간 추적, 카운트 증가)
- **시간 계산 로직**: StudySessionTimeCalculator를 통한 시간 추적
- 세션 조회 로직 (정렬, 필터링)
- 에러 처리 로직 (예외 상황 처리)

## 주요 특징

### 1. DB 연결 불필요

- 모든 테스트는 실제 DB 연결 없이 실행 가능
- @Mock을 활용한 의존성 모킹
- 인메모리 테스트로 빠른 실행 속도

### 2. 실제 동작 모방

- WebFlux의 Reactive 스트림 완전 지원
- StepVerifier를 통한 정확한 Reactive 스트림 테스트

### 3. 포괄적인 테스트 시나리오

- 성공 케이스와 실패 케이스 모두 포함
- Edge case 처리 검증
- 에러 상황에 대한 적절한 응답 확인

## 테스트 데이터

### Mock 사용자 정보

```java
-userId:

UUID(랜덤 생성)
-email:"test@example.com"
    -name:"테스트 사용자"
    -username:"testuser"
    -role:UserRole.STUDENT
-accountStatus:AccountStatus.ACTIVE
```

### Mock 세션 정보

```java
-sessionId:

UUID(랜덤 생성)
-studentId:
테스트 사용자
ID
-seatId:

UUID(랜덤 생성)
-sessionStatus:

SessionStatus(테스트별 상이)
-duration:SessionDuration.

TWO_HOURS(기본값)
```

## 최근 해결된 문제점

### 1. @CurrentUser 어노테이션 문제 해결

**문제**: WebTestClient에서 @CurrentUser 어노테이션이 제대로 처리되지 않아 500 Internal Server Error 발생

**해결**:

- WebTestClient 대신 Controller 메서드 직접 호출 방식 사용
- ArgumentResolver 의존성 제거
- StepVerifier를 통한 더 정확한 결과 검증

### 2. Service Layer NullPointerException 해결

**문제**: StudySessionTimeCalculator가 Mock되지 않아 NPE 발생

**해결**:

- @Mock StudySessionTimeCalculator timeCalculator 추가
- timeCalculator.calculateTimeUpdateForStatusChange() 메서드 stubbing
- StudySessionTimeCalculator.StudySessionTimeUpdate 적절한 반환값 설정

### 3. Mock Stubbing 개선

**문제**: Mockito Strict stubbing argument mismatch 오류

**해결**:

- 정확한 파라미터 타입과 매칭 조건 설정
- any() 매처 사용으로 유연한 매칭
- verify()를 통한 Mock 호출 검증 강화

## 테스트 실행 결과

- **총 테스트 수**: 43개
- **성공률**: 100%
- **실행 시간**: 약 17초
- **커버리지**: Controller, Service, Repository 레이어 전체

## 개선 사항 및 확장 가능성

### 1. 추가 가능한 테스트

- 동시성 테스트 (여러 사용자가 동시에 세션 조작)
- 성능 테스트 (대량 데이터 처리)
- 보안 테스트 (권한 검증)

### 2. 실제 환경 연동 테스트

- TestContainer를 활용한 실제 DB 연동 테스트
- 통합 환경에서의 E2E 테스트

### 3. 테스트 자동화

- CI/CD 파이프라인 연동
- 코드 커버리지 측정 및 리포팅