-- 2. audience 샘플 DML 생성
USE demo;

-- 200만명 샘플 데이터 생성을 위한 프로시저
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS InsertAudienceData()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE total_users INT DEFAULT 2000000; -- 200만명
    DECLARE batch_size INT DEFAULT 1000; -- 배치 크기
    DECLARE current_batch INT DEFAULT 0;

    -- 기존 데이터 삭제 (필요시 주석 해제)
    -- TRUNCATE TABLE audience;

    -- 자동 커밋 비활성화 (성능 최적화)
    SET autocommit = 0;

    WHILE i <= total_users DO
        SET current_batch = current_batch + 1;

INSERT INTO audience (
    gender,
    address,
    age,
    monthly_sales,
    is_hometax_salary,
    membership_level,
    industry_category

) VALUES (
             -- 성별: 50:50 비율
             CASE WHEN RAND() < 0.5 THEN '남자' ELSE '여자' END,

             -- 주소: 17개 시/도 (서울/경기/인천 비중 높게)
             CASE
                 WHEN RAND() < 0.25 THEN '서울'
                 WHEN RAND() < 0.45 THEN '경기'
                 WHEN RAND() < 0.55 THEN '인천'
                 ELSE ELT(FLOOR(1 + RAND() * 14), '부산', '대구', '광주', '대전', '울산',
                          '강원', '충북', '충남', '전북', '전남', '경북', '경남', '제주', '세종')
                 END,

             -- 나이: 18-79세 (20-30세 비중 높게)
             CASE
                 WHEN RAND() < 0.2 THEN FLOOR(20 + RAND() * 11)  -- 20-30세 20%
                 ELSE FLOOR(18 + RAND() * 62)  -- 18-79세 80%
                 END,

             -- 월 매출액: 0원 ~ 5천만원 (1천만원 이상 15% 비율)
             CASE
                 WHEN RAND() < 0.15 THEN ROUND(10000000 + RAND() * 40000000, 2)  -- 1천만원 이상 15%
                 ELSE ROUND(RAND() * 10000000, 2)  -- 1천만원 미만 85%
                 END,

             -- 홈택스 연봉 여부: 30% 확률
             RAND() < 0.3,

             -- 멤버십 등급: 균등 분포
             ELT(FLOOR(1 + RAND() * 5), 'BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'DIAMOND'),

             -- 업종 카테고리: 10개 업종
             ELT(FLOOR(1 + RAND() * 10), 'IT', '금융', '제조업', '서비스업', '유통업',
                 '건설업', '교육', '의료', '농업', '기타')
         );

SET i = i + 1;

        -- 1000건마다 커밋 (배치 처리)
        IF current_batch = batch_size THEN
            COMMIT;
            SET current_batch = 0;
END IF;

        -- 10만건마다 진행상황 출력
        IF i % 100000 = 0 THEN
SELECT CONCAT('Inserted ', i, ' / ', total_users, ' users (',
              ROUND(i/total_users * 100, 1), '%)') as progress;
END IF;
END WHILE;

    -- 마지막 배치 커밋
COMMIT;
SET autocommit = 1;

SELECT CONCAT('✅ Successfully inserted ', total_users, ' audience data') as result;
END$$

DELIMITER ;

-- 프로시저 실행 (시간이 오래 걸릴 수 있음 - 약 10-20분)
-- CALL InsertAudienceData();

-- 데이터 검증 쿼리들
-- 1) 전체 데이터 건수 확인
SELECT '전체 사용자 수' as category, COUNT(*) as count FROM audience;

-- 2) 세그먼트 조건별 사용자 수 확인
SELECT
    '남자 20-30세' as segment,
    COUNT(*) as count,
    ROUND(COUNT(*) / (SELECT COUNT(*) FROM audience) * 100, 2) as percentage
FROM audience
WHERE gender = '남자' AND age >= 20 AND age <= 30

UNION ALL

SELECT
    '서울/경기/인천 고액매출(1천만원 이상)' as segment,
    COUNT(*) as count,
    ROUND(COUNT(*) / (SELECT COUNT(*) FROM audience) * 100, 2) as percentage
FROM audience
WHERE address IN ('서울', '경기', '인천') AND monthly_sales >= 10000000

UNION ALL

SELECT
    '전체 세그먼트 조건 (OR)' as segment,
    COUNT(*) as count,
    ROUND(COUNT(*) / (SELECT COUNT(*) FROM audience) * 100, 2) as percentage
FROM audience
WHERE (gender = '남자' AND age >= 20 AND age <= 30)
   OR (address IN ('서울', '경기', '인천') AND monthly_sales >= 10000000);


CALL InsertAudienceData();