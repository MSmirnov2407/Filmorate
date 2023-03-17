package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {
    /*константа для хранения нижней допустимой временной границы даты релиза фильмов*/
    public static final LocalDate OLDEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Storage<User> userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") Storage<Film> storage,
                       @Qualifier("userDbStorage") Storage<User> userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
    }

    @Override
    void validate(Film film) {
        String validationError = ""; //текст ошибки валидации
        boolean badValidation = false; //флаг неуспешной валидации
        if (film.getReleaseDate().isBefore(OLDEST_RELEASE_DATE)) { //валидация даты релиза
            validationError = "Дата релиза раньше, чем " + OLDEST_RELEASE_DATE;
            log.warn(validationError);
            badValidation = true;
        }
        if (badValidation) { //в случае неуспешной валидации выбрасывается исключение
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
        ((FilmDbStorage) storage).addLike(film, user); //добавили запись в таблицу лайков
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
        ((FilmDbStorage) storage).deleteLike(film, user); //удалили пользователя из списка лайкнувших
    }

    /**
     * Получение наиболее популярных фильмов по кол-ву лайков
     *
     * @return список популярных фильмов
     */
    public List<Film> getPopularFilms(int count) {
        return storage.getAll().stream()
                .sorted((f1, f2) -> {
                    int cmp = 0;
                    if (((FilmDbStorage) storage).getLikeAmountByFilm(f1.getId()) >
                            ((FilmDbStorage) storage).getLikeAmountByFilm(f2.getId())) {
                        cmp = -1;
                    } else if (((FilmDbStorage) storage).getLikeAmountByFilm(f1.getId()) <
                            ((FilmDbStorage) storage).getLikeAmountByFilm(f2.getId())) {
                        cmp = 1;
                    }
                    return cmp;
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
