CREATE TABLE IF NOT EXISTS person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    birth_date DATE
    );


INSERT INTO person (first_name, last_name, birth_date) VALUES
    ('Christopher', 'Nolan', '1970-07-30'),
    ('Quentin', 'Tarantino', '1963-03-27'),
    ('Martin', 'Scorsese', '1942-11-17'),
    ('Leonardo', 'DiCaprio', '1974-11-11'),
    ('Brad', 'Pitt', '1963-12-18'),
    ('Robert', 'Pattinson', '1986-05-13'),
    ('Margot', 'Robbie', '1990-07-02'),
    ('Matt', 'Damon', '1970-10-08'),
    ('Jodie', 'Foster', '1962-11-19'),
    ('Will', 'Smith', '1968-09-25');


CREATE TABLE movie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    director_id BIGINT,
    release_date DATE,
    genre VARCHAR(100),
    rating DOUBLE,
    duration_minutes INT,
    is_available_for_streaming BOOLEAN,
    FOREIGN KEY (director_id) REFERENCES person(id)
);

CREATE TABLE movie_actor (
    movie_id BIGINT,
    actor_id BIGINT,
    FOREIGN KEY (movie_id) REFERENCES movie(id),
    FOREIGN KEY (actor_id) REFERENCES person(id),
    PRIMARY KEY (movie_id, actor_id)
);

INSERT INTO movie (title, release_date, genre, rating, duration_minutes, is_available_for_streaming, director_id)
VALUES
    ('Inception', '2010-07-16', 'Sci-Fi', 8.8, 148, true, 1),
    ('Pulp Fiction', '1994-10-14', 'Crime', 8.9, 154, true, 2),
    ('The Wolf of Wall Street', '2013-12-25', 'Biography', 8.2, 180, true, 3),
    ('Interstellar', '2014-11-07', 'Sci-Fi', 8.6, 169, true, 1),
    ('Once Upon a Time in Hollywood', '2019-07-26', 'Comedy', 7.6, 161, true, 2),
    ('The Revenant', '2015-12-25', 'Adventure', 8.0, 156, true, 3),
    ('Tenet', '2020-08-26', 'Sci-Fi', 7.4, 150, true, 1),
    ('Mad Max: Fury Road', '2015-05-15', 'Action', 8.1, 120, true, 2);

INSERT INTO movie_actor (movie_id, actor_id) VALUES
    (1, 4),
    (1, 6),
    (2, 4),
    (2, 5),
    (3, 4),
    (4, 4),
    (4, 7),
    (5, 8),
    (5, 7),
    (6, 4),
    (6, 9),
    (7, 4),
    (7, 6),
    (8, 10);
