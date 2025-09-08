# Schema and seed data for exception2 campaigns
# This file creates the campaigns table and inserts sample rows
# aligned with src/http/02.charge.http scenarios.

-- Ensure we're operating on the demo database
USE `demo`;

-- Create campaigns table if it doesn't exist
CREATE TABLE IF NOT EXISTS `campaigns` (
  `id`          VARCHAR(64)  NOT NULL,
  `remain`      BIGINT       NOT NULL,
  `daily_quota` BIGINT       NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed data
-- camp-001: success path; enough remain and quota
INSERT INTO `campaigns` (`id`, `remain`, `daily_quota`) VALUES
  ('camp-001', 100000, 50000)
ON DUPLICATE KEY UPDATE `remain` = VALUES(`remain`), `daily_quota` = VALUES(`daily_quota`);

-- camp-low: very low remain to trigger NotEnoughBudget for large amounts
INSERT INTO `campaigns` (`id`, `remain`, `daily_quota`) VALUES
  ('camp-low', 50, 50000)
ON DUPLICATE KEY UPDATE `remain` = VALUES(`remain`), `daily_quota` = VALUES(`daily_quota`);

-- camp-quota: adequate remain but small daily_quota to trigger QuotaExceeded when amount > 1000
INSERT INTO `campaigns` (`id`, `remain`, `daily_quota`) VALUES
  ('camp-quota', 200000, 1000)
ON DUPLICATE KEY UPDATE `remain` = VALUES(`remain`), `daily_quota` = VALUES(`daily_quota`);
