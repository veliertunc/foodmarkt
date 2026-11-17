INSERT INTO categories (slug, name)
SELECT seed.slug, seed.name
FROM (VALUES
    ('pantry', 'Pantry'),
    ('drinks', 'Drinks')
) AS seed(slug, name)
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE categories.slug = seed.slug
);

INSERT INTO products (name, description, price, available, category_slug)
SELECT seed.name, seed.description, seed.price, TRUE, seed.category_slug
FROM (VALUES
    ('Sesame Simit', 'A crisp sesame-crusted bread ring, baked until golden.', 25.00, 'bakery'),
    ('Olive Poğaça', 'A soft savory pastry filled with black olives.', 45.00, 'bakery'),
    ('Dark Chocolate Bar', 'A 70% cocoa dark chocolate bar, 80 g.', 110.00, 'chocolate'),
    ('Hazelnut Chocolate Bites', 'Milk chocolate bites with roasted hazelnuts, 100 g.', 135.00, 'chocolate'),
    ('Honey Oat Granola', 'Baked rolled oats with honey and almonds, 300 g.', 165.00, 'breakfast'),
    ('Apricot Jam', 'A bright apricot preserve with pieces of fruit, 280 g.', 115.00, 'breakfast'),
    ('Extra Virgin Olive Oil', 'Cold-pressed olive oil for dressings and cooking, 500 ml.', 320.00, 'pantry'),
    ('Red Lentils', 'Dried red lentils for soups and stews, 500 g.', 85.00, 'pantry'),
    ('Sour Cherry Juice', 'A tart cherry juice served chilled, 250 ml.', 55.00, 'drinks'),
    ('Sparkling Mineral Water', 'Plain sparkling mineral water, 200 ml.', 20.00, 'drinks')
) AS seed(name, description, price, category_slug)
WHERE NOT EXISTS (
    SELECT 1 FROM products
    WHERE products.category_slug = seed.category_slug AND products.name = seed.name
);
