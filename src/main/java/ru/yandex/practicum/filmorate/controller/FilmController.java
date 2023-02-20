package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Получен список фильмов");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        Film film = filmService.getById(id);
        log.info("Получен фильм по id={}", film.getId());
        return film;
    }

    @PostMapping
    public Film postNewFilm(@Valid @RequestBody Film newFilm) {
        Film film = filmService.create(newFilm);
        log.info("Создан фильм. Id = {}, название = {}", film.getId(), film.getName());
        return filmService.getById(film.getId());
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film updatedFilm) {
        Film film = filmService.update(updatedFilm);
        log.info("Обновлен фильм. Id = {}, название = {}", film.getId(), film.getName());
        return filmService.getById(film.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.delete(id);
        log.info("Удален Фильм. Id = {}", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        filmService.addLike(filmId, userId);
        log.info("Фильму с id={} поставил лайк пользователь с id={}", filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        filmService.removeLike(filmId, userId);
        log.info("У фильма с id={} удалил лайк пользователь с id={}", filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int count) {
        log.info("Запрошен список популярных фильмов");
        return filmService.getPopularFilms(count);
    }
}
