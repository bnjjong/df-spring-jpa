-- 1. audience DDL 생성
-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS audience_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE audience_db;

-- audience 테이블 생성 (200만명 사용자 데이터용)
CREATE TABLE IF NOT EXISTS audience (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 고유 ID',

    -- 사용자가 요청한 7개 프로퍼티
                                        gender VARCHAR(10) NOT NULL COMMENT '성별 (남자/여자)',
    address VARCHAR(50) NOT NULL COMMENT '주소 (시/도)',
    age INT NOT NULL COMMENT '나이',
    monthly_sales DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '월 매출액',
    is_hometax_salary BOOLEAN NOT NULL DEFAULT FALSE COMMENT '홈텍스 연봉 여부',
    membership_level VARCHAR(20) NOT NULL COMMENT '멤버십 등급 (BRONZE/SILVER/GOLD/PLATINUM/DIAMOND)',
    industry_category VARCHAR(50) NOT NULL COMMENT '업종 카테고리',

    -- 시스템 필드
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='오디언스 사용자 테이블';

-- 세그먼트 조건 쿼리 성능을 위한 인덱스 생성
-- (성별 = '남자' and 나이 >= 20 and 나이 <= 30) or (주소 in('서울','경기','인천') and 월매출액 >=10000000)
CREATE INDEX idx_gender_age ON audience(gender, age) COMMENT '성별+나이 조건용 인덱스';
CREATE INDEX idx_address ON audience(address) COMMENT '주소 조건용 인덱스';
CREATE INDEX idx_monthly_sales ON audience(monthly_sales) COMMENT '월매출액 조건용 인덱스';
CREATE INDEX idx_segment_condition ON audience(gender, age, address, monthly_sales) COMMENT '복합 세그먼트 조건용 인덱스';

-- 테이블 구조 확인
DESCRIBE audience;
