-- Dictionaries

CREATE TABLE IF NOT EXISTS mpas
(
  id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS genres
(
  id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS friend_statuses
(
  id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL UNIQUE
);

-- Model classes

CREATE TABLE IF NOT EXISTS users
(
  id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email    VARCHAR NOT NULL UNIQUE,
  login    VARCHAR NOT NULL UNIQUE,
  name     VARCHAR NOT NULL,
  birthday DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS films
(
  id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name         VARCHAR      NOT NULL,
  description  VARCHAR(200) NULL,
  release_date DATE         NOT NULL,
  duration     INT          NOT NULL,
  mpa_id       BIGINT       NULL REFERENCES mpas (id) ON DELETE SET NULL,
  likes        BIGINT       NOT NULL DEFAULT 0,
  marks_sum    BIGINT       NOT NULL DEFAULT 0,
  release_year INT GENERATED ALWAYS AS (EXTRACT(YEAR FROM release_date)),
  rating       NUMERIC(3, 1) GENERATED ALWAYS AS (CASE likes WHEN 0 THEN 0 ELSE ROUND((marks_sum + 0.0) / likes, 1) END)
);

CREATE TABLE IF NOT EXISTS directors
(
  id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews
(
  id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content     VARCHAR NOT NULL,
  is_positive BOOLEAN,
  user_id     BIGINT  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  film_id     BIGINT  NOT NULL REFERENCES films (id) ON DELETE CASCADE,
  useful      BIGINT  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS events
(
  id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id    BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  event_type VARCHAR   NOT NULL,
  operation  VARCHAR   NOT NULL,
  entity_id  BIGINT    NOT NULL,
  timestamp  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cross-reference tables

CREATE TABLE IF NOT EXISTS friends
(
  user_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  friend_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  status_id BIGINT NULL REFERENCES friend_statuses (id) ON DELETE SET NULL,
  CONSTRAINT friends_ux UNIQUE (user_id, friend_id),
  CONSTRAINT friends_no_reflection CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS film_genres
(
  film_id  BIGINT NOT NULL REFERENCES films (id) ON DELETE CASCADE,
  genre_id BIGINT NOT NULL REFERENCES genres (id) ON DELETE CASCADE,
  CONSTRAINT film_genres_ux UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_directors
(
  film_id     BIGINT NOT NULL REFERENCES films (id) ON DELETE CASCADE,
  director_id BIGINT NOT NULL REFERENCES directors (id) ON DELETE CASCADE,
  CONSTRAINT film_directors_ux UNIQUE (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS film_likes
(
  film_id BIGINT NOT NULL REFERENCES films (id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  mark    INT    NOT NULL DEFAULT 10 CHECK (mark BETWEEN 1 AND 10),
  CONSTRAINT film_likes_ux UNIQUE (film_id, user_id)
);

CREATE TRIGGER IF NOT EXISTS film_likes_trigger
AFTER INSERT, DELETE ON film_likes
FOR EACH ROW CALL 'ru.yandex.practicum.filmorate.storage.db.trigger.FilmLikeTrigger';

CREATE TABLE IF NOT EXISTS review_likes
(
  review_id BIGINT  NOT NULL REFERENCES reviews (id) ON DELETE CASCADE,
  user_id   BIGINT  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  is_like   BOOLEAN NOT NULL,
  CONSTRAINT reviews_likes_ux UNIQUE (review_id, user_id)
);

CREATE TRIGGER IF NOT EXISTS review_likes_trigger
AFTER INSERT, UPDATE, DELETE ON review_likes
FOR EACH ROW CALL 'ru.yandex.practicum.filmorate.storage.db.trigger.ReviewLikeTrigger';
