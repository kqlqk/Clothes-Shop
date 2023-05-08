delete
from product_tag;
delete
from product;
delete
from tag;

ALTER SEQUENCE product_tag_id_seq RESTART WITH 1;
ALTER SEQUENCE product_id_seq RESTART WITH 1;
ALTER SEQUENCE tag_id_seq RESTART WITH 1;

insert into product (name, price, path, description)
values ('black t-shirt', 10, '/tshirts/1', 'some description');

insert into product (name, price, path, description)
values ('white oversize t-shirt', 20, '/tshirts/2', 'some description');

insert into tag (name)
values ('BLACK');

insert into tag (name)
values ('XL');

insert into product_tag (product_id, tag_id)
values (1, 1);

insert into product_tag (product_id, tag_id)
values (1, 2);

insert into product_tag (product_id, tag_id)
values (2, 2);