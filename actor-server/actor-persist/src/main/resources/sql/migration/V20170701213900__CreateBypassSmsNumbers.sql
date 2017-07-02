drop table bypass_sms_numbers;
CREATE TABLE bypass_sms_numbers (
  "phone"             BIGINT PRIMARY KEY,
  "transaction_hash"  VARCHAR(255) NULL,
  "code"              VARCHAR(10) NULL
);

