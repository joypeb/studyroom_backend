-- updated_at 자동 업데이트 함수
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- 트리거 생성
CREATE TRIGGER update_daily_statistics_updated_at
    BEFORE UPDATE ON daily_study_statistics
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_extension_logs_updated_at
    BEFORE UPDATE ON session_extension_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();