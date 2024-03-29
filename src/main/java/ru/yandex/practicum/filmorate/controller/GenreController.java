package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.AbstractService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {

    private final AbstractService<Genre, GenreDbStorage> genreService;

    @Autowired
    public GenreController(AbstractService<Genre,GenreDbStorage> genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenre() {
        log.info("Получен список жанров фильмов");
        return genreService.getAll();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        Genre genre = genreService.getById(id);
        log.info("Получен мпа-рейтинг по id={}", genre.getId());
        return genre;
    }

    @PostMapping
    public Genre postNewGenre(@Valid @RequestBody Genre newGenre) {
        Genre genre = genreService.create(newGenre);
        log.info("Создан Жанр. Id = {}, name = {}", genre.getId(), genre.getName());
        return genre;
    }

    @DeleteMapping("/{id}")
    public void deleteGenre(@PathVariable int id) {
        genreService.delete(id);
        log.info("удален жанр. Id = {}", id);
    }
}
