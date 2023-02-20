package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService extends AbstractService<User, UserDbStorage> {

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserDbStorage storage) {
        this.storage = storage;
    }

    @Override
    void validate(User user) {
        String validationError = ""; //текст ошибки валидации
        String login = user.getLogin(); //логин проверяемого пользователя

        if (login.contains(" ")) { //валидация login
            validationError = "Некорректный логин";
            log.warn(validationError);
            throw new ValidationException(validationError);

        } else if (user.getName() == null || user.getName().isBlank()) { // если логин корректный, а имя пустое, то подставляем логин
            user.setName(login);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) { //валидация даты рождения
            validationError = "Дата рождения не может быть в будущем";
            log.warn(validationError);
            throw new ValidationException(validationError);
        }
    }

    /**
     * Добавление друга пользователю
     *
     * @param userId   - id пользователя
     * @param friendId - id его нового друга пользователя
     */
    public void addFriend(int userId, int friendId) {
        Set<User> friends;
        User user = storage.getById(userId); //взяли из хранилищи пользователя и друга
        User friend = storage.getById(friendId); //взяли из хранилищи друга
        if (user == null || friend == null) {
            log.warn("не существующий id");
            throw new ElementNotFoundException("пользователь не найден");
        }
        storage.addFriend(user, friend); //добавили запись в таблицу дружбы
    }

    /**
     * Удаление пользователя из списка друзей
     *
     * @param userId   - id пользователя
     * @param friendId - id его  друга
     */
    public void removeFriend(int userId, int friendId) {
        User user = storage.getById(userId); //взяли из хранилищи пользователя и дрга
        User friend = storage.getById(friendId); //взяли из хранилищи друга
        if (user == null || friend == null) {
            log.warn("не существующий id");
            throw new ElementNotFoundException("пользователь не найден");
        }
        storage.deleteFriend(user, friend); //удалили запись из таблицы дружбы
    }

    /**
     * получение списка друзей по id пользователя
     *
     * @param id - Id пользователя, чей список друзей необходимо получить
     * @return - список друзей
     */
    public List<User> getFriends(int id) {
        User user = storage.getById(id);
        if (user == null) {
            log.warn("пользователь не найден");
            throw new ElementNotFoundException("пользователь не найден");
        }
        return new ArrayList(storage.getFriendsByUserId(id));
    }

    /**
     * Получение списка общих друзей двух пользователей
     *
     * @param idUser1 id Одного пользователя
     * @param idUser2 id Другого пользователя
     * @return - спиок общих друзей
     */
    public List<User> getCommonFriends(int idUser1, int idUser2) {
        User user1 = storage.getById(idUser1); //получили из хранилища одного пользователя
        User user2 = storage.getById(idUser2); //получили из хранилища другого пользователя
        if (user1 == null) {
            log.warn("не существующий id");
            throw new ElementNotFoundException("пользователь 1 не найден");
        }
        if (user2 == null) {
            log.warn("не существующий id");
            throw new ElementNotFoundException("пользователь 2 не найден");
        }
        return storage.getCommonFriends(user1, user2);
    }
}
