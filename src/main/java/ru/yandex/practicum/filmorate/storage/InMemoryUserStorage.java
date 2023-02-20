package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage extends AbstractElementStorage<User> implements UserStorage{

}
