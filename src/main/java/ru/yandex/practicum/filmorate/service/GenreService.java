package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.Storage;

@Service
@Slf4j
public class GenreService extends AbstractService<Genre> {

    @Autowired
    public GenreService(@Qualifier("genreDbStorage") Storage<Genre> storage) {
        this.storage = storage;
    }

    @Override
    void validate(Genre element) {
        String validationError = ""; //текст ошибки валидации
        boolean badValidation = false; //флаг неуспешной валидации
        String name = element.getName(); //имя проверяемого жанра

        if (name.length() > 255) { //валидация name
            validationError = "Некорректное называние жанра";
            log.warn(validationError);
            badValidation = true;
        }

        if (badValidation) { //в случае неуспешной валидации выбрасывается исключение
            throw new ValidationException(validationError);
        }
    }
}
