-- =========================
-- 1) Drop game_review table
-- =========================
DROP TABLE IF EXISTS game_review CASCADE;

-- ==========================================
-- 2) GAME: remove location, add image_url + status
-- ==========================================
ALTER TABLE game
  DROP COLUMN IF EXISTS location,
  ADD COLUMN IF NOT EXISTS image_url TEXT,
  ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;

-- ===================================================================
-- 3) BRANCH_AMENITY: switch product -> game, add image_url + status
-- ===================================================================
-- If branch_amenity currently has product_id FK, drop it and add game_id
ALTER TABLE branch_amenity
  DROP CONSTRAINT IF EXISTS fk_branch_amenity_product,
  DROP COLUMN IF EXISTS product_id,
  ADD COLUMN IF NOT EXISTS game_id BIGINT,
  ADD COLUMN IF NOT EXISTS image_url TEXT,
  ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;

-- Add the FK to game (if not already)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_branch_amenity_game'
  ) THEN
    ALTER TABLE branch_amenity
      ADD CONSTRAINT fk_branch_amenity_game
      FOREIGN KEY (game_id) REFERENCES game(id);
  END IF;
END$$;

-- Ensure park_branch_id exists (if your schema didn’t already enforce this)
ALTER TABLE branch_amenity
  ADD COLUMN IF NOT EXISTS park_branch_id BIGINT;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_branch_amenity_branch'
  ) THEN
    ALTER TABLE branch_amenity
      ADD CONSTRAINT fk_branch_amenity_branch
      FOREIGN KEY (park_branch_id) REFERENCES park_branch(id);
  END IF;
END$$;

-- ======================================================================
-- 4) Remove bulk pricing + daily inventory (drop tables & FKs if present)
-- ======================================================================
DROP TABLE IF EXISTS bulk_pricing_rule CASCADE;
DROP TABLE IF EXISTS daily_ticket_inventory CASCADE;

-- =========================================================
-- 5) TICKET_TYPE: relate to park_branch, drop time fields
-- =========================================================
ALTER TABLE ticket_type
  ADD COLUMN IF NOT EXISTS park_branch_id BIGINT,
  ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE,
  DROP COLUMN IF EXISTS start_time,
  DROP COLUMN IF EXISTS end_time,
  DROP COLUMN IF EXISTS game_id;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_ticket_type_branch'
  ) THEN
    ALTER TABLE ticket_type
      ADD CONSTRAINT fk_ticket_type_branch
      FOREIGN KEY (park_branch_id) REFERENCES park_branch(id);
  END IF;
END$$;

-- ===========================================================
-- 6) PARK_BRANCH: open/close to time, add image + status
-- ===========================================================
-- If you had open/close as timestamp, change to time
ALTER TABLE park_branch
  ADD COLUMN IF NOT EXISTS open_time TIME,
  ADD COLUMN IF NOT EXISTS close_time TIME,
  ADD COLUMN IF NOT EXISTS image_url TEXT,
  ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;

-- Optional: copy values from old datetime cols if they exist (open, close)
-- UPDATE park_branch SET open_time = CAST(open AS TIME), close_time = CAST(close AS TIME);
-- Then drop old cols:
ALTER TABLE park_branch
  DROP COLUMN IF EXISTS open,
  DROP COLUMN IF EXISTS close;

-- =========================================================
-- 7) BRANCH_EVENT: new table
-- =========================================================
CREATE TABLE IF NOT EXISTS branch_event (
  id BIGSERIAL PRIMARY KEY,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  created_by VARCHAR(255),
  updated_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_by VARCHAR(255),
  park_branch_id BIGINT NOT NULL REFERENCES park_branch(id),
  title TEXT NOT NULL,
  content TEXT,
  image_url TEXT,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  status BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================
-- 8) Rename BRANCH_PROMOTION -> BRANCH_VOUCHER and fields
-- =========================================================
-- Table rename
ALTER TABLE IF EXISTS branch_promotion RENAME TO branch_voucher;

-- Column rename (if you used is_active; normalize to status)
ALTER TABLE branch_voucher
  RENAME COLUMN IF EXISTS is_active TO status;

-- Keep existing columns like valid_from, valid_until, discount_type, discount_value
-- Add image_url if you need visuals for vouchers:
ALTER TABLE branch_voucher
  ADD COLUMN IF NOT EXISTS image_url TEXT;

-- =========================================================
-- 9) Add status fields where needed
-- =========================================================
ALTER TABLE branch_staff       ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE user_entity        ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE staff_assignment   ADD COLUMN IF NOT EXISTS status BOOLEAN NOT NULL DEFAULT TRUE;

-- =========================================================
-- 10) STAFF_ASSIGNMENT: scan in/out timestamps
-- =========================================================
ALTER TABLE staff_assignment
  ADD COLUMN IF NOT EXISTS scan_in_at  TIMESTAMP,
  ADD COLUMN IF NOT EXISTS scan_out_at TIMESTAMP;

-- =========================================================
-- 11) SHIFT: add days_of_week text & ensure defaults 10-22 exist
-- =========================================================
ALTER TABLE shift
  ADD COLUMN IF NOT EXISTS days_of_week VARCHAR(50); -- e.g., "MON,TUE,WED"
-- If start_time and end_time don’t exist, add them with defaults:
ALTER TABLE shift
  ADD COLUMN IF NOT EXISTS start_time TIME NOT NULL DEFAULT TIME '10:00',
  ADD COLUMN IF NOT EXISTS end_time   TIME NOT NULL DEFAULT TIME '22:00';

-- ==================================================================
-- 12) IMAGE URL columns on requested 3 tables (if not covered above)
-- ==================================================================
ALTER TABLE game           ADD COLUMN IF NOT EXISTS image_url TEXT;
ALTER TABLE branch_amenity ADD COLUMN IF NOT EXISTS image_url TEXT;
ALTER TABLE park_branch    ADD COLUMN IF NOT EXISTS image_url TEXT;

-- ==================================================================
-- 13) Clean up product if you no longer need it (optional)
-- ==================================================================
-- DROP TABLE IF EXISTS product CASCADE;
