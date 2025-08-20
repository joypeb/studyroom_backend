/**
* - 통계 테이블 자료 생성
* - 목표 시간 추가 기능
*을 위한 스터디 세션 테이블 수정 쿼리 2025.08.18
 */
-- 기존 study_sessions 테이블에 필요한 컬럼 추가
ALTER TABLE study_sessions
    ADD COLUMN IF NOT EXISTS target_study_minutes INTEGER,
    ADD COLUMN IF NOT EXISTS extension_requested BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS extension_granted_until TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS extension_minutes INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS pre_end_notification_sent BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS auto_terminated BOOLEAN DEFAULT FALSE;

-- end_reason enum 값 확장
ALTER TABLE study_sessions
    DROP CONSTRAINT IF EXISTS study_sessions_end_reason_check;

ALTER TABLE study_sessions
    ADD CONSTRAINT study_sessions_end_reason_check
        CHECK ((end_reason)::text = ANY
    ((ARRAY ['COMPLETED'::character varying, 'MANUAL_STOP'::character varying, 'TIMEOUT'::character varying, 'SYSTEM_ERROR'::character varying, 'FORCED_STOP'::character varying, 'TARGET_REACHED'::character varying, 'DAILY_RESET'::character varying, 'EXTENDED_TARGET_REACHED'::character varying])::text[]));

-- 컬럼 주석 추가
COMMENT ON COLUMN study_sessions.target_study_minutes IS '목표 학습시간 (분)';
COMMENT ON COLUMN study_sessions.extension_requested IS '연장 요청 여부';
COMMENT ON COLUMN study_sessions.extension_granted_until IS '연장 승인된 종료 시간';
COMMENT ON COLUMN study_sessions.extension_minutes IS '연장된 시간 (분)';
COMMENT ON COLUMN study_sessions.pre_end_notification_sent IS '10분 전 알림 발송 여부';
COMMENT ON COLUMN study_sessions.auto_terminated IS '자동 종료 여부';