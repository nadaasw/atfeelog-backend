-- ============================================================
-- follows 테이블: following_id → followee_id 컬럼명 변경
-- 앱 실행 전에 먼저 실행하세요 (ddl-auto: update 충돌 방지)
-- ============================================================

-- 1. 기존 유니크 제약조건 이름 조회 후 삭제
SET @constraint_name = (
    SELECT constraint_name
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'follows'
      AND CONSTRAINT_TYPE = 'UNIQUE'
    LIMIT 1
);

SET @drop_sql = CONCAT('ALTER TABLE follows DROP INDEX `', @constraint_name, '`');
PREPARE stmt FROM @drop_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2. 컬럼명 변경 (MySQL 8.0+)
ALTER TABLE follows RENAME COLUMN following_id TO followee_id;

-- 3. 새 유니크 제약조건 추가
ALTER TABLE follows ADD CONSTRAINT uk_follows_follower_followee UNIQUE (follower_id, followee_id);
