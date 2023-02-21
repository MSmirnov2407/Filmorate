package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    @Autowired
    public UserService(Storage<User> userStorage) {
        this.storage = userStorage;
    }

    @Override
    void validate(User user) {
        String validationError = ""; //текст ошибки валидации
        boolean badValidation = false; //флаг неуспешной валидации
        String login = user.getLogin(); //логин проверяемого пользователя

        if (login.contains(" ")) { //валидация login
            validationError = "Некорректный логин";
            log.warn(validationError);
            badValidation = true;
        } else if (user.getName() == null || user.getName().isBlank()) { // если логин корректный, а имя пустое, то подставляем логин
            user.setName(login);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) { //валидация даты рождения
            validationError = "Дата рождения не может быть в будущем";
            log.warn(validationError);
            badValidation = true;
        }
        if (badValidation) { //в случае неуспешной валидации выбрасывается исключение
            throw new ValidationException(validationError);
        }
    }

    /**
     * Добавление пользователей в списки друзей друг дргуа
     *
     * @param userId   - id пользователя
     * @param friendId - id его нового друга пользователя
     */
    public void addFriend(int userId, int friendId) {
        User user = storage.getById(userId); //взяли из хранилищи пользователя и дрга
        User friend = storage.getById(friendId); //взяли из хранилищи друга
        if (user == null || friend == null) {
            log.warn("не существующий id");
            throw new ElementNotFoundException("пользователь не найден");
        }
        user.getFriends().add(friendId); //добавили емейлы пользователей в списки друзей бруг друга
        friend.getFriends().add(userId);
        storage.update(user); //обновили пользователя и друга в хранилище
        storage.update(friend);
    }

    /**
     * Удаление пользователей из списков друзей друг дргуа
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
        user.getFriends().remove(friendId); //удалили емейлы пользователей из списков друзей бруг друга
        friend.getFriends().remove(userId);
        storage.update(user); //обновили пользователя и друга в хранилище
        storage.update(friend);
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
        return storage.getAll().stream()
                .filter(u -> user.getFriends().contains(u.getId()))
                .collect(Collectors.toList());
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

        return storage.getAll().stream()
                .filter(u -> u.getFriends().containsAll(Arrays.asList(idUser1, idUser2)))
                .collect(Collectors.toList());
    }
}
