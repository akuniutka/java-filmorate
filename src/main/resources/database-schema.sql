DROP TABLE IF EXISTS film_genres;

DROP TABLE IF EXISTS genres;

DROP TABLE IF EXISTS friend_requests;

DROP TABLE IF EXISTS friends;

DROP TABLE IF EXISTS likes;

DROP TABLE IF EXISTS films;

DROP TABLE IF EXISTS ratings;

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
  id       BIGSERIAL PRIMARY KEY,
  email    VARCHAR NOT NULL,
  login    VARCHAR NOT NULL,
  name     VARCHAR NOT NULL,
  birthday DATE    NOT NULL
);

CREATE TABLE ratings
(
  id     BIGSERIAL PRIMARY KEY,
  rating VARCHAR
);

CREATE TABLE films
(
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR      NOT NULL,
  description  VARCHAR(200) NULL,
  release_date DATE         NOT NULL,
  duration     INT          NOT NULL,
  rating_id    BIGINT       NULL REFERENCES ratings (id)
);

CREATE TABLE likes
(
  user_id BIGINT NOT NULL REFERENCES users (id),
  film_id BIGINT NOT NULL REFERENCES films (id)
);

CREATE TABLE friends
(
  user_id   BIGINT NOT NULL REFERENCES users (id),
  friend_id BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE friend_requests
(
  sender_id   BIGINT NOT NULL REFERENCES users (id),
  receiver_id BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE genres
(
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE film_genres
(
  film_id  BIGINT NOT NULL REFERENCES films (id),
  genre_id BIGINT NOT NULL REFERENCES genres (id)
);

