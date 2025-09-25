\set ON_ERROR_STOP on
BEGIN;

-- =========================================================
-- Your existing seeds (amenity_type, role, permission, etc.)
-- =========================================================

DO $$
BEGIN
  RAISE NOTICE 'Seeding amenity_type...';
END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='amenity_type' AND column_name='description'
  ) THEN
    INSERT INTO amenity_type (name, description)
    SELECT v.name, v.description
    FROM (VALUES
      ('Parking','Car & motorbike parking'),
      ('Food Court','Food & beverages'),
      ('First Aid','Medical assistance')
    ) AS v(name, description)
    WHERE NOT EXISTS (SELECT 1 FROM amenity_type a WHERE a.name=v.name);
  ELSE
    INSERT INTO amenity_type (name)
    SELECT v.name
    FROM (VALUES
      ('Parking'),
      ('Food Court'),
      ('First Aid')
    ) AS v(name)
    WHERE NOT EXISTS (SELECT 1 FROM amenity_type a WHERE a.name=v.name);
  END IF;
END$$;

DO $$ BEGIN RAISE NOTICE 'Seeding role...'; END$$;

INSERT INTO role (name) SELECT 'ADMIN'    WHERE NOT EXISTS (SELECT 1 FROM role r WHERE r.name='ADMIN');
INSERT INTO role (name) SELECT 'MANAGER'  WHERE NOT EXISTS (SELECT 1 FROM role r WHERE r.name='MANAGER');
INSERT INTO role (name) SELECT 'CUSTOMER' WHERE NOT EXISTS (SELECT 1 FROM role r WHERE r.name='CUSTOMER');

DO $$ BEGIN RAISE NOTICE 'Seeding permission...'; END$$;

INSERT INTO permission (name) SELECT 'MANAGE_USERS'    WHERE NOT EXISTS (SELECT 1 FROM permission p WHERE p.name='MANAGE_USERS');
INSERT INTO permission (name) SELECT 'MANAGE_BRANCHES' WHERE NOT EXISTS (SELECT 1 FROM permission p WHERE p.name='MANAGE_BRANCHES');
INSERT INTO permission (name) SELECT 'MANAGE_TICKETS'  WHERE NOT EXISTS (SELECT 1 FROM permission p WHERE p.name='MANAGE_TICKETS');
INSERT INTO permission (name) SELECT 'VIEW_REPORTS'    WHERE NOT EXISTS (SELECT 1 FROM permission p WHERE p.name='VIEW_REPORTS');

DO $$ BEGIN RAISE NOTICE 'Mapping role -> permission...'; END$$;

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p ON p.name IN ('MANAGE_USERS','MANAGE_BRANCHES','MANAGE_TICKETS','VIEW_REPORTS')
WHERE r.name='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM role_permission rp WHERE rp.role_id=r.id AND rp.permission_id=p.id);

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p ON p.name IN ('MANAGE_BRANCHES','MANAGE_TICKETS','VIEW_REPORTS')
WHERE r.name='MANAGER'
  AND NOT EXISTS (SELECT 1 FROM role_permission rp WHERE rp.role_id=r.id AND rp.permission_id=p.id);

DO $$ BEGIN RAISE NOTICE 'Seeding park_branch...'; END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='park_branch' AND column_name='address'
  ) THEN
    INSERT INTO park_branch (name, address)
    SELECT v.name, v.address
    FROM (VALUES
      ('Central Park','1 Green Ave, District 1'),
      ('Riverside Park','99 River Rd, District 2')
    ) v(name, address)
    WHERE NOT EXISTS (SELECT 1 FROM park_branch b WHERE b.name=v.name);
  ELSE
    INSERT INTO park_branch (name)
    SELECT v.name
    FROM (VALUES
      ('Central Park'),
      ('Riverside Park')
    ) v(name)
    WHERE NOT EXISTS (SELECT 1 FROM park_branch b WHERE b.name=v.name);
  END IF;
