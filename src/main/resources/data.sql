DELETE FROM film_likes_dislikes;
DELETE FROM film_genres;
DELETE FROM reviews;
DELETE FROM friends;
DELETE FROM friend_statuses;
DELETE FROM likes;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE MPA ALTER COLUMN mpa_id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;


CREATE TABLE initial_genres_data_temp
(
  genre_id BIGINT,
  genre_name VARCHAR
);

INSERT INTO initial_genres_data_temp (genre_id, genre_name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO genres (genre_id, genre_name)
SELECT genre_id,
       genre_name
FROM initial_genres_data_temp
WHERE genre_id NOT IN (
  SELECT genre_id
  FROM genres
  );

DROP TABLE initial_genres_data_temp;

CREATE TABLE initial_mpa_data_temp
(
  mpa_id   BIGINT,
  mpa_name VARCHAR
);

INSERT INTO initial_mpa_data_temp (mpa_id, mpa_name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO mpa (mpa_id, mpa_name)
SELECT mpa_id,
       mpa_name
FROM initial_mpa_data_temp
WHERE mpa_id NOT IN (
  SELECT mpa_id
  FROM mpa
  );

DROP TABLE initial_mpa_data_temp;



