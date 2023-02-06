package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    FilmController filmController = new FilmController();

    @Test
    public void goodFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));
        filmController.validate(film);
    }

    @Test
    public void badNameFilmValidation() {

        Film film = new Film();
        film.setName("");
        film.setDescription("really good movie");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));

        Set<ConstraintViolation<Film>> violations = validator.validate(film); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size()==1); //если сет не пустой, значит проверяемый фильм не прошел валидацию
    }


    @Test
    public void badDescriptionFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie really good movie really good movie really good movie really good movie" +
                " really good movie really good movie really good movie really good movie really good movie really" +
                "Описание фильма длиннее 200 символовОписание фильма длиннее 200 символов");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));
        Set<ConstraintViolation<Film>> violations = validator.validate(film); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size()==1); //если сет не пустой, значит проверяемый фильм не прошел валидацию
    }

    @Test
    public void badReleaseDateFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(1001, 11, 30));
        Exception exception = assertThrows(ValidationException.class, () -> filmController.validate(film));
        assertEquals("Дата релиза раньше 28 дек.1895г.", exception.getMessage());
    }

    @Test
    public void badDurationFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));
        Set<ConstraintViolation<Film>> violations = validator.validate(film); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size()==1); //если сет не пустой, значит проверяемый фильм не прошел валидацию
    }
}
