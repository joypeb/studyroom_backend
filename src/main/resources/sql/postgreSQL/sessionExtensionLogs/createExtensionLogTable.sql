/*
* 세션 연장 요청 로그 테이블
 */
-- 세션 연장 요청 로그 테이블
CREATE TABLE session_extension_logs (
        extension_log_id        UUID                     DEFAULT gen_random_uuid()           NOT NULL
            PRIMARY KEY,
        session_id              UUID                                                          NOT NULL
            REFERENCES study_sessions(session_id)
            ON DELETE CASCADE,
        student_id              UUID                                                          NOT NULL
            REFERENCES users(user_id)
            ON DELETE CASCADE,
        requested_at            TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP           NOT NULL,
        extension_minutes       INTEGER                                                       NOT NULL,
        request_status          VARCHAR(20)              DEFAULT 'PENDING'                    NOT NULL
            CONSTRAINT extension_logs_status_check
            CHECK (request_status IN ('PENDING', 'APPROVED', 'REJECTED', 'EXPIRED')),
        responded_at            TIMESTAMP WITH TIME ZONE,
        original_target_minutes INTEGER                                                       NOT NULL,
        extended_target_minutes INTEGER,
        version                 INTEGER                  DEFAULT 1                            NOT NULL,
        created_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP           NOT NULL,
        updated_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP           NOT NULL,
        created_by              UUID
            REFERENCES users(user_id),

        CONSTRAINT extension_logs_positive_minutes_check
            CHECK (extension_minutes > 0),
        CONSTRAINT extension_logs_positive_target_check
            CHECK (original_target_minutes > 0),
        CONSTRAINT extension_logs_extended_target_check
            CHECK (extended_target_minutes IS NULL OR extended_target_minutes > original_target_minutes)
);

COMMENT ON TABLE session_extension_logs IS '세션 연장 요청 로그 테이블';
COMMENT ON COLUMN session_extension_logs.session_id IS '연장 요청된 세션 ID';
COMMENT ON COLUMN session_extension_logs.student_id IS '연장 요청한 학생 ID';
COMMENT ON COLUMN session_extension_logs.extension_minutes IS '요청한 연장 시간 (분)';
COMMENT ON COLUMN session_extension_logs.request_status IS '요청 상태 (PENDING: 대기, APPROVED: 승인, REJECTED: 거부, EXPIRED: 만료)';
COMMENT ON COLUMN session_extension_logs.original_target_minutes IS '원래 목표시간 (분)';
COMMENT ON COLUMN session_extension_logs.extended_target_minutes IS '연장된 목표시간 (분)';

-- 인덱스 생성
CREATE INDEX idx_extension_logs_session ON session_extension_logs(session_id);
CREATE INDEX idx_extension_logs_student ON session_extension_logs(student_id);
CREATE INDEX idx_extension_logs_requested_at ON session_extension_logs(requested_at);
CREATE INDEX idx_extension_logs_status ON session_extension_logs(request_status);


