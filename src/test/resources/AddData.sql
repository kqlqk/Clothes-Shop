delete
from buying_order;
delete
from color;
delete
from size;
delete
from product_amount;
delete
from product;
delete
from address;
delete
from customer;
delete
from refresh_token;



ALTER SEQUENCE color_id_seq RESTART WITH 1;
ALTER SEQUENCE size_id_seq RESTART WITH 1;
ALTER SEQUENCE product_amount_id_seq RESTART WITH 1;
ALTER SEQUENCE product_id_seq RESTART WITH 1;
ALTER SEQUENCE customer_id_seq RESTART WITH 1;
ALTER SEQUENCE address_id_seq RESTART WITH 1;
ALTER SEQUENCE refresh_token_id_seq RESTART WITH 1;
ALTER SEQUENCE buying_order_id_seq RESTART WITH 1;


insert into product (name, price, discount, path, description)
values ('black t-shirt', 10, 0, '/tshirts/1', 'some description');

insert into product (name, price, discount, path, description)
values ('white oversize t-shirt', 0, 20, '/tshirts/2', 'some description');


insert into color (color, product_id)
values ('BLACK', 1);

insert into color (color, product_id)
values ('WHITE', 1);


insert into size (size, product_id)
values ('M', 1);

insert into size (size, product_id)
values ('L', 1);


insert into product_amount (product_id, color_id, size_id, amount)
values (1, 1, 1, 10);

insert into product_amount (product_id, color_id, size_id, amount)
values (1, 2, 1, 5);


insert into address(country, city, street, house, postal_code)
values ('Poland', 'Warsaw', 'os. Bohaterego', '10/4', '93214');

insert into address(country, city, street, house, postal_code)
values ('Usa', 'Texas', 'Main avenue', '2/1', '434332');


insert into customer (name, address_id, email, password)
values ('test1', 1, 'email@email.com', 'Password1');

insert into customer (name, address_id, email, password)
values ('test3', 2, 'email2@email.com', 'Password2');


insert into refresh_token (customer_id, token)
values (1, 'token1');


insert into buying_order (product_id, size_id, color_id, realised, create_date, delivered_date, uuid, customer_id)
values (1, 1, 1, false, '10/07/2023 02:00:00', null, 2, 1);

insert into buying_order (product_id, size_id, color_id, realised, create_date, delivered_date, uuid, customer_id)
values (2, 2, 2, true, '03/06/2023 12:42:43', null, 1, null);
