INSERT INTO customer_orders (id, customer_name, email, phone, delivery_address, created_at, total)
SELECT CAST(seed.id AS UUID), seed.customer_name, seed.email, seed.phone,
       seed.delivery_address, seed.created_at, totals.total
FROM (VALUES
    ('2bf727e4-b35c-43c2-af2d-22ca95b69848', 'Sample Customer One', 'order-one@example.com',
     '+12025550101', 'Sample delivery address 1', TIMESTAMP WITH TIME ZONE '2026-01-10 09:30:00+00:00'),
    ('bcc71f88-dd69-4560-9d87-5416ec9d7e18', 'Sample Customer Two', 'order-two@example.com',
     '+12025550102', 'Sample delivery address 2', TIMESTAMP WITH TIME ZONE '2026-01-11 12:00:00+00:00')
) AS seed(id, customer_name, email, phone, delivery_address, created_at)
JOIN (
    SELECT lines.order_id, SUM(product.price * lines.quantity) AS total
    FROM (VALUES
        ('2bf727e4-b35c-43c2-af2d-22ca95b69848', 'bakery', 'Sesame Simit', 3),
        ('2bf727e4-b35c-43c2-af2d-22ca95b69848', 'breakfast', 'Apricot Jam', 1),
        ('bcc71f88-dd69-4560-9d87-5416ec9d7e18', 'chocolate', 'Dark Chocolate Bar', 2),
        ('bcc71f88-dd69-4560-9d87-5416ec9d7e18', 'drinks', 'Sour Cherry Juice', 2)
    ) AS lines(order_id, category_slug, product_name, quantity)
    JOIN products AS product ON product.id = (
        SELECT MIN(id) FROM products
        WHERE category_slug = lines.category_slug AND name = lines.product_name
    )
    GROUP BY lines.order_id
) AS totals ON totals.order_id = seed.id;

INSERT INTO order_items (order_id, product_id, product_name, unit_price, quantity)
SELECT customer_order.id, product.id, product.name, product.price, lines.quantity
FROM (VALUES
    ('2bf727e4-b35c-43c2-af2d-22ca95b69848', 'bakery', 'Sesame Simit', 3),
    ('2bf727e4-b35c-43c2-af2d-22ca95b69848', 'breakfast', 'Apricot Jam', 1),
    ('bcc71f88-dd69-4560-9d87-5416ec9d7e18', 'chocolate', 'Dark Chocolate Bar', 2),
    ('bcc71f88-dd69-4560-9d87-5416ec9d7e18', 'drinks', 'Sour Cherry Juice', 2)
) AS lines(order_id, category_slug, product_name, quantity)
JOIN customer_orders AS customer_order ON customer_order.id = CAST(lines.order_id AS UUID)
JOIN products AS product ON product.id = (
    SELECT MIN(id) FROM products
    WHERE category_slug = lines.category_slug AND name = lines.product_name
);
