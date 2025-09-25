-- allow guest orders (nullable FK)
ALTER TABLE ticket_order
  ALTER COLUMN user_id DROP NOT NULL;

-- guest fields (safe if already exist)
ALTER TABLE ticket_order
  ADD COLUMN IF NOT EXISTS customer_name  varchar(255),
  ADD COLUMN IF NOT EXISTS customer_age   integer,
  ADD COLUMN IF NOT EXISTS customer_email varchar(255),
  ADD COLUMN IF NOT EXISTS customer_phone varchar(50);
