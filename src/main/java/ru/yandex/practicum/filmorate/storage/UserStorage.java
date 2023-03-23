package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface UserStorage extends Storage<User> {

    /**
     * Добавление друга пользователю
     *
     * @param user   - пользователь
     * @param friend - друг
     */
    void addFriend(User user, User friend);

    /**
     * Удаление друга у пользователя
     *
     * @param user   - пользователь
     * @param friend - друг
     */
    void deleteFriend(User user, User friend);

    /**
     * Вывод списка общих друзей
     *
     * @param user1 - пользователь 1
     * @param user2 - пользователь 1
     */
    List<User> getCommonFriends(User user1, User user2);

    /**
     * Получение из БД всех друзей пользователя.
     *
     * @param userId - id пользователя
     * @return Set из объектов User
     */
     Set<User> getFriendsByUserId(int userId);
}
