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
    file_name varchar(100),
    primary key (`code`)
);

--一条记录对应一个文件
create table large_file(
    id bigint AUTO_INCREMENT not null,
    user_id bigint,
    view_dir varchar(100) COMMENT '前端视图显示的相对路径,is_public为true时,该字段也表示文件所在的磁盘真实路径',
    file_name varchar(50),
    is_public bool,
    md5 varchar(32) COMMENT '前端视图显示的相对路径,is_publicfalse时,该字段决定磁盘真实相对路径',
    primary key (`id`),
    unique key `user_path` (`user_id`,`view_dir`,`file_name`)
);

--数据初始化
insert into user (nick_name,email,password) values ( 'vincent', 'mr.vincent.ge@outlook.com', '4hm1aYkoGnhG3YNhYdeivQ==' );
