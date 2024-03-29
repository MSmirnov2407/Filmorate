package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Repository
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(User element) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        return simpleJdbcInsert.executeAndReturnKey(element.toMap()).intValue();
    }

    @Override
    public int update(User element) {
        int elementId = element.getId();
        String sqlQuery = "update users set login = ?, email = ?, name = ?, birthday = ? where user_id = ?;";
        jdbcTemplate.update(sqlQuery,
                element.getLogin(),
                element.getEmail(),
                element.getName(),
                element.getBirthday().toString(),
                elementId);
        return elementId;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "select * from users order by user_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getById(int id) {
        String sqlQuery = "select * from users where user_id = ?";

        User user = new User();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            user.setId(userRows.getInt("user_id"));
            user.setLogin(userRows.getString("login"));
            user.setEmail(userRows.getString("email"));
            user.setName(userRows.getString("name"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
        } else {
            throw new ElementNotFoundException("USerDbStorage Не найден пользователь с id = " + id);
        }
        return user;

    }

    /**
     * Получение из БД всех друзей пользователя.
     *
     * @param userId - id пользователя
     * @return Set из объектов User
     */
    public Set<User> getFriendsByUserId(int userId) {
        Set<User> friends = new TreeSet<>((u1, u2) -> {
            int cmp = 0;
            if (u1.getId() > u2.getId()) {
                cmp = -1;
            } else if (u1.getId() < u2.getId()) {
                cmp = 1;
            }
            return -1 * cmp;
        });

        /*получаем друзей пользователя из БД*/
        String sqlQuery = "select f.user_id, f.friend_id, u.login, u.email, u.name, u.birthday " +
                "FROM friendship AS f " +
                "INNER JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?;"; //запрос для получения друзей пользователя
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId); //отправка запроса и сохранение результатов в SqlRowSet

        /*превращаем полученные данные в сет объектов типа User*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем данные друга и складываем в сет новый объект User
            User friend = new User();
            friend.setId(userRows.getInt("friend_id"));
            friend.setLogin(userRows.getString("login"));
            friend.setName(userRows.getString("name"));
            friend.setEmail(userRows.getString("email"));
            friend.setBirthday(userRows.getDate("birthday").toLocalDate());
            friends.add(friend);
        }
        return friends;
    }

    /**
     * Добавление друга к пользователю
     *
     * @param user   - пользователь
     * @param friend - друг
     */
    public void addFriend(User user, User friend) {
        int userId = user.getId();
        Set<User> friends = getFriendsByUserId(userId); //получили сет друзей пользователя
        friends.add(friend); //добавили нового друга в сет
        
        /*добавляем новые данные в таблицу friendship*/
        final String sqlInsertQuery = "merge into friendship(user_id,friend_id) KEY (user_id,friend_id) values(?,?)";
        friends.forEach(f -> jdbcTemplate.update(sqlInsertQuery, userId, f.getId()));
    }

    /**
     * Удаление друга у пользователя
     *
     * @param user   - пользователь
     * @param friend - друг
     */
    public void deleteFriend(User user, User friend) {
        int userId = user.getId();
        int friendId = friend.getId();

        /*обновляем данные в таблице friendship*/
        /*удаляем старые данные в таблице friendship*/
        String sqlQuery = "delete from friendship where user_id = ? AND friend_id = ?;";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    /**
     * Вывод списка общих друзей
     *
     * @param user1 - пользователь 1
     * @param user2 - пользователь 1
     */
    public List<User> getCommonFriends(User user1, User user2) {
        int user1Id = user1.getId();
        int user2Id = user2.getId();
        List<User> commonFriends = new ArrayList<>();

        String sqlQuery = "SELECT f1.friend_id, u.login, u.email, u.name, u.birthday " +
                "FROM friendship AS f1 " +
                "JOIN friendship AS f2 ON f1.friend_id = f2.friend_id " +
                "JOIN users AS u ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user1Id, user2Id); //отправка запроса и сохранение результатов в SqlRowSet

        /*превращаем полученные данные в список объектов типа User*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем данные друга и складываем в сет новый объект User
            User friend = new User();
            friend.setId(userRows.getInt("friend_id"));
            friend.setLogin(userRows.getString("login"));
            friend.setName(userRows.getString("name"));
            friend.setEmail(userRows.getString("email"));
            friend.setBirthday(userRows.getDate("birthday").toLocalDate());
            commonFriends.add(friend);
        }
        return commonFriends;
    }

    /**
     * Метод для имплементации функционального интерфейса RowMapper, описывающий
     * превращение данных из ResultSet в объект типа User
     *
     * @param resultSet считанный из БД набор данных
     * @param rowNum    номер строки
     * @return объект User
     * @throws SQLException
     */
    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt("user_id"));
        user.setLogin(resultSet.getString("login"));
        user.setEmail(resultSet.getString("email"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());

        return user;
    }
}
