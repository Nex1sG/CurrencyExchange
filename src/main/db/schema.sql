

CREATE TABLE currencies (
                            ID SERIAL PRIMARY KEY,
                            Code TEXT UNIQUE,
                            FullName TEXT,
                            Sign TEXT
);

CREATE TABLE exchange_rates (
                                ID SERIAL PRIMARY KEY,
                                BaseCurrencyId INTEGER,
                                TargetCurrencyId INTEGER,
                                Rate DECIMAL(14, 6)
);
