drop database if exists easypan;
create database easypan;
use easypan;

--表格初始化
create table user(
    id bigint AUTO_INCREMENT not null,
    nick_name varchar(20),
    email varchar(150),
    password varchar(32),
    join_time datetime,
    last_login_time datetime,
    status tinyint(1),
    use_space bigint(20),
    primary key (`id`),
    unique key `key_email` (`email`),
    unique key `key_nick_name` (`nick_name`)
);

create table code2path(
    code varchar(20),
    path varchar(100),
    primary key (`code`)
);

--数据初始化
insert into user (nick_name,email,password) values ( 'vincent', 'mr.vincent.ge@outlook.com', '4hm1aYkoGnhG3YNhYdeivQ==' );
