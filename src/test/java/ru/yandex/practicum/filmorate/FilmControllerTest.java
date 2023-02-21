package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.Storage;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FilmControllerTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    Storage<Film> filmStorage;
    Storage<User> userStorage;
    FilmService filmService;
    FilmController filmController;

    @Autowired
    public FilmControllerTest(Storage<Film> filmStorage, Storage<User> userStorage,
                              FilmService filmService, FilmController filmController) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmService = filmService;
        this.filmController = filmController;
    }

    @Test
    public void goodFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));
        /*проверка валидации, реализованной через аннотации*/
        Set<ConstraintViolation<Film>> violations = validator.validate(film); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size() == 0); //если сет пустой, значит проверяемый фильм прошел валидацию
        /*проверка дополнительной валидации, сделанной через метод validate*/
        assertDoesNotThrow(() -> filmController.postNewFilm(film));
    }

    @Test
    public void badNameFilmValidation() {

        Film film = new Film();
        film.setName("");
        film.setDescription("really good movie");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));

        Set<ConstraintViolation<Film>> violations = validator.validate(film); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size() == 1); //если сет не пустой, значит проверяемый фильм не прошел валидацию
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
        assertTrue(violations.size() == 1); //если сет не пустой, значит проверяемый фильм не прошел валидацию
    }

    @Test
    public void badReleaseDateFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie");
        film.setDuration(122);
        film.setReleaseDate(LocalDate.of(1001, 11, 30));
        Exception exception = assertThrows(ValidationException.class, () -> filmController.postNewFilm(film));
        assertEquals("Дата релиза раньше, чем 1895-12-28", exception.getMessage());
    }

    @Test
    public void badDurationFilmValidation() {
        Film film = new Film();
        film.setName("GoodFilm");
        film.setDescription("really good movie");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(2001, 11, 30));
        Set<ConstraintViolation<Film>> violations = validator.validate(film); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size() == 1); //если сет не пустой, значит проверяемый фильм не прошел валидацию
    }
}