END$$;

DO $$ BEGIN RAISE NOTICE 'Seeding ticket_type...'; END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='ticket_type' AND column_name='base_price'
  ) AND EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='ticket_type' AND column_name='description'
  ) THEN
    INSERT INTO ticket_type (name, description, base_price)
    SELECT v.name, v.description, v.price
    FROM (VALUES
      ('Day Ticket (Adult)','All-day access (Adult)', 200000),
      ('Day Ticket (Child)','All-day access (Child)', 120000),
      ('Evening Pass','17:00–21:00 access', 90000)
    ) v(name, description, price)
    WHERE NOT EXISTS (SELECT 1 FROM ticket_type t WHERE t.name=v.name);
  ELSIF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='ticket_type' AND column_name='base_price'
  ) THEN
    INSERT INTO ticket_type (name, base_price)
    SELECT v.name, v.price
    FROM (VALUES
      ('Day Ticket (Adult)',200000),
      ('Day Ticket (Child)',120000),
      ('Evening Pass',90000)
    ) v(name, price)
    WHERE NOT EXISTS (SELECT 1 FROM ticket_type t WHERE t.name=v.name);
  ELSIF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema='public' AND table_name='ticket_type' AND column_name='description'
  ) THEN
    INSERT INTO ticket_type (name, description)
    SELECT v.name, v.description
    FROM (VALUES
      ('Day Ticket (Adult)','All-day access (Adult)'),
      ('Day Ticket (Child)','All-day access (Child)'),
      ('Evening Pass','17:00–21:00 access')
    ) v(name, description)
    WHERE NOT EXISTS (SELECT 1 FROM ticket_type t WHERE t.name=v.name);
  ELSE
    INSERT INTO ticket_type (name)
    SELECT v.name
    FROM (VALUES
      ('Day Ticket (Adult)'),
      ('Day Ticket (Child)'),
      ('Evening Pass')
    ) v(name)
    WHERE NOT EXISTS (SELECT 1 FROM ticket_type t WHERE t.name=v.name);
  END IF;
END$$;

-- =========================================================
-- NEW: Daily inventories so ticket purchase works
-- =========================================================
DO $$ BEGIN RAISE NOTICE 'Seeding daily_ticket_inventory for 14 days + specific date...'; END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema='public' AND table_name='daily_ticket_inventory'
  ) THEN
    -- next 14 days for all ticket types (default capacity 100)
    INSERT INTO daily_ticket_inventory (ticket_type_id, date, total_available, sold, created_at, updated_at)
    SELECT t.id, d::date, 100, 0, NOW(), NOW()
    FROM ticket_type t
    CROSS JOIN generate_series(CURRENT_DATE, CURRENT_DATE + INTERVAL '14 day', INTERVAL '1 day') AS d
    WHERE NOT EXISTS (
      SELECT 1 FROM daily_ticket_inventory di
      WHERE di.ticket_type_id = t.id AND di.date = d::date
    );

    -- ensure explicit test date exists too (2025-09-22)
    INSERT INTO daily_ticket_inventory (ticket_type_id, date, total_available, sold, created_at, updated_at)
    SELECT t.id, DATE '2025-09-22', 100, 0, NOW(), NOW()
    FROM ticket_type t
    WHERE NOT EXISTS (
      SELECT 1 FROM daily_ticket_inventory di
      WHERE di.ticket_type_id = t.id AND di.date = DATE '2025-09-22'
    );
  END IF;
END$$;

-- =========================================================
-- NEW: Optional bulk pricing rules (if table exists)
-- =========================================================
DO $$ BEGIN RAISE NOTICE 'Seeding bulk_pricing_rule (optional)...'; END$$;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema='public' AND table_name='bulk_pricing_rule'
  ) THEN
    -- Minimal shape: (ticket_type_id, discount_percent, min_quantity)
    -- If your table lacks min_quantity, this will auto-adapt.
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema='public' AND table_name='bulk_pricing_rule' AND column_name='min_quantity'
    ) THEN
      INSERT INTO bulk_pricing_rule (ticket_type_id, discount_percent, min_quantity)
      SELECT t.id, 10, 5
      FROM ticket_type t
      WHERE NOT EXISTS (SELECT 1 FROM bulk_pricing_rule b WHERE b.ticket_type_id=t.id);
    ELSE
      INSERT INTO bulk_pricing_rule (ticket_type_id, discount_percent)
      SELECT t.id, 10
      FROM ticket_type t
      WHERE NOT EXISTS (SELECT 1 FROM bulk_pricing_rule b WHERE b.ticket_type_id=t.id);
    END IF;
  END IF;
END$$;

-- =========================================================
-- NEW: Optional branch promotion (active, valid window)
-- =========================================================
DO $$
DECLARE
  fk_col        text;
  from_col      text;
  to_col        text;
  active_col    text;
  discount_col  text;
  desc_col      text;
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema='public' AND table_name='branch_promotion'
  ) THEN
    RAISE NOTICE 'branch_promotion table not found; skipping promo seed.';
    RETURN;
  END IF;

  -- Try common FK names referencing park_branch
  SELECT column_name INTO fk_col
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='branch_promotion'
    AND column_name IN ('park_branch_id','branch_id','parkbranch_id','park_branch','parkbranch')
  LIMIT 1;

  -- Detect date, active, discount, description columns (support multiple schemas)
  SELECT column_name INTO from_col
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='branch_promotion'
    AND column_name IN ('from','valid_from','start_at','start')
  LIMIT 1;

  SELECT column_name INTO to_col
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='branch_promotion'
    AND column_name IN ('to','valid_until','end_at','end')
  LIMIT 1;

  SELECT column_name INTO active_col
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='branch_promotion'
    AND column_name IN ('is_active','active')
  LIMIT 1;

  SELECT column_name INTO discount_col
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='branch_promotion'
    AND column_name IN ('discount','discount_value','percent')
  LIMIT 1;

  SELECT column_name INTO desc_col
  FROM information_schema.columns
  WHERE table_schema='public' AND table_name='branch_promotion'
    AND column_name IN ('description','desc','name','title')
  LIMIT 1;

  RAISE NOTICE 'branch_promotion columns: fk=%, from=%, to=%, active=%, discount=%, desc=%',
    fk_col, from_col, to_col, active_col, discount_col, desc_col;

  IF fk_col IS NULL OR from_col IS NULL OR to_col IS NULL
     OR active_col IS NULL OR discount_col IS NULL OR desc_col IS NULL THEN
    RAISE NOTICE 'Required columns missing; skipping promo seed.';
    RETURN;
  END IF;

  -- Insert 1 active promo per branch if not already present (matched by description)
  EXECUTE format($f$
    INSERT INTO branch_promotion (%I, %I, %I, %I, %I, %I)
    SELECT b.id, 'Autumn Promo 10%%', 10, NOW() - INTERVAL '1 day', NOW() + INTERVAL '30 day', TRUE
    FROM park_branch b
    WHERE NOT EXISTS (
      SELECT 1 FROM branch_promotion bp WHERE bp.%I = 'Autumn Promo 10%%'
    )
  $f$, fk_col, desc_col, discount_col, from_col, to_col, active_col, desc_col);

  RAISE NOTICE 'Seeded branch_promotion promo where missing.';
END$$;

COMMIT;

\echo '== Seed complete. Quick peek =='
TABLE park_branch;
TABLE ticket_type;

-- These may not exist in all envs; ignore errors if they do not.
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name='daily_ticket_inventory') THEN
    RAISE NOTICE 'daily_ticket_inventory (next few rows):';
  END IF;
END$$;
SELECT * FROM daily_ticket_inventory ORDER BY date, ticket_type_id LIMIT 20;

