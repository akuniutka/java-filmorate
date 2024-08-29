CREATE TABLE initial_genres_data_temp
(
  id BIGINT,
  name VARCHAR
);

INSERT INTO initial_genres_data_temp (id, name)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');

INSERT INTO genres (id, name)
SELECT id,
       name
FROM initial_genres_data_temp
WHERE id NOT IN (
  SELECT id
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



