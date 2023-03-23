package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.Storage;

@Service
@Slf4j
public class MpaRatingService extends AbstractService<MpaRating, MpaRatingDbStorage> {

    @Autowired
    public MpaRatingService(@Qualifier("mpaRatingDbStorage") MpaRatingDbStorage storage) {
        this.storage = storage;
    }

    @Override
    void validate(MpaRating element) {

    }
}
