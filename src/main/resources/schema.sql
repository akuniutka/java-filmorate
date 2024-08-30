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
  status_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  status    VARCHAR NOT NULL UNIQUE
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
  mpa_id       BIGINT       NULL REFERENCES mpas (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS directors
(
  id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews
(
  id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content     VARCHAR NOT NULL,
  is_positive BOOLEAN,
  user_id     BIGINT  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  film_id     BIGINT  NOT NULL REFERENCES films (id) ON DELETE CASCADE,
  useful      BIGINT     NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS events
(
  event_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id    BIGINT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  event_type VARCHAR   NOT NULL,
  operation  VARCHAR   NOT NULL,
  entity_id  BIGINT    NOT NULL,
  time_stamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cross-reference tables

CREATE TABLE IF NOT EXISTS friends
(
  user_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  friend_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  status_id BIGINT NULL REFERENCES friend_statuses (status_id) ON DELETE SET NULL,
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
  CONSTRAINT film_likes_ux UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS reviews_likes
(
  review_id BIGINT NOT NULL REFERENCES reviews (id) ON DELETE CASCADE,
  user_id   BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  is_like   BOOLEAN NOT NULL,
  CONSTRAINT reviews_likes_ux UNIQUE (review_id, user_id)
);

-- Supplementary views

CREATE VIEW IF NOT EXISTS film_ratings
AS
SELECT f.id AS film_id,
       COUNT(l.user_id) AS rating
FROM films AS f
LEFT JOIN film_likes AS l ON f.id = l.film_id
GROUP BY f.id;

CREATE VIEW IF NOT EXISTS review_ratings
AS
SELECT r.id AS review_id,
       SUM(
        CASE rl.is_like
          WHEN TRUE THEN 1
          WHEN FALSE THEN -1
          ELSE 0
        END
       ) AS rating
FROM reviews AS r
       LEFT JOIN reviews_likes AS rl ON r.id = rl.review_id
GROUP BY r.id;
