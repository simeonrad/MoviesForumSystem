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
    user_id           int auto_increment
        primary key,
    first_name        varchar(32)          not null,
    last_name         varchar(32)          not null,
    username          varchar(50)          not null,
    password          varchar(50)          not null,
    is_admin          tinyint(1)           not null,
    email             varchar(100)         not null,
    is_blocked        tinyint(1) default 0 not null,
    is_deleted        tinyint(1) default 0 not null,
    profile_photo_url varchar(255)         null,
    constraint users_pk
        unique (email)
);

create table phone_numbers
(
    user_id         int         null,
    phone_number    varchar(30) not null,
    phone_number_id int auto_increment
        primary key,
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
    post_id       int          null,
    user_id       int          not null,
    content       varchar(500) not null,
    date_created  datetime     not null,
    date_modified datetime     null,
    repliedTo_id  int          null,
    constraint comments_comments_comment_id_fk
        foreign key (repliedTo_id) references comments (comment_id),
    constraint comments_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint comments_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

create table post_likes
(
    post_id int not null,
    user_id int not null,
    primary key (post_id, user_id),
    constraint post_likes_ibfk_1
        foreign key (post_id) references posts (post_id),
    constraint post_likes_ibfk_2
        foreign key (user_id) references users (user_id)
);

create index user_id
    on post_likes (user_id);

create table posts_tags
(
    tag_id  int null,
    post_id int null,
    constraint posts_tags_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint posts_tags_tags_tag_id_fk
        foreign key (tag_id) references tags (tag_id)
);
create table post_views
(
    view_id    int auto_increment
        primary key,
    time_stamp timestamp not null,
    post_id    int       not null,
    user_id    int       not null,
    constraint post_views_posts_post_id_fk
        foreign key (post_id) references posts (post_id),
    constraint post_views_users_user_id_fk
        foreign key (user_id) references users (user_id)
);

ALTER TABLE post_views DROP FOREIGN KEY post_views_posts_post_id_fk;
ALTER TABLE post_views ADD CONSTRAINT post_views_posts_post_id_fk
    FOREIGN KEY (post_id) REFERENCES posts (post_id) ON DELETE CASCADE;