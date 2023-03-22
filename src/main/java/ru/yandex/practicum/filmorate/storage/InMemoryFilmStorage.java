package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Component
public class InMemoryFilmStorage extends AbstractElementStorage<Film> implements FilmStorage{
    @Override
    public void deleteLike(Film film, User user){
        film.getLikedUsers().remove(user);
    };

    @Override
    public void addLike(Film film, User user){
        film.getLikedUsers().add(user);
    }
}
