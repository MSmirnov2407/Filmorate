package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

@Service
@Slf4j
public class GenreService extends AbstractService<Genre, GenreDbStorage> {

    @Autowired
    public GenreService(@Qualifier("genreDbStorage") GenreDbStorage storage) {
        this.storage = storage;
    }

    @Override
    void validate(Genre element) {

    }
}
