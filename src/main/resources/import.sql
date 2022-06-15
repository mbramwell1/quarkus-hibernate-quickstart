INSERT INTO customer(id, name) VALUES (nextval('hibernate_sequence'), 'Joe', 'Chef');
INSERT INTO customer(id, name) VALUES (nextval('hibernate_sequence'), 'Bob', 'Pilot');
INSERT INTO customer(id, name) VALUES (nextval('hibernate_sequence'), 'Skippy', 'Asshole');

INSERT INTO customerOrder(id, item, customerId, customerName) VALUES (nextval('hibernate_sequence'), 'Apple', '1', 'Joe');
INSERT INTO customerOrder(id, item, customerId, customerName) VALUES (nextval('hibernate_sequence'), 'Peach', '1', 'Joe');
INSERT INTO customerOrder(id, item, customerId, customerName) VALUES (nextval('hibernate_sequence'), 'Car', '2', 'Bob');
INSERT INTO customerOrder(id, item, customerId, customerName) VALUES (nextval('hibernate_sequence'), 'Velvis', '3', 'Skippy');