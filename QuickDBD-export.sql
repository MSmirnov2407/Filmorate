-- Exported from QuickDBD: https://www.quickdatabasediagrams.com/
-- Link to schema: https://app.quickdatabasediagrams.com/#/d/0CmnV8
-- NOTE! If you have used non-SQL datatypes in your design, you will have to change these here.


CREATE TABLE "Film" (
    "Film_Id" int   NOT NULL,
    "FIlm_name" varchar(50)   NOT NULL,
    "description" varchar(200)   NOT NULL,
    "ReleaseDate" Date   NOT NULL,
    "Duration" long   NOT NULL,
    "Rating" int   NOT NULL,
    CONSTRAINT "pk_Film" PRIMARY KEY (
        "Film_Id"
     )
);

CREATE TABLE "User" (
    "user_id" int   NOT NULL,
    "Lodin" varchar(50)   NOT NULL,
    "User_name" varchar(50)   NOT NULL,
    "Email" varchar(50)   NOT NULL,
    "Birthday" Date   NOT NULL,
    CONSTRAINT "pk_User" PRIMARY KEY (
        "user_id"
     )
);

CREATE TABLE "Genre" (
    "Genre_Id" int   NOT NULL,
    "Genre_name" varchar(50)   NOT NULL,
    CONSTRAINT "pk_Genre" PRIMARY KEY (
        "Genre_Id"
     )
);

CREATE TABLE "Film_genre" (
    "Film_Id" int   NOT NULL,
    "Genre_Id" int   NOT NULL,
    CONSTRAINT "pk_Film_genre" PRIMARY KEY (
        "Film_Id","Genre_Id"
     )
);

CREATE TABLE "Friendship" (
    "User_id" int   NOT NULL,
    "Friend_id" int   NOT NULL,
    "Status" int   NOT NULL,
    CONSTRAINT "pk_Friendship" PRIMARY KEY (
        "User_id","Friend_id"
     )
);

CREATE TABLE "Like" (
    "Film_Id" int   NOT NULL,
    "User_id" int   NOT NULL
);

CREATE TABLE "Rating" (
    "Rating_Id" int   NOT NULL,
    "Rating_name" varchar(50)   NOT NULL,
    CONSTRAINT "pk_Rating" PRIMARY KEY (
        "Rating_Id"
     )
);

ALTER TABLE "Film" ADD CONSTRAINT "fk_Film_Rating" FOREIGN KEY("Rating")
REFERENCES "Rating" ("Rating_Id");

ALTER TABLE "Film_genre" ADD CONSTRAINT "fk_Film_genre_Film_Id" FOREIGN KEY("Film_Id")
REFERENCES "Film" ("Film_Id");

ALTER TABLE "Film_genre" ADD CONSTRAINT "fk_Film_genre_Genre_Id" FOREIGN KEY("Genre_Id")
REFERENCES "Genre" ("Genre_Id");

ALTER TABLE "Friendship" ADD CONSTRAINT "fk_Friendship_User_id" FOREIGN KEY("User_id")
REFERENCES "User" ("user_id");

ALTER TABLE "Friendship" ADD CONSTRAINT "fk_Friendship_Friend_id" FOREIGN KEY("Friend_id")
REFERENCES "User" ("user_id");

ALTER TABLE "Like" ADD CONSTRAINT "fk_Like_Film_Id" FOREIGN KEY("Film_Id")
REFERENCES "Film" ("Film_Id");

ALTER TABLE "Like" ADD CONSTRAINT "fk_Like_User_id" FOREIGN KEY("User_id")
REFERENCES "User" ("user_id");

