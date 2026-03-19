CREATE TABLE currencies (
                            id SERIAL PRIMARY KEY,
                            code TEXT UNIQUE NOT NULL,
                            full_name  TEXT UNIQUE NOT NULL,
                            sign TEXT NOT NULL
);

CREATE TABLE exchange_rates (
                                id SERIAL PRIMARY KEY,

                                base_currency_id INTEGER NOT NULL,
                                target_currency_id INTEGER NOT NULL,

                                rate DECIMAL(14, 6) NOT NULL,

                                CONSTRAINT fk_base_currency
                                    FOREIGN KEY (base_currency_id)
                                        REFERENCES currencies(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_target_currency
                                    FOREIGN KEY (target_currency_id)
                                        REFERENCES currencies(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT unique_currency_pair
                                    UNIQUE (base_currency_id, target_currency_id)
);