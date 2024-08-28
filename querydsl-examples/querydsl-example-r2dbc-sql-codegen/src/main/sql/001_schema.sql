create table if not exists supplier (
  id long auto_increment primary key,
  code varchar(255),
  name varchar(255)
);

create table if not exists product (
  id long auto_increment primary key,
  supplier_id long,
  name varchar(64),
  price double,
  other_product_details varchar(64),
  
  constraint supplier_fk foreign key (supplier_id) references supplier(id)
);

create table if not exists product_l10n (
  product_id long,
  lang varchar(5),
  name varchar(128),
  description varchar(255),  
  
  constraint product_fk foreign key (product_id) references product(id)
);

create table if not exists person (
  id long auto_increment primary key,
  first_name varchar(64),
  last_name varchar(64),
  phone varchar(64),
  email varchar(64)
);

create table if not exists customer (
  id long auto_increment primary key,
  name varchar(64),
  contact_person_id long,
 
  constraint contact_person_fk foreign key (contact_person_id) references person(id)
);

create table if not exists customer_payment_method (
  id long auto_increment primary key,
  customer_id long,
  payment_method_code varchar(12),
  card_number varchar(24),
  from_date date,
  to_date date,
  other_details varchar(128),
  
  constraint customer_fk foreign key (customer_id) references customer(id)
);

create table if not exists customer_order (
  id long auto_increment primary key,
  customer_id long,
  customer_payment_method_id long,
  order_status varchar(12),
  order_placed_date date,
  order_paid_date date,
  total_order_price double,
  
  constraint customer2_fk foreign key (customer_id) references customer(id),
  constraint payment_method_fk foreign key (customer_payment_method_id) references customer_payment_method(id)
);

create table if not exists customer_order_product (
  order_id long,
  product_id long,
  quantity int,
  comments varchar(12),
  
  constraint order_fk foreign key (order_id) references customer_order(id),
  constraint product2_fk foreign key (product_id) references product(id)
);

create table if not exists customer_order_delivery (
  order_id long,
  reported_date date,
  delivery_status_code varchar(12),
  
  constraint order2_fk foreign key (order_id) references customer_order(id)
);

create table if not exists address (
  id long auto_increment primary key,
  street varchar(64),
  zip varchar(64),
  town varchar(64),
  state varchar(64),
  country varchar(3),
  other_details varchar(64) 
);

create table if not exists customer_address (
  customer_id long,
  address_id long,
  from_date date,
  to_date date,
  address_type_code varchar(12),
  
  constraint customer3_fk foreign key (customer_id) references customer(id),
  constraint address_fk foreign key (address_id) references address(id)
);
