DROP TABLE IF EXISTS films,users,rating,genre,friendship,film_genre,likes CASCADE;

CREATE TABLE IF NOT EXISTS rating (
rating_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
rating_name varchar(50)
);

CREATE TABLE IF NOT EXISTS films (
film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
film_name varchar(50),
description varchar(200),
release_date date,
duration long,
rating INTEGER REFERENCES rating (rating_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genre (
genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
genre_name varchar(50)
);

CREATE TABLE IF NOT EXISTS film_genre (
film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
genre_id INTEGER REFERENCES genre (genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
login varchar(50),
email varchar(50),
name varchar(50),
birthday date
);

CREATE TABLE IF NOT EXISTS friendship (
user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
friend_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
status INTEGER
);

CREATE TABLE IF NOT EXISTS likes (
film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE
);
