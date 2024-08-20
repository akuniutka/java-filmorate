DELETE FROM film_genres;
DELETE FROM friend_statuses;
DELETE FROM likes;
DELETE FROM friends;
DELETE FROM films;
DELETE FROM users;
DELETE FROM genres;
DELETE FROM mpa;
ALTER TABLE genres ALTER COLUMN genre_id RESTART WITH 1;
ALTER TABLE MPA ALTER COLUMN mpa_id RESTART WITH 1;
ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;


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


INSERT INTO users (user_name,login,email,birthday) VALUES
	 ('Hayden','Hayden','cras.interdum.nunc@protonmail.com','1985-03-08'),
	 ('Mechelle','Mechelle','cursus.vestibulum.mauris@icloud.org','1982-05-22'),
	 ('Christian','Christian','sociis.natoque@aol.com','2016-04-13'),
	 ('Whilemina','Whilemina','orci.adipiscing@yahoo.org','2008-01-18'),
	 ('Gisela','Gisela','turpis.nec@outlook.ca','1996-08-22'),
	 ('Austin','Austin','placerat.velit@yahoo.org','2003-05-17'),
	 ('Maxine','Maxine','feugiat.placerat@outlook.edu','1983-07-21'),
	 ('Jasper','Jasper','nisl@protonmail.com','2007-03-16'),
	 ('Cyrus','Cyrus','malesuada.fames@hotmail.com','1992-10-29'),
	 ('Heidi','Heidi','sodales@yahoo.couk','2012-01-05');
INSERT INTO users (user_name,login,email,birthday) VALUES
	 ('Hilda','Hilda','ipsum.porta@icloud.org','1983-10-12'),
	 ('Rafael','Rafael','tellus.suspendisse.sed@yahoo.couk','1994-02-27'),
	 ('Eliana','Eliana','urna.nunc.quis@google.couk','2003-12-19'),
	 ('Laurel','Laurel','luctus.sit@outlook.net','1995-07-31'),
	 ('Idola','Idola','fusce.aliquet@icloud.org','1988-11-10'),
	 ('Ifeoma','Ifeoma','ante.blandit@google.edu','2012-05-31'),
	 ('Cedric','Cedric','et.malesuada@protonmail.net','1984-03-05'),
	 ('Maggy','Maggy','iaculis.quis@protonmail.org','2005-10-10'),
	 ('Wylie','Wylie','nulla@outlook.edu','1992-01-03'),
	 ('Joshua','Joshua','eu.ultrices.sit@google.com','2018-05-15');


INSERT INTO FILM (film_name,description,release_date,duration, mpa_id) VALUES
	 ('По щучьему велению (2023)','Чудо-рыба помогает непутевому Емеле завоевать сердце царской дочери. Сказочный хит с Никитой Кологривым','2023-11-24',115,1),
	 ('Гарри Поттер и философский камень (2001)','Жизнь десятилетнего Гарри Поттера нельзя назвать сладкой: родители умерли, едва ему исполнился год, а от дяди и тёти, взявших сироту на воспитание, достаются лишь тычки да подзатыльники.','2001-03-04',NULL,1),
	 ('Властелин колец: Братство Кольца (2001)','Сказания о Средиземье — это хроника Великой войны за Кольцо, длившейся не одну тысячу лет. Тот, кто владел Кольцом, получал неограниченную власть, но был обязан служить злу.','2012-12-03',178,3);

INSERT INTO film_genres (film_id,genre_id) VALUES
	 (1,1),
	 (1,2),
	 (1,3),
	 (2,4),
	 (2,5),
	 (2,6);

INSERT INTO likes (film_id,user_id) VALUES
	 (1,1),
	 (1,2),
	 (1,3),
	 (3,5);
-- FRIENDSHIP
INSERT INTO friends (user_id,friend_id,status_id) VALUES
	 (1,2,1),
	 (5,6,2),
	 (15,11,1),
	 (13,6,2),
	 (7,2,1),
	 (5,16,2);


INSERT INTO friend_statuses (status) VALUES
	 (1, "Отправлен"),
	 (2, "Принял");