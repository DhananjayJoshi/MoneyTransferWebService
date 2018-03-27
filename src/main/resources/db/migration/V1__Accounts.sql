
-- DROP TABLE IF EXISTS accounts;

CREATE TABLE IF NOT EXISTS accounts
(
  id                SERIAL not null,
  account_id        TEXT UNIQUE NOT NULL,
  balance           double precision NOT NULL DEFAULT 0,
  CONSTRAINT pk_accounts_id PRIMARY KEY (id)
);


INSERT INTO accounts
(account_id, balance)
VALUES
('A', 100.00),
('B', 120.00),
('C', 700.00),
('D', 1000.00);
