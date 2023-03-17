--заполняем рейтинг
INSERT INTO rating (rating_name) VALUES ('G');
INSERT INTO rating (rating_name) VALUES ('PG');
INSERT INTO rating (rating_name) VALUES ('PG-13');
INSERT INTO rating (rating_name) VALUES ('R');
INSERT INTO rating (rating_name) VALUES ('RR');

--заполняем фильмы
INSERT INTO films (film_name,description,duration,release_date,rating)
VALUES ('Titanic','Next time choose train',123,'1991-01-01',1); --genres: 1,2,3
INSERT INTO films (film_name,description,duration,release_date,rating)
VALUES ('Spider-Man','in love with MJ',124,'1992-02-02',1); --4,5,7,
INSERT INTO films (film_name,description,duration,release_date,rating)
VALUES ('The Shawshank Redemption','top250 #3',125,'1993-03-03',2); --8,9
INSERT INTO films (film_name,description,duration,release_date,rating)
VALUES ('Forrest Gump','run, Forrest, run!',126,'1994-04-04',3); --8,4,1,3
INSERT INTO films (film_name,description,duration,release_date,rating)
VALUES ('Matrix','shmatrix',127,'1995-05-05',4); --10,5

--заполняем жанры
INSERT INTO genre (genre_name) VALUES ('Melodrama'); --1
INSERT INTO genre (genre_name) VALUES ('Thriller');--2
INSERT INTO genre (genre_name) VALUES ('Historical');--3
INSERT INTO genre (genre_name) VALUES ('Comedy');--4
INSERT INTO genre (genre_name) VALUES ('Action');--5
INSERT INTO genre (genre_name) VALUES ('Western');--6
INSERT INTO genre (genre_name) VALUES ('Superhero');--7
INSERT INTO genre (genre_name) VALUES ('Drama');--8
INSERT INTO genre (genre_name) VALUES ('Crime film');--9
INSERT INTO genre (genre_name) VALUES ('Fantasy');--10

--заполняяем фильм_жанр
INSERT INTO film_genre (film_id,genre_id) VALUES (1,1); --Titanic
INSERT INTO film_genre (film_id,genre_id) VALUES (1,2);
INSERT INTO film_genre (film_id,genre_id) VALUES (1,3);
INSERT INTO film_genre (film_id,genre_id) VALUES (2,4) ;--Spider-Man
INSERT INTO film_genre (film_id,genre_id) VALUES (2,5);
INSERT INTO film_genre (film_id,genre_id) VALUES (2,7);
INSERT INTO film_genre (film_id,genre_id) VALUES (3,8); --Shawshank
INSERT INTO film_genre (film_id,genre_id) VALUES (3,9);
INSERT INTO film_genre (film_id,genre_id) VALUES (4,8); --Forrest Gump
INSERT INTO film_genre (film_id,genre_id) VALUES (4,4);
INSERT INTO film_genre (film_id,genre_id) VALUES (4,1);
INSERT INTO film_genre (film_id,genre_id) VALUES (4,3);
INSERT INTO film_genre (film_id,genre_id) VALUES (5,10); --Matrix
INSERT INTO film_genre (film_id,genre_id) VALUES (5,5);

--заполняем таблицу пользователей
INSERT INTO users (login,email,birthday,name) VALUES ('ruby','ruby@onrails.com','1993-02-23','Rname'); --1
INSERT INTO users (login,email,birthday,name) VALUES ('PHP','php@webpage.com','1994-01-01','Pname'); --2
INSERT INTO users (login,email,birthday,name) VALUES ('SQL','sql@database.com','1974-02-02','Sname'); --3
INSERT INTO users (login,email,birthday,name) VALUES ('JavaScript','js@goaway.com','1996-07-18','JSname'); --4
INSERT INTO users (login,email,birthday,name) VALUES ('Java','java@onelove.com','1996-01-21','Jname'); --5
INSERT INTO users (login,email,birthday,name) VALUES ('Assembler','asse@mbler.com','1947-03-03','Aname'); --6
INSERT INTO users (login,email,birthday,name) VALUES ('Cpp','cpp@ppc.com','1985-04-04','Cname'); --7
INSERT INTO users (login,email,birthday,name) VALUES ('Python','vape@hipster.com','1989-05-05','PYname'); --8

--заполняем таблицу дружбы
INSERT INTO friendship (user_id,friend_id,status) VALUES ('1','4',1);
INSERT INTO friendship (user_id,friend_id,status) VALUES ('1','5',1);
INSERT INTO friendship (user_id,friend_id,status) VALUES ('1','6',1);
INSERT INTO friendship (user_id,friend_id,status) VALUES ('1','7',0);

INSERT INTO friendship (user_id,friend_id,status) VALUES ('2','4',1);
INSERT INTO friendship (user_id,friend_id,status) VALUES ('2','5',1);
INSERT INTO friendship (user_id,friend_id,status) VALUES ('2','7',1);

INSERT INTO friendship (user_id,friend_id,status) VALUES ('3','1',1);
INSERT INTO friendship (user_id,friend_id,status) VALUES ('3','2',1);

INSERT INTO friendship (user_id,friend_id,status) VALUES ('4','5',0);

--заполняем таблицу лайков
INSERT INTO likes (film_id,user_id) VALUES (1,1);
INSERT INTO likes (film_id,user_id) VALUES (1,2);
INSERT INTO likes (film_id,user_id) VALUES (1,3);
INSERT INTO likes (film_id,user_id) VALUES (1,4);
INSERT INTO likes (film_id,user_id) VALUES (1,5);

INSERT INTO likes (film_id,user_id) VALUES (2,1);
INSERT INTO likes (film_id,user_id) VALUES (2,2);
INSERT INTO likes (film_id,user_id) VALUES (2,3);
INSERT INTO likes (film_id,user_id) VALUES (2,4);

INSERT INTO likes (film_id,user_id) VALUES (3,6);
INSERT INTO likes (film_id,user_id) VALUES (3,7);
INSERT INTO likes (film_id,user_id) VALUES (3,8);

INSERT INTO likes (film_id,user_id) VALUES (4,5);

INSERT INTO likes (film_id,user_id) VALUES (5,1);
INSERT INTO likes (film_id,user_id) VALUES (5,2);
INSERT INTO likes (film_id,user_id) VALUES (5,3);
INSERT INTO likes (film_id,user_id) VALUES (5,4);