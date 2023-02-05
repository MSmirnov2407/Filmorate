package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends Controller<Film> {
    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList(elements.values());
    }

    @PostMapping
    public Film postNewFilm(@RequestBody Film newFilm) {
        int newId = create(newFilm); //сложили новый фильм в мапу, получили его id
        log.info("Создан фильм. Id = {}, название = {}",newFilm.getId(), newFilm.getName());
        return elements.get(newId); //вернули фильм из общей мапы фильмов
    }

    @PutMapping
    public Film postFilm(@RequestBody Film updatedFilm) {
        Integer updatedFilmId = updatedFilm.getId(); //из переданного фильма взали Id
        if (!elements.containsKey(updatedFilmId)) { //если не существует id - исключение
            log.warn("Ошибка обновления фильма: не найден id");
            throw new ValidationException("Ошибка обновления фильма: не найден id");
        }
        validate(updatedFilm); //проверка корректности переданных данных перед обновлением
        elements.put(updatedFilmId, updatedFilm); //Обновили фильм в мапе
        log.info("Обновлен фильм. Id = {}, название = {}",updatedFilm.getId(), updatedFilm.getName());
        return elements.get(updatedFilm.getId()); //вернули обновленный фильм из общей мапы фильмов
    }

    @Override
    public void validate(Film film){
        String validationError = ""; //текст ошибки валидации
        boolean badValidation = false; //флаг неуспешной валидации
        if( film.getName().isBlank()){ //валидация названия
            validationError = "Передан фильм с пустым названием";
            log.warn(validationError);
            badValidation = true;
        }
        if (film.getDescription().length() > 200){ //валидация описания
            validationError = "Описание фильма длиннее 200 символов";
            log.warn(validationError);
            badValidation = true;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895,12,28))){ //валидация даты релиза
            validationError = "Дата релиза раньше 28 дек.1895г.";
            log.warn(validationError);
            badValidation = true;
        }
        if (film.getDuration() <= 0){ //валидация продолжительности
            validationError = "Продолжительность фильма <= 0";
            log.warn(validationError);
            badValidation = true;
        }
        if (badValidation){ //в случае неуспешной валидации выбрасывается исключение
            throw new ValidationException(validationError);
        }
    }
}
