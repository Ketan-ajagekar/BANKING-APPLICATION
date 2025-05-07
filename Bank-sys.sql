create database bankSystem;
use bankSystem;
create table signup(form_no varchar(30), name varchar(30), father_name varchar(30),DOB varchar(30), gender varchar(30), email varchar(60), marital_status varchar(30), address varchar(60), city varchar(30), pincode varchar(30), state varchar(50));
select * from signup;

create table signuptwo(form_no varchar(30), religion varchar(30), category varchar(30),income varchar(30), education varchar(30), occuption varchar(60), pan varchar(30), aadhar varchar(60), seniorcitizen varchar(30), existing_account varchar(30));
select * from signuptwo;

create table signupthree(form_no varchar(30), account_Type varchar(40), card_number varchar(16) unique, pin varchar (6), facility varchar(200), FOREIGN KEY (form_no) REFERENCES signup(form_no) ON DELETE CASCADE);
select * from signupthree;

create table login(form_no varchar(30), card_number VARCHAR(16) unique, pin varchar(6), FOREIGN KEY (form_no) REFERENCES signupthree(form_no) ON DELETE CASCADE);
select * from login;

CREATE TABLE bank (id INT AUTO_INCREMENT PRIMARY KEY, pin VARCHAR(6) NOT NULL, card_number VARCHAR(16), type ENUM('Deposit', 'Withdrawal') NOT NULL, amount INT NOT NULL, FOREIGN KEY (card_number) REFERENCES login(card_number) ON DELETE CASCADE);
select * from bank;
ALTER TABLE bank MODIFY COLUMN amount INT;
ALTER TABLE bank ADD COLUMN id INT AUTO_INCREMENT PRIMARY KEY;
ALTER TABLE bank ADD COLUMN new_date DATETIME;
UPDATE bank SET new_date = STR_TO_DATE(SUBSTRING_INDEX(date, ' IST', 1), '%a %b %d %H:%i:%s %Y')WHERE date IS NOT NULL;
ALTER TABLE bank DROP COLUMN date;
ALTER TABLE bank CHANGE COLUMN new_date date DATETIME;
SELECT * FROM bank WHERE card_number IS NULL;
SET SQL_SAFE_UPDATES = 0;
UPDATE bank SET card_number = ' ' WHERE card_number IS NULL;
ALTER TABLE bank MODIFY COLUMN card_number VARCHAR(16) NOT NULL;
SET SQL_SAFE_UPDATES = 1;
SELECT * FROM bank WHERE card_number = '';
UPDATE bank SET card_number = ' ' WHERE card_number = '';
UPDATE bank SET card_number = ' ' WHERE id = SOME_ID;
SELECT * FROM bank WHERE card_number IS NULL;
SELECT COUNT(*) FROM bank;
SHOW CREATE TABLE bank;
ALTER TABLE bank MODIFY COLUMN card_number VARCHAR(16) NOT NULL DEFAULT ' ';
UPDATE bank SET card_number = ' ' WHERE card_number IS NULL OR card_number = '';
ALTER TABLE bank ALTER COLUMN card_number DROP DEFAULT;
SELECT COUNT(*) FROM bank WHERE card_number IS NULL OR card_number = '';
SELECT COUNT(*) FROM bank WHERE card_number = '';
SHOW COLUMNS FROM bank LIKE 'card_number';
SET SQL_SAFE_UPDATES = 0;
UPDATE bank SET card_number = '1409962925395953' WHERE card_number = '';
SET SQL_SAFE_UPDATES = 1;
INSERT INTO bank (pin, type, amount)VALUES ('123456', 'Deposit', 500);
SELECT * FROM bank WHERE pin = '123456';
ALTER TABLE bank MODIFY pin VARCHAR(10) DEFAULT '0000';

DROP TABLE IF EXISTS bank;
CREATE TABLE bank (id INT AUTO_INCREMENT PRIMARY KEY, pin VARCHAR(6) NOT NULL,card_number VARCHAR(16) NOT NULL ,type ENUM('Deposit', 'Withdrawal') NOT NULL,amount INT NOT NULL,date DATETIME DEFAULT CURRENT_TIMESTAMP,FOREIGN KEY (card_number) REFERENCES login(card_number) ON DELETE CASCADE);
UPDATE bank SET card_number = (SELECT card_number FROM login LIMIT 1)WHERE card_number IS NULL OR card_number = '';
SHOW CREATE TABLE bank;
SELECT * FROM bank;

create table accounts(account_no varchar(10) PRIMARY KEY,  pin VARCHAR(6) NOT NULL, balance DECIMAL(10,2) NOT NULL);
select * from accounts;

create table transaction(id INT AUTO_INCREMENT PRIMARY KEY, sender_card_number VARCHAR(16), receiver_card_number VARCHAR(16), amount DECIMAL(10,2), transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (sender_card_number) REFERENCES login(card_number) ON DELETE CASCADE, FOREIGN KEY (receiver_card_number) REFERENCES login(card_number) ON DELETE CASCADE);
select * from transaction;

DESC login;
DESC bank;
