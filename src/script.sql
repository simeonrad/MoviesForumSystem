INSERT INTO tags (tag_name) VALUES ('Science Fiction');

INSERT INTO tags (tag_name) VALUES ('Romance');

INSERT INTO tags (tag_name) VALUES ('Action');

INSERT INTO tags (tag_name) VALUES ('Horror');

INSERT INTO tags (tag_name) VALUES ('Television');

INSERT INTO tags (tag_name) VALUES ('Western');

INSERT INTO tags (tag_name) VALUES ('Fantasy');

INSERT INTO tags (tag_name) VALUES ('Drama');

INSERT INTO tags (tag_name) VALUES ('Documentary');

INSERT INTO tags (tag_name) VALUES ('Thriller');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('John', 'Doe', 'johndoe', 'password123', 0, 'john.doe@example.com', 'https://example.com/photos/johndoe.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Jane', 'Smith', 'janesmith', 'securepass', 1, 'jane.smith@example.com', 'https://example.com/photos/janesmith.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Alex', 'Johnson', 'alexjohnson', 'mypassword', 0, 'alex.johnson@example.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Brad', 'Pitt', 'brad_pitt', 'actor123', 0, 'brad.pitt@collactors.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Anjela', 'Dzhulianova', 'anjitu123', 'kokonata', 0, 'anji.jolie@abv.bg', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Johnny', 'Depp', 'johnny_depp', 'johnjohn', 1, 'johnny.depp@gmail.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Meryl', 'Streep', 'mery1949', 'merythebest', 1, 'meryl.streep@yahoo.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Jennifer', 'Aniston', 'jenny_ani', 'jennypass', 0, 'aniston.j@gmail.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('Anne', 'Hathaway', 'anny_baby', 'anny_best', 0, 'anny.h@gmail.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO users (first_name, last_name, username, password, is_admin, email, profile_photo_url)
VALUES ('The Boss', 'Adminov', 'admin', 'admin', 0, 'admin@ilmforum.com', 'https://example.com/photos/alexjohnson.jpg');

INSERT INTO posts (user_id, title, content, date_created)
VALUES (2, 'The Devil Wears Prada Review', 'A fascinating glimpse into the fashion industry.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (3, 'Sex and the City Review', 'An iconic series that explores friendship and love in New York City.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (1, 'Mission Impossible Review', 'An adrenaline-fueled action movie with impressive stunts.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (2, 'Pretty Woman Review', 'A modern Cinderella story set in Los Angeles.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (5, 'The Tourist Review', 'A captivating thriller that weaves romance and mystery, starring Johnny Depp and Angelina Jolie in a visually stunning European backdrop.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (6, 'Mr. & Mrs. Smith: Action Packed Romance', 'The dynamic duo of Brad Pitt and Angelina Jolie sparkles in this action-packed comedy about a secret spy couple.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (7, 'Mamma Mia: A Musical Delight', 'A heartwarming tale set on a picturesque Greek island, featuring ABBA’s timeless songs. A perfect blend of music, dance, and romance.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (8, 'Pirates of the Caribbean: An Adventure on the High Seas', 'Join Captain Jack Sparrow in this swashbuckling adventure across the Caribbean. A story of treachery, ghostly pirates, and hidden treasure.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (9, 'Troy: An Epic of Love and War', 'A cinematic retelling of Homer’s Iliad, focusing on the siege of Troy. A tale of heroism, betrayal, and the complexities of human nature.', NOW());

INSERT INTO posts (user_id, title, content, date_created)
VALUES (9, 'Barbie: More Than Just a Doll', 'Exploring the world of Barbie as she steps into various roles, showcasing empowerment, dreams, and the importance of imagination in a colorful universe.', NOW());


INSERT INTO comments (post_id, user_id, content, date_created) VALUES (1, 2, 'OMG! I love that movie. I met my girlfriend watching it. Blockbuster!', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (2, 1, 'Booooooooooring!', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (1, 3, 'And you think this is a good movie?!', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (4, 5, 'Ive never been a fan of such movies. They are just not my thing', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (5, 6, 'Crying every time I see this movie. Magnificent!', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (6, 7, 'One and only, but for the people that do not have a taste for movies. For a person like me this is nothing more than a baaaaaad job!', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (7, 8, 'Yes, I totally a fan of that movie. Watched it more than twice!', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (8, 9, 'Have you ever considered watching a telenovela instead of this?', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (9, 10, 'Not best, not worse, somewhere in the middle.', NOW());
INSERT INTO comments (post_id, user_id, content, date_created) VALUES (10, 4, 'Cannot think of a great movie!', NOW());

INSERT INTO post_likes (post_id, user_id) VALUES (1, 2);
INSERT INTO post_likes (post_id, user_id) VALUES (2, 3);
INSERT INTO post_likes (post_id, user_id) VALUES (3, 1);
INSERT INTO post_likes (post_id, user_id) VALUES (3, 10);
INSERT INTO post_likes (post_id, user_id) VALUES (4, 9);
INSERT INTO post_likes (post_id, user_id) VALUES (5, 8);
INSERT INTO post_likes (post_id, user_id) VALUES (6, 7);
INSERT INTO post_likes (post_id, user_id) VALUES (7, 6);
INSERT INTO post_likes (post_id, user_id) VALUES (8, 1);
INSERT INTO post_likes (post_id, user_id) VALUES (9, 5);
INSERT INTO post_likes (post_id, user_id) VALUES (10, 4);
INSERT INTO post_likes (post_id, user_id) VALUES (9, 3);
INSERT INTO post_likes (post_id, user_id) VALUES (7, 4);
INSERT INTO post_likes (post_id, user_id) VALUES (6, 5);
INSERT INTO post_likes (post_id, user_id) VALUES (4, 6);
INSERT INTO post_likes (post_id, user_id) VALUES (3, 7);
INSERT INTO post_likes (post_id, user_id) VALUES (2, 8);
INSERT INTO post_likes (post_id, user_id) VALUES (1, 9);
INSERT INTO post_likes (post_id, user_id) VALUES (8, 10);
INSERT INTO post_likes (post_id, user_id) VALUES (6, 10);
INSERT INTO post_likes (post_id, user_id) VALUES (7, 1);

INSERT INTO post_views (post_id, user_id, time_stamp) VALUES (1, 1, NOW());
INSERT INTO post_views (post_id, user_id, time_stamp) VALUES (2, 2, NOW());
INSERT INTO post_views (post_id, user_id, time_stamp) VALUES (3, 3, NOW());