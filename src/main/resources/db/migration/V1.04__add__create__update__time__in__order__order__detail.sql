ALTER TABLE orders ADD COLUMN create_at timestamp;
ALTER TABLE orders ADD COLUMN update_at timestamp;
ALTER TABLE order_detail ADD COLUMN create_at timestamp;
ALTER TABLE order_detail ADD COLUMN update_at timestamp;
