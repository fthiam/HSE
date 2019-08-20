-- Cleanup
DROP DATABASE IF EXISTS homeSharingExpensesDatabase;
CREATE DATABASE homeSharingExpensesDatabase;
USE homeSharingExpensesDatabase;

-- Cleanup
DROP TABLE IF EXISTS T_MEMBER;
DROP TABLE IF EXISTS T_GROUP;
DROP TABLE IF EXISTS T_SHOPPING_LIST;
DROP TABLE IF EXISTS T_ITEM;

-- Create
-- Entity tables  group_fk VARCHAR(10), FOREIGN KEY (group_fk) REFERENCES T_GROUP(id)    , balance FLOAT  id VARCHAR(10), PRIMARY KEY(id),
CREATE TABLE T_MEMBER( id VARCHAR(10), PRIMARY KEY(id), name VARCHAR(50) NOT NULL, emailAdress VARCHAR(50) NOT NULL, passWord VARCHAR(20), information VARCHAR(150)) ENGINE=INNODB;
CREATE TABLE T_GROUP( id VARCHAR(10), PRIMARY KEY(id), name VARCHAR(50) NOT NULL, description VARCHAR(150), Budget FLOAT) ENGINE=INNODB;
CREATE TABLE T_SHOPPING_LIST( id VARCHAR(10), PRIMARY KEY(id), name VARCHAR(50) NOT NULL,  _GROUP_ID VARCHAR(50)) ENGINE=INNODB;
CREATE TABLE T_ITEM( id VARCHAR(10), PRIMARY KEY(id), name VARCHAR(50) NOT NULL, price FLOAT, description VARCHAR(150), state BOOLEAN, _SHOPPINGLIST_id VARCHAR(50)) ENGINE=INNODB;
-- Association tables
CREATE TABLE T_GROUP_MEMBER(id int NOT NULL AUTO_INCREMENT,PRIMARY KEY(id),  groupID VARCHAR(50) NOT NULL, memberID VARCHAR(50) NOT NULL, balance FLOAT default 0) ENGINE=INNODB;
CREATE TABLE T_INVITATION( id int NOT NULL AUTO_INCREMENT,PRIMARY KEY(id), groupID VARCHAR(50) NOT NULL, memberID VARCHAR(50) NOT NULL) ENGINE=INNODB;
CREATE TABLE T_ITEM_OWNER(id int NOT NULL AUTO_INCREMENT,PRIMARY KEY(id), memberID VARCHAR(50) NOT NULL, _ITEM_id VARCHAR(50) NOT NULL) ENGINE=INNODB;


CREATE TABLE T_COUNTER( name VARCHAR(20), PRIMARY KEY(name), value INTEGER NOT NULL);
INSERT INTO T_COUNTER (name, value) VALUES
('Group', '1'),
('Member', '1'),
('ShoppingList', '1'),
('Item', '1'),
('GroupMember', '1'),
('ItemOwner', '1');
