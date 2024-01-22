create table tags
(
    tag_id   int auto_increment
        primary key,
    tag_name varchar(100) not null,
    constraint tags_pk_2
        unique (tag_name)
);

create table users
(
    user_id    int auto_increment
        primary key,
    first_name varchar(32)          not null,
    last_name  varchar(32)          not null,
    username   varchar(50)          not null,
    password   varchar(50)          not null,
    is_admin   tinyint(1)           not null,
    email      varchar(100)         not null,
    is_blocked tinyint(1) default 0 not null,
    constraint users_pk
        unique (email)
);

create table phone_numbers
(
    user_id      int         null,
    phone_number varchar(30) not null,
    constraint phone_numbers_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table posts
(
    post_id       int auto_increment
        primary key,
    user_id       int           not null,
    title         varchar(64)   not null,
    content       varchar(8192) not null,
    date_created  datetime      not null,
    likes         int default 0 null,
    date_modified datetime      null,
    constraint posts_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table comments
(
    comment_id    int auto_increment
        primary key,
    post_id       int          not null,
    user_id       int          not null,
    content       varchar(500) not null,
    date_created  datetime     not null,
    date_modified datetime     not null,
    constraint comments_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint comments_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table post_tags
(
    tag_id  int null,
    post_id int null,
    constraint post_tags_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint post_tags_tags_tag_id_fk
        foreign key (tag_id) references tags (tag_id)
);

