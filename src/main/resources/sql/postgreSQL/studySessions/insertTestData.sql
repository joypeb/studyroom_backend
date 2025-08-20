INSERT INTO study_sessions
(student_id, seat_id, start_time, end_time, planned_end_time,
 session_status, total_study_minutes, total_break_minutes, pause_count,
 end_reason, ended_by, created_by)
VALUES
-- 1 ACTIVE 진행 중. 4f1151db-0a86-478b-8f96-35f72b788901
('4f1151db-0a86-478b-8f96-35f72b788901', '2b0bfecb-3386-4538-b3fa-6ad6ac12477d',
 NOW() - INTERVAL '1 hour', NULL, NOW() + INTERVAL '2 hours',
 'ACTIVE', 0, 5, 1, NULL, NULL, '4f1151db-0a86-478b-8f96-35f72b788901'),

-- 2 COMPLETED 정상 종료
('e4b8a266-4234-4907-b0d3-d21af2c3a39a', 'b55af7c2-aa22-4bc4-877a-a460df665879',
 NOW() - INTERVAL '4 hours', NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour',
 'COMPLETED', 180, 20, 2, 'COMPLETED', 'e4b8a266-4234-4907-b0d3-d21af2c3a39a',
 'e4b8a266-4234-4907-b0d3-d21af2c3a39a'),

-- 3 READY 대기
('e9191ae0-db56-46b4-ad39-3e2636459c4a', '15ed8d3c-b9af-4377-85f5-a1353f3c249a',
 NOW() + INTERVAL '30 minutes', NULL, NOW() + INTERVAL '4 hours',
 'READY', 0, 0, 0, NULL, NULL, 'e9191ae0-db56-46b4-ad39-3e2636459c4a'),

-- 4 PAUSED 일시중지
('5fbc1290-fee5-4d0e-8118-29ae7a3b975d', '9fa9b079-17c9-487a-9cc5-dd8878df5e77',
 NOW() - INTERVAL '2 hours', NULL, NOW() + INTERVAL '1 hour',
 'PAUSED', 0, 10, 1, NULL, NULL, '5fbc1290-fee5-4d0e-8118-29ae7a3b975d'),

-- 5 CANCELLED MANUAL_STOP
('93a14201-c840-4931-899a-d05fec508452', 'a9f910aa-48d2-4ce1-8a7b-4d1a242d40fb',
 NOW() - INTERVAL '3 hours', NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour',
 'CANCELLED', 0, 15, 1, 'MANUAL_STOP', '93a14201-c840-4931-899a-d05fec508452',
 '93a14201-c840-4931-899a-d05fec508452'),

-- 6 INTERRUPTED SYSTEM_ERROR
('fd5eed56-cc90-4d56-8b9a-02b9863cf56d', '88eedb10-2871-4660-ad32-c220831820db',
 NOW() - INTERVAL '5 hours', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hour',
 'INTERRUPTED', 0, 20, 2, 'SYSTEM_ERROR', 'fd5eed56-cc90-4d56-8b9a-02b9863cf56d',
 'fd5eed56-cc90-4d56-8b9a-02b9863cf56d'),

-- 7 ACTIVE 진행 중
('9ed28a9c-7bc0-450f-aab1-6f59d37c0daf', 'a6a0970a-313a-4223-af25-60e29bdf030f',
 NOW() - INTERVAL '40 minutes', NULL, NOW() + INTERVAL '3 hours',
 'ACTIVE', 0, 3, 0, NULL, NULL, '9ed28a9c-7bc0-450f-aab1-6f59d37c0daf'),

-- 8 COMPLETED TIMEOUT
('2e54771f-ff34-4540-9abd-d5b256227f1f', '4ec1a0e4-a1d3-4b80-ad33-47c38afe4a2f',
 NOW() - INTERVAL '6 hours', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hour',
 'COMPLETED', 240, 30, 3, 'TIMEOUT', '2e54771f-ff34-4540-9abd-d5b256227f1f',
 '2e54771f-ff34-4540-9abd-d5b256227f1f'),

-- 9 READY 대기
('2eaf3347-89d6-418c-be51-3013ffb58a81', 'a58f1d0d-d7bc-44c4-b2ad-188ca6a2483f',
 NOW() + INTERVAL '1 hour', NULL, NOW() + INTERVAL '5 hours',
 'READY', 0, 0, 0, NULL, NULL, '2eaf3347-89d6-418c-be51-3013ffb58a81');

commit;

