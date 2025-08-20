/**
* - 통계 테이블 생성
 */
-- 일일 공부 통계 테이블 (간소화)
CREATE TABLE daily_study_statistics (
            statistics_id            UUID                     DEFAULT gen_random_uuid()           NOT NULL
                PRIMARY KEY,
            study_date              DATE                                                          NOT NULL,
            student_id              UUID                                                          NOT NULL
                REFERENCES users(user_id)
                ON DELETE CASCADE,
            total_study_minutes     INTEGER                  DEFAULT 0                            NOT NULL,
            total_break_minutes     INTEGER                  DEFAULT 0                            NOT NULL,
            total_session_minutes   INTEGER                  DEFAULT 0                            NOT NULL,
            session_count           INTEGER                  DEFAULT 0                            NOT NULL,
            average_session_minutes INTEGER                  DEFAULT 0                            NOT NULL,
            total_pause_count       INTEGER                  DEFAULT 0                            NOT NULL,
            version                 INTEGER                  DEFAULT 1                            NOT NULL,
            created_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP           NOT NULL,
            updated_at              TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP           NOT NULL,
            created_by              UUID
                REFERENCES users(user_id),

            CONSTRAINT unique_daily_student_statistics UNIQUE(study_date, student_id),
            CONSTRAINT daily_statistics_positive_times_check
                CHECK (total_study_minutes >= 0 AND total_break_minutes >= 0 AND total_session_minutes >= 0),
            CONSTRAINT daily_statistics_session_time_check
                CHECK (total_session_minutes = total_study_minutes + total_break_minutes),
            CONSTRAINT daily_statistics_session_count_check
                CHECK (session_count >= 0)
);

COMMENT ON TABLE daily_study_statistics IS '일일 학생별 학습 통계 테이블 (오전6시 기준 집계)';
COMMENT ON COLUMN daily_study_statistics.study_date IS '통계 기준일 (당일 오전6시 ~ 다음날 오전6시)';
COMMENT ON COLUMN daily_study_statistics.student_id IS '학생 사용자 ID';
COMMENT ON COLUMN daily_study_statistics.total_study_minutes IS '총 학습시간 (분)';
COMMENT ON COLUMN daily_study_statistics.total_break_minutes IS '총 휴식시간 (분)';
COMMENT ON COLUMN daily_study_statistics.total_session_minutes IS '총 세션 유지시간 (분) = 학습시간 + 휴식시간';
COMMENT ON COLUMN daily_study_statistics.session_count IS '총 세션 수';
COMMENT ON COLUMN daily_study_statistics.average_session_minutes IS '평균 세션 시간 (분)';
COMMENT ON COLUMN daily_study_statistics.total_pause_count IS '총 일시정지 횟수';

-- 인덱스 생성
CREATE INDEX idx_daily_statistics_date_student ON daily_study_statistics(study_date, student_id);
CREATE INDEX idx_daily_statistics_date ON daily_study_statistics(study_date);
CREATE INDEX idx_daily_statistics_student ON daily_study_statistics(student_id);
CREATE INDEX idx_daily_statistics_created_at ON daily_study_statistics(created_at);