\echo '== Required columns (NOT NULL and no default) in your public tables =='
SELECT c.table_name,
       c.column_name,
       c.data_type,
       c.is_nullable,
       c.column_default,
       CASE
         WHEN c.is_nullable = 'NO' AND c.column_default IS NULL THEN 'REQUIRED'
         ELSE ''
       END AS required
FROM information_schema.columns c
JOIN information_schema.tables t
  ON t.table_schema = c.table_schema
 AND t.table_name   = c.table_name
WHERE c.table_schema = 'public'
  AND t.table_type   = 'BASE TABLE'
  AND c.table_name IN (
    'amenity_type','park_branch','branch_amenity',
    'ticket_type','daily_ticket_inventory',
    'user_entity','role','permission','user_role','role_permission',
    'wallet','wallet_topup','transaction_record',
    'ticket_order','ticket_detail'
  )
ORDER BY
  c.table_name,
  /* repeat the logic instead of using the alias inside CASE */
  CASE WHEN c.is_nullable = 'NO' AND c.column_default IS NULL THEN 0 ELSE 1 END,
  c.ordinal_position;

\echo '== Done =='
