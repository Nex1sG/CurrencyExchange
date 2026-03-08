CREATE TABLE currencies (
                            id SERIAL PRIMARY KEY,
                            code TEXT UNIQUE,
                            full_name TEXT,
                            sign TEXT
);

CREATE TABLE exchange_rates (
                                id SERIAL PRIMARY KEY,
                                base_currency_id INTEGER,
                                target_currency_id INTEGER,
                                rate REAL
);
