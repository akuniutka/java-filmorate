DELETE FROM events;
DELETE FROM reviews_likes_dislikes;
DELETE FROM film_genres;
DELETE FROM reviews;
DELETE FROM friends;
DELETE FROM friend_statuses;
DELETE FROM likes;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpas;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE MPAS ALTER COLUMN id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1;
ALTER TABLE events ALTER COLUMN event_id RESTART WITH 1;

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
  id   BIGINT,
  name VARCHAR
);

INSERT INTO initial_mpa_data_temp (id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

INSERT INTO mpas (id, name)
SELECT id,
       name
FROM initial_mpa_data_temp
WHERE id NOT IN (
  SELECT id
  FROM mpas
  );

DROP TABLE initial_mpa_data_temp;



