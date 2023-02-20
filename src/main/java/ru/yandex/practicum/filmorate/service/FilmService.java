package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService extends AbstractService<Film, FilmDbStorage> {
    /*константа для хранения нижней допустимой временной границы даты релиза фильмов*/
    public static final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmDbStorage storage,
                       @Qualifier("userDbStorage") UserDbStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    @Override
    void validate(Film film) {
        String validationError = ""; //текст ошибки валидации
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) { //валидация даты релиза
            validationError = "Дата релиза раньше, чем " + OLDEST_RELEASE_DATE;
            log.warn(validationError);
            throw new ValidationException(validationError);
        }
    }

    /**
     * Добавление лайка к фильму от пользователя
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    public void addLike(int filmId, int userId) {
        Film film = storage.getById(filmId); //взяли фильм и пользователя из хранилища по id
        User user = userStorage.getById(userId);
        if (film == null) {
            log.warn("фильм не найден");
            throw new ElementNotFoundException("фильм не найден");
        }
        if (user == null) {
            log.warn("пользователь не найден");
            throw new ElementNotFoundException("пользователь не найден");
        }
        storage.addLike(film, user); //добавили запись в таблицу лайков
    }

    /**
     * Удаление лайка у фильма от пользователя
     *
     * @param filmId id фильма
     * @param userId id пользователя
     */
    public void removeLike(int filmId, int userId) {
        Film film = storage.getById(filmId); //взяли фильм и пользователя из хранилища по id
        User user = userStorage.getById(userId);
        if (film == null) {
            log.warn("фильм не найден");
            throw new ElementNotFoundException("фильм не найден");
        }
        if (user == null) {
            log.warn("пользователь не найден");
            throw new ElementNotFoundException("пользователь не найден");
        }
        storage.deleteLike(film, user); //удалили пользователя из списка лайкнувших
    }

    /**
     * Получение наиболее популярных фильмов по кол-ву лайков
     *
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(int count) {
        return storage.getAll().stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
