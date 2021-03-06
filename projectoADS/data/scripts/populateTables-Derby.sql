INSERT INTO PRODUCT (ID, ITEMID, DESCRIPTION, PRICE, QTY) VALUES (1001, 101, 'GTX 1080',     700, 20)
INSERT INTO PRODUCT (ID, ITEMID, DESCRIPTION, PRICE, QTY) VALUES (1002, 102, 'Titan V',       3000, 10)
INSERT INTO PRODUCT (ID, ITEMID, DESCRIPTION, PRICE, QTY) VALUES (1003, 103, 'Tesla V100',       9000, 5)
INSERT INTO PRODUCT (ID, ITEMID, DESCRIPTION, PRICE, QTY) VALUES (1004, 104, 'GTX 1080TI',      1100, 15)
INSERT INTO PRODUCT (ID, ITEMID, DESCRIPTION, PRICE, QTY) VALUES (1005, 105, 'GTX 1070TI',       500,   30)
INSERT INTO PRODUCT (ID, ITEMID, DESCRIPTION, PRICE, QTY) VALUES (1006, 106, 'GTX 1070', 400,  100)
INSERT INTO RENTAL (ID, DATE, RETURN_DATE, TOTAL, STATUS, RETURN_STATUS) VALUES (2001, '03/26/2018', '03/29/2018', 1650, 'C', 1)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (901, 2001, 1002, 10)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (902, 2001, 1004, 20)
INSERT INTO RENTAL (ID, DATE, RETURN_DATE, TOTAL, STATUS, RETURN_STATUS) VALUES (2002, '04/11/2018', '04/25/2018', 22750, 'C', 1)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (903, 2002, 1001, 100)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (904, 2002, 1002, 150)
INSERT INTO RENTAL (ID, DATE, RETURN_DATE, TOTAL, STATUS, RETURN_STATUS) VALUES (2003, '04/12/2018', '05/22/2018', 4150, 'C', 1)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (905, 2003, 1002, 10)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (906, 2003, 1003, 10)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (907, 2003, 1004, 20)
INSERT INTO RENTALPRODUCT (ID, RENTAL_ID, PRODUCT_ID, QTY) VALUES (908, 2003, 1006, 10)