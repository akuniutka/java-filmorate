CREATE TABLE IF NOT EXISTS users
(
  user_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email     VARCHAR NOT NULL UNIQUE,
  login     VARCHAR NOT NULL UNIQUE,
  user_name VARCHAR NOT NULL,
  birthday  DATE    NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa
(
  mpa_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  mpa_name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films
(
  film_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  film_name    VARCHAR      NOT NULL,
  description  VARCHAR(200) NULL,
  release_date DATE         NOT NULL,
  duration     INT          NOT NULL,
  mpa_id       BIGINT       NULL REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS likes
(
  film_id BIGINT NOT NULL REFERENCES films (film_id),
  user_id BIGINT NOT NULL REFERENCES users (user_id),
  CONSTRAINT likes_ux UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friend_statuses
(
  status_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  status    VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS friends
(
  user_id   BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
  friend_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
  status_id BIGINT NULL REFERENCES friend_statuses (status_id),
  CONSTRAINT friends_ux UNIQUE (user_id, friend_id),
  CONSTRAINT friends_no_reflection CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS genres
(
  genre_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  genre_name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genres
(
  film_id  BIGINT NOT NULL REFERENCES films (film_id),
  genre_id BIGINT NOT NULL REFERENCES genres (genre_id),
  CONSTRAINT film_genres_ux UNIQUE (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
  review_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  content  VARCHAR NOT NULL,
  is_positive  BOOLEAN,
  user_id BIGINT NOT NULL REFERENCES users (user_id),
  film_id  BIGINT NOT NULL REFERENCES films (film_id),
  useful INT,
  review_date TIMESTAMP  NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews_likes_dislikes
(
  user_id BIGINT NOT NULL REFERENCES users (user_id),
  review_id  BIGINT NOT NULL REFERENCES reviews (review_id),
  is_like BOOLEAN,
  create_datetime TIMESTAMP NOT NULL

);

CREATE TABLE IF NOT EXISTS events
(
  event_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users (user_id),
  event_type  VARCHAR NOT NULL,
  operation  VARCHAR NOT NULL,
  entity_id BIGINT NOT NULL,
  time_stamp TIMESTAMP NOT NULL

);
