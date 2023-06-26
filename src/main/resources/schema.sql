DROP TABLE IF EXISTS "film_genre";
DROP TABLE IF EXISTS "likes";
DROP TABLE IF EXISTS "friendship";

DROP TABLE IF EXISTS "films";
DROP TABLE IF EXISTS "users";

DROP TABLE IF EXISTS "genres";
DROP TABLE IF EXISTS "MPA_ratings";

-- Если нужно, чтобы данные сохранялись, то необходимо закомментировать секцию с DROP TABLE.

CREATE TABLE IF NOT EXISTS "genres" (
  "id" INTEGER PRIMARY KEY,
  "name" varchar
);

CREATE TABLE IF NOT EXISTS "MPA_ratings" (
  "id" INTEGER PRIMARY KEY,
  "name" varchar
);

CREATE TABLE IF NOT EXISTS "films" (
  "id" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(200),
  "release_date" timestamp,
  "duration" integer,
  "mpa_rating_id" integer
);

CREATE TABLE IF NOT EXISTS "film_genre" (
  "id" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE IF NOT EXISTS "users" (
  "id" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "email" varchar,
  "login" varchar NOT NULL,
  "name" varchar,
  "birthday" timestamp
);

CREATE TABLE IF NOT EXISTS "likes" (
  "id" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "id_user" integer,
  "id_film" integer
);

CREATE TABLE IF NOT EXISTS "friendship" (
  "id" INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  "id_user" integer,
  "id_friend" integer,
  "is_accepted" boolean DEFAULT false
);

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id") on delete cascade;

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");

ALTER TABLE "film_genre" ADD UNIQUE ("film_id", "genre_id");

ALTER TABLE "films" ADD FOREIGN KEY ("mpa_rating_id") REFERENCES "MPA_ratings" ("id");

ALTER TABLE "likes" ADD FOREIGN KEY ("id_user") REFERENCES "users" ("id") on delete cascade;

ALTER TABLE "likes" ADD FOREIGN KEY ("id_film") REFERENCES "films" ("id") on delete cascade;

ALTER TABLE "likes" ADD UNIQUE ("id_user", "id_film");

ALTER TABLE "friendship" ADD FOREIGN KEY ("id_user") REFERENCES "users" ("id") on delete cascade;

ALTER TABLE "friendship" ADD FOREIGN KEY ("id_friend") REFERENCES "users" ("id") on delete cascade;

ALTER TABLE "friendship" ADD UNIQUE ("id_user", "id_friend");

