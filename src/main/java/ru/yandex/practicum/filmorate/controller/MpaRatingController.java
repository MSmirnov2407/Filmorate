package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.AbstractService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaRatingController extends Controller<MpaRating> {
    private final AbstractService<MpaRating> mpaRatingService;

    @Autowired
    public MpaRatingController(AbstractService<MpaRating> mpaRatingService) {
        this.mpaRatingService = mpaRatingService;
    }

    @GetMapping
    public List<MpaRating> getMpa() {
        log.info("Получен список рейтинов фильмов");
        return mpaRatingService.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        MpaRating mpaRating = mpaRatingService.getById(id);
        log.info("Получен мпа-рейтинг по id={}", mpaRating.getId());
        return mpaRating;
    }

    @PostMapping
    public MpaRating postNewMpa(@Valid @RequestBody MpaRating newMpa) {
        MpaRating mpaRating = mpaRatingService.create(newMpa);
        log.info("Создан мпа-рейтинг. Id = {}, name = {}", mpaRating.getId(), mpaRating.getName());
        return mpaRating;
    }

    @DeleteMapping("/{id}")
    public void deleteMpa(@PathVariable int id) {
        mpaRatingService.delete(id);
        log.info("удален мпа-рейтинг. Id = {}", id);
    }
}
