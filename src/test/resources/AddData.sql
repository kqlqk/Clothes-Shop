delete
from product_color;
delete
from product_size;
delete
from product;
delete
from color;
delete
from size;
delete
from customer;
delete
from buying_order;
delete
from current_order;
delete
from last_order;


ALTER SEQUENCE product_color_id_seq RESTART WITH 1;
ALTER SEQUENCE product_size_id_seq RESTART WITH 1;
ALTER SEQUENCE product_id_seq RESTART WITH 1;
ALTER SEQUENCE color_id_seq RESTART WITH 1;
ALTER SEQUENCE size_id_seq RESTART WITH 1;
ALTER SEQUENCE customer_id_seq RESTART WITH 1;
ALTER SEQUENCE buying_order_id_seq RESTART WITH 1;
ALTER SEQUENCE current_order_id_seq RESTART WITH 1;
ALTER SEQUENCE last_order_id_seq RESTART WITH 1;


insert into product (name, price, path, description)
values ('black t-shirt', 10, '/tshirts/1', 'some description');

insert into product (name, price, path, description)
values ('white oversize t-shirt', 20, '/tshirts/2', 'some description');


insert into color (name)
values ('BLACK');

insert into color (name)
values ('WHITE');


insert into size (name)
values ('M');

insert into size (name)
values ('L');


insert into product_color (product_id, color_id)
values (1, 1);

insert into product_color (product_id, color_id)
values (1, 2);

insert into product_color (product_id, color_id)
values (2, 2);


insert into product_size (product_id, size_id)
values (1, 1);

insert into product_size (product_id, size_id)
values (1, 2);

insert into product_size (product_id, size_id)
values (2, 2);


insert into customer (name, address)
values ('test1', 'test2');

insert into customer (name, address)
values ('test3', 'test4');


insert into buying_order (product_id, size_id, color_id)
values (1, 1, 1);

insert into buying_order (product_id, size_id, color_id)
values (2, 2, 2);


insert into current_order (user_id, order_id)
values (1, 1);

insert into current_order (user_id, order_id)
values (2, 2);


insert into last_order (user_id, order_id)
values (1, 1);

insert into last_order (user_id, order_id)
values (2, 2);