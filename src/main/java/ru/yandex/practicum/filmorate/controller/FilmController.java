package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController extends Controller<Film> {

    AbstractService<Film> filmService;

    @Autowired
    public FilmController(AbstractService<Film> filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getById(id);
    }

    @PostMapping
    public Film postNewFilm(@Valid @RequestBody Film newFilm) {
        return filmService.create(newFilm);
    }

    @PutMapping
    public Film putFilm(@Valid @RequestBody Film updatedFilm) {
        return filmService.update(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable(value = "id") int filmId, @PathVariable int userId) {
        ((FilmService)filmService).addLike(filmId,userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") int filmid, @PathVariable int userId) {
        ((FilmService)filmService).removeLike(filmid,userId);
    }
@GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value="count", defaultValue = "10") int count ){
        return ((FilmService)filmService).getPopularFilms(count);
    }
}
