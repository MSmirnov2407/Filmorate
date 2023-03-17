
-- create
CREATE TABLE user_table (
  userId integer PRIMARY KEY,
  name TEXT NOT NULL
  );
  
-- create
CREATE TABLE friendship (
  user1 integer REFERENCES user_table (userId),
  user2 integer REFERENCES user_table (userId),
  status integer
  );
  
-- insert
INSERT INTO user_table (userId, name) VALUES (0001, 'Clark');
INSERT INTO user_table (userId, name) VALUES (0002, 'Dave');
INSERT INTO user_table (userId, name) VALUES (0003, 'Ava');
INSERT INTO user_table (userId, name) VALUES (0004, 'Kur');
INSERT INTO user_table (userId, name) VALUES (0005, 'Mar');
INSERT INTO user_table (userId, name) VALUES (0006, 'Uet');
INSERT INTO user_table (userId, name) VALUES (0007, 'Mar');
INSERT INTO user_table (userId, name) VALUES (0008, 'Uet');
INSERT INTO user_table (userId, name) VALUES (0009, 'Mar');
INSERT INTO user_table (userId, name) VALUES (0010, 'Uet');
INSERT INTO user_table (userId, name) VALUES (0088, 'Mar');
INSERT INTO user_table (userId, name) VALUES (0099, 'Uet');

-- insert
INSERT INTO friendship (user1, user2,status) VALUES (88,1, 1);
INSERT INTO friendship (user1, user2,status) VALUES (88,2, 1);
INSERT INTO friendship (user1, user2,status) VALUES (88,3, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0004,88, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0005,88, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0088,6, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0099,1, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0099,2, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0099,7, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0099,8, 1);
INSERT INTO friendship (user1, user2,status) VALUES (9,99, 1);
INSERT INTO friendship (user1, user2,status) VALUES (0010,99, 1);
INSERT INTO friendship (user1, user2,status) VALUES (006,99, 1);


-- fetch 
SELECT * FROM user_table;

SELECT * FROM friendship;

SELECT user1
FROM friendship
WHERE user2 = 88 AND Status = 1
			AND user1 IN (
					SELECT user2
					FROM friendship
					WHERE user1 = 99 AND Status = 1

					UNION

					SELECT user1
					FROM friendship
					WHERE user2 = 99 AND Status = 1
					)
UNION
SELECT user2
FROM friendship
WHERE user1= 88 AND Status = 1
			AND user2 IN (
					SELECT user2
					FROM friendship
					WHERE user1 = 99 AND Status = 1

					UNION

					SELECT user1
					FROM friendship
					WHERE user2 = 99 AND Status = 1
					)