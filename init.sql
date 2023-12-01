drop database if exists easypan;
create database easypan;
use easypan;

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
