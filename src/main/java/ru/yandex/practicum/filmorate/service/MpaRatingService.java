package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.Storage;

@Service
@Slf4j
public class MpaRatingService extends AbstractService<MpaRating> {

    @Autowired
    public MpaRatingService(@Qualifier("mpaRatingDbStorage") Storage<MpaRating> storage) {
        this.storage = storage;
    }

    @Override
    void validate(MpaRating element) {
        String validationError = ""; //текст ошибки валидации
        boolean badValidation = false; //флаг неуспешной валидации
        String name = element.getName(); //имя проверяемого рейтинга

        if (name.length()>255) { //валидация name
            validationError = "Некорректное называние рейтинга";
            log.warn(validationError);
            badValidation = true;
        }
        if (badValidation) { //в случае неуспешной валидации выбрасывается исключение
            throw new ValidationException(validationError);
        }
    }
}
