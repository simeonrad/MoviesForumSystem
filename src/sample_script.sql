INSERT INTO tags (tag_name) VALUES ('Tag11');
INSERT INTO tags (tag_name) VALUES ('Tag56');
INSERT INTO tags (tag_name) VALUES ('Tag50');
INSERT INTO tags (tag_name) VALUES ('Tag32');
INSERT INTO tags (tag_name) VALUES ('Tag77');
INSERT INTO tags (tag_name) VALUES ('Tag86');
INSERT INTO tags (tag_name) VALUES ('Tag40');
INSERT INTO tags (tag_name) VALUES ('Tag69');
INSERT INTO tags (tag_name) VALUES ('Tag28');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name281', 'Name831', 'user_863', 'password863', 1, 'user_863@example.com', 0, 0);
INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name117', 'Name933', 'user_825', 'password825', 0, 'user_825@example.com', 0, 0);
INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name971', 'Name881', 'user_160', 'password160', 0, 'user_160@example.com', 0, 0);
INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name123', 'Name881', 'user_123', 'password123', 1, 'user_123@example.com', 0, 0);
INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name321', 'Name881', 'user_321', 'password321', 1, 'user_321@example.com', 0, 0);
INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name654', 'Name881', 'user_654', 'password654', 0, 'user_654@example.com', 0, 0);
INSERT INTO users (first_name, last_name, username, password, is_admin, email, is_blocked, is_deleted) VALUES ('Name251', 'Name881', 'user_251', 'password251', 1, 'user_251@example.com', 0, 0);

INSERT INTO phone_numbers (user_id, phone_number) VALUES (20, '1234567890');
INSERT INTO phone_numbers (user_id, phone_number) VALUES (17, '2345678901');
INSERT INTO phone_numbers (user_id, phone_number) VALUES (21, '3456789101');
INSERT INTO phone_numbers (user_id, phone_number) VALUES (23, '4567891011');

INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (1, 'Post Title 101', 'Content 1001 Content 1001 Content 1001Content 1001', '2024-02-05 17:37:11', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (2, 'Post Title 102', 'Content 1002 Content 1001 Content 1001 Content 1001', '2024-02-05 17:37:15', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (3, 'Post Title 103', 'Content 1003 Content 1001 Content 1001 Content 1001', '2024-02-05 17:37:20', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (4, 'Post Title 104', 'Content 1003 Content 1001 Content 1001 Content 1001 yeah', '2024-02-05 17:37:21', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (10, 'Post Title 105', 'Content 1003 cool Content 1001Content 1001Content 1001', '2024-02-05 17:37:25', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (18, 'Post Title 106', 'Content 1003 magic Content 1001Content 1001Content 1001', '2024-02-05 17:37:30', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (19, 'Post Title 107', 'Content 1003 mark it Content 1001Content 1001Content 1001', '2024-02-05 17:37:32', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (23, 'Post Title 108', 'Content 1003 sum up Content 1001Content 1001Content 1001', '2024-02-05 17:37:51', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (22, 'Post Title 109', 'Content 1003 sum it noow Content 1001Content 1001', '2024-02-05 17:38:00', 0);
INSERT INTO posts (user_id, title, content, date_created, likes) VALUES (15, 'Post Title 110', 'Content 1003 kay kay Content 1001Content 1001Content 1001', '2024-02-05 17:38:11', 0);

INSERT INTO comments (post_id, user_id, content, date_created) VALUES (1, 1, 'Comment Content 1', '2024-02-05 17:37:11');
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (4, 2, 'Comment Content 2', '2024-02-05 17:37:12');
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (3, 3, 'Comment Content 3', '2024-02-05 17:37:15');

INSERT INTO posts_tags (tag_id, post_id) VALUES (1, 1);
INSERT INTO posts_tags (tag_id, post_id) VALUES (4, 5);
INSERT INTO posts_tags (tag_id, post_id) VALUES (3, 3);