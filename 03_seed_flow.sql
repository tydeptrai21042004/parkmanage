\set ON_ERROR_STOP on
BEGIN;

-- =============== BRANCH AMENITIES (requires: name, amenity_type_id, park_branch_id) ===============
DO $$
DECLARE
  v_branch_central BIGINT;
  v_branch_river   BIGINT;
  v_at_parking     BIGINT;
  v_at_food        BIGINT;
  v_at_firstaid    BIGINT;
BEGIN
  SELECT id INTO v_branch_central FROM park_branch WHERE name='Central Park';
  SELECT id INTO v_branch_river   FROM park_branch WHERE name='Riverside Park';

  SELECT id INTO v_at_parking  FROM amenity_type WHERE name='Parking';
  SELECT id INTO v_at_food     FROM amenity_type WHERE name='Food Court';
  SELECT id INTO v_at_firstaid FROM amenity_type WHERE name='First Aid';

  IF v_branch_central IS NOT NULL AND v_at_parking IS NOT NULL THEN
    INSERT INTO branch_amenity (name, amenity_type_id, park_branch_id, description)
    SELECT 'Central Park - Parking', v_at_parking, v_branch_central, 'On-site vehicle parking'
    WHERE NOT EXISTS (
      SELECT 1 FROM branch_amenity WHERE name='Central Park - Parking'
    );
  END IF;

  IF v_branch_central IS NOT NULL AND v_at_food IS NOT NULL THEN
    INSERT INTO branch_amenity (name, amenity_type_id, park_branch_id, description)
    SELECT 'Central Park - Food Court', v_at_food, v_branch_central, 'Food & beverages'
    WHERE NOT EXISTS (
      SELECT 1 FROM branch_amenity WHERE name='Central Park - Food Court'
    );
  END IF;

  IF v_branch_central IS NOT NULL AND v_at_firstaid IS NOT NULL THEN
    INSERT INTO branch_amenity (name, amenity_type_id, park_branch_id, description)
    SELECT 'Central Park - First Aid', v_at_firstaid, v_branch_central, 'Medical assistance'
    WHERE NOT EXISTS (
      SELECT 1 FROM branch_amenity WHERE name='Central Park - First Aid'
    );
  END IF;

  IF v_branch_river IS NOT NULL AND v_at_parking IS NOT NULL THEN
    INSERT INTO branch_amenity (name, amenity_type_id, park_branch_id, description)
    SELECT 'Riverside Park - Parking', v_at_parking, v_branch_river, 'On-site vehicle parking'
    WHERE NOT EXISTS (
      SELECT 1 FROM branch_amenity WHERE name='Riverside Park - Parking'
    );
  END IF;

  IF v_branch_river IS NOT NULL AND v_at_food IS NOT NULL THEN
    INSERT INTO branch_amenity (name, amenity_type_id, park_branch_id, description)
    SELECT 'Riverside Park - Food Court', v_at_food, v_branch_river, 'Food & beverages'
    WHERE NOT EXISTS (
      SELECT 1 FROM branch_amenity WHERE name='Riverside Park - Food Court'
    );
  END IF;
END$$;

-- =============== USERS (requires: email, password, username) ===============
-- bcrypt hash below is a placeholder; replace if your app validates login.
-- It corresponds to password "password".
INSERT INTO user_entity (email, password, username)
SELECT 'admin@park.local', '{bcrypt}$2a$10$CwTqweycUXWue0Thq9StjUM0uJ8s6b6s1JbL5G1F7x2f3vJfPj5eZ3y2', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM user_entity WHERE email='admin@park.local');

INSERT INTO user_entity (email, password, username, park_branch_id)
SELECT 'alice@example.com', '{bcrypt}$2a$10$CwTycUXWue0Thq9StjUM0uJ8s6b6333333333s1JbL5G1F7x2f3vJfPj5eZ3y2', 'alice',
       (SELECT id FROM park_branch WHERE name='Central Park')
WHERE NOT EXISTS (SELECT 1 FROM user_entity WHERE email='alice@example.com');

-- =============== ROLE ASSIGNMENTS (user_role needs: user_id, role_id) ===============
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user_entity u, role r
WHERE u.email='admin@park.local' AND r.name='ADMIN'
  AND NOT EXISTS (SELECT 1 FROM user_role ur WHERE ur.user_id=u.id AND ur.role_id=r.id);

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user_entity u, role r
WHERE u.email='alice@example.com' AND r.name='CUSTOMER'
  AND NOT EXISTS (SELECT 1 FROM user_role ur WHERE ur.user_id=u.id AND ur.role_id=r.id);

-- =============== WALLETS (wallet needs: user_id, balance (nullable) ) ===============
INSERT INTO wallet (user_id, balance)
SELECT u.id, 0
FROM user_entity u
WHERE u.email IN ('admin@park.local','alice@example.com')
  AND NOT EXISTS (SELECT 1 FROM wallet w WHERE w.user_id=u.id);

-- =============== TOP-UP for Alice (wallet_topup needs: amount, order_code, status, wallet_id) ===============
DO $$
DECLARE
  v_wallet_alice BIGINT;
  v_order_code   BIGINT := 202509160001;  -- pick any unique bigint
  v_amount       NUMERIC := 500000;
BEGIN
  SELECT w.id INTO v_wallet_alice
  FROM wallet w
  JOIN user_entity u ON u.id = w.user_id
  WHERE u.email='alice@example.com';

  IF v_wallet_alice IS NOT NULL THEN
    -- create topup row if not exists
    INSERT INTO wallet_topup (amount, order_code, status, wallet_id, created_at)
    SELECT v_amount, v_order_code, 'SUCCESS', v_wallet_alice, NOW()
    WHERE NOT EXISTS (SELECT 1 FROM wallet_topup wt WHERE wt.order_code = v_order_code);

    -- credit wallet exactly once by checking an existing transaction
    IF NOT EXISTS (
      SELECT 1 FROM transaction_record tr
      WHERE tr.wallet_id = v_wallet_alice AND tr.type = 'TOPUP' AND tr.amount = v_amount
    ) THEN
      INSERT INTO transaction_record (wallet_id, amount, type, created_at)
      VALUES (v_wallet_alice, v_amount, 'TOPUP', NOW());

      UPDATE wallet SET balance = COALESCE(balance,0) + v_amount
      WHERE id = v_wallet_alice;
    END IF;
  END IF;
END$$;

-- =============== DAILY INVENTORY (daily_ticket_inventory needs: date, sold, total_available, ticket_type_id) ===============
-- Seed today for each ticket type created earlier.
INSERT INTO daily_ticket_inventory (date, sold, total_available, ticket_type_id)
SELECT CURRENT_DATE, 0, 500, t.id
FROM ticket_type t
WHERE t.name IN ('Day Ticket (Adult)','Day Ticket (Child)','Evening Pass')
  AND NOT EXISTS (
    SELECT 1 FROM daily_ticket_inventory d
    WHERE d.date = CURRENT_DATE AND d.ticket_type_id = t.id
  );

-- =============== SAMPLE ORDER for Alice (ticket_order & ticket_detail) ===============
DO $$
DECLARE
  v_user_alice    BIGINT;
  v_branch_central BIGINT;
  v_ticket_type    BIGINT;
  v_unit_price     NUMERIC;
  v_order_id       BIGINT;
  v_order_date     DATE := CURRENT_DATE;
  v_qty            INT := 1;
BEGIN
  SELECT id INTO v_user_alice FROM user_entity WHERE email='alice@example.com';
  SELECT id INTO v_branch_central FROM park_branch WHERE name='Central Park';
  SELECT id, base_price INTO v_ticket_type, v_unit_price FROM ticket_type WHERE name='Day Ticket (Adult)';

  IF v_user_alice IS NOT NULL AND v_branch_central IS NOT NULL AND v_ticket_type IS NOT NULL THEN
    -- Create an order for today if none exists yet with same totals
    INSERT INTO ticket_order (final_amount, status, ticket_date, total_amount, park_branch_id, user_id, payment_method, payment_time)
    SELECT v_unit_price * v_qty, 'PAID', v_order_date, v_unit_price * v_qty, v_branch_central, v_user_alice, 'WALLET', NOW()
    WHERE NOT EXISTS (
      SELECT 1 FROM ticket_order o
      WHERE o.user_id=v_user_alice AND o.park_branch_id=v_branch_central
        AND o.ticket_date=v_order_date AND o.total_amount = v_unit_price * v_qty
    )
    RETURNING id INTO v_order_id;

    -- If order already existed, fetch it so we can insert detail idempotently
    IF v_order_id IS NULL THEN
      SELECT id INTO v_order_id
      FROM ticket_order o
      WHERE o.user_id=v_user_alice AND o.park_branch_id=v_branch_central
        AND o.ticket_date=v_order_date AND o.total_amount = v_unit_price * v_qty
      ORDER BY id DESC LIMIT 1;
    END IF;

    -- Add order line if missing
    IF v_order_id IS NOT NULL THEN
      INSERT INTO ticket_detail (final_price, quantity, unit_price, ticket_order_id, ticket_type_id, discount_percent)
      SELECT v_unit_price * v_qty, v_qty, v_unit_price, v_order_id, v_ticket_type, NULL
      WHERE NOT EXISTS (
        SELECT 1 FROM ticket_detail d
        WHERE d.ticket_order_id = v_order_id AND d.ticket_type_id = v_ticket_type
      );

      -- Increment sold in daily inventory for that ticket type & date
      UPDATE daily_ticket_inventory d
      SET sold = sold + v_qty
      WHERE d.date = v_order_date AND d.ticket_type_id = v_ticket_type;
    END IF;
  END IF;
END$$;

COMMIT;

\echo '== Flow seed complete. Quick checks =='
-- Quick checks
SELECT email, username FROM user_entity ORDER BY id;
SELECT u.email, r.name AS role
FROM user_role ur
JOIN user_entity u ON u.id=ur.user_id
JOIN role r ON r.id=ur.role_id
ORDER BY u.email, r.name;

SELECT u.email, w.balance
FROM wallet w JOIN user_entity u ON u.id=w.user_id
ORDER BY u.email;

SELECT o.id, u.email, b.name AS branch, o.ticket_date, o.status, o.total_amount, o.final_amount
FROM ticket_order o
JOIN user_entity u ON u.id=o.user_id
JOIN park_branch b ON b.id=o.park_branch_id
ORDER BY o.id DESC LIMIT 5;

SELECT t.name AS ticket_type, d.date, d.sold, d.total_available
FROM daily_ticket_inventory d
JOIN ticket_type t ON t.id=d.ticket_type_id
WHERE d.date = CURRENT_DATE
ORDER BY t.name;
