-- 일일 통계 요약 뷰
CREATE VIEW v_daily_study_summary AS
SELECT
    dss.*,
    u.name as student_name,
    u.email as student_email,
    CASE
        WHEN dss.total_session_minutes > 0
            THEN ROUND((dss.total_study_minutes::DECIMAL / dss.total_session_minutes * 100), 2)
        ELSE 0
        END as study_efficiency_percentage
FROM daily_study_statistics dss
         JOIN users u ON dss.student_id = u.user_id
WHERE u.role = 'STUDENT' AND u.account_status = 'ACTIVE';

COMMENT ON VIEW v_daily_study_summary IS '일일 학습 통계 요약 뷰 (학생 정보 및 효율성 지표 포함)';