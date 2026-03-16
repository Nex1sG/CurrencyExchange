INSERT INTO currencies (id, code, full_name, sign) VALUES
                                                       (1, 'USD', 'United States Dollar', '$'),
                                                       (2, 'EUR', 'Euro', '€'),
                                                       (3, 'GBP', 'British Pound Sterling', '£'),
                                                       (4, 'JPY', 'Japanese Yen', '¥'),
                                                       (5, 'CNY', 'Chinese Yuan', '¥'),
                                                       (6, 'RUB', 'Russian Ruble', '₽'),
                                                       (7, 'CHF', 'Swiss Franc', '₣'),
                                                       (8, 'CAD', 'Canadian Dollar', '$'),
                                                       (9, 'AUD', 'Australian Dollar', '$');
INSERT INTO exchange_rates (id, base_currency_id, target_currency_id, rate) VALUES
                                                                                (1, 1, 2, 0.92),   -- USD → EUR
                                                                                (2, 2, 1, 1.09),   -- EUR → USD
                                                                                (3, 1, 3, 0.78),   -- USD → GBP
                                                                                (4, 3, 1, 1.28),   -- GBP → USD
                                                                                (5, 1, 4, 150.25), -- USD → JPY
                                                                                (6, 4, 1, 0.0067), -- JPY → USD
                                                                                (7, 1, 5, 7.20),   -- USD → CNY
                                                                                (8, 5, 1, 0.14),   -- CNY → USD
                                                                                (9, 1, 6, 92.50),  -- USD → RUB
                                                                                (10, 6, 1, 0.0108),-- RUB → USD
                                                                                (11, 1, 7, 0.88),  -- USD → CHF
                                                                                (12, 7, 1, 1.13),  -- CHF → USD
                                                                                (13, 1, 8, 1.35),  -- USD → CAD
                                                                                (14, 8, 1, 0.74),  -- CAD → USD
                                                                                (15, 1, 9, 1.50),  -- USD → AUD
                                                                                (16, 9, 1, 0.67);  -- AUD → USD

