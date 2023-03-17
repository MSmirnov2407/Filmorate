package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        jdbcTemplate.update(sqlQuery
                , element.getLogin()
                , element.getEmail()
                , element.getName()
                , element.getBirthday().toString()
                , elementId);
        return elementId;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<User> getAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User getById(int id) {
        String sqlQuery = "select * from users where user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
    }


    /**
     * Получение из БД всех друзей пользователя.
     *
     * @param userId - id пользователя
     * @return Set из объектов User
     */
    public Set<User> getFriendsByUserId(int userId) {
        Set<User> friends = new HashSet<>();

        /*получаем друзей пользователя из БД*/
        String sqlQuery = "select u.user_id, f.friend_id " +
                "FROM users AS u " +
                "INNER JOIN friendship AS f ON u.user_id = f.user_id " +
                "WHERE u.user_id = ? AND status = 1;"; //запрос для получения друзей пользователя
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId); //отправка запроса и сохранение результатов в SqlRowSet

        /*превращаем полученные friend_id в сет объектов типа User*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем friend_id и складываем в сет
            int friendId = userRows.getInt("friend_id"); //извлекаем значение
            friends.add(this.getById(friendId));
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

        /*обновляем данные в таблице friendship*/
        /*удаляем старые данные в таблице friendship*/
        String sqlQuery = "delete from friendship where user_id = ? AND status =1;";
        jdbcTemplate.update(sqlQuery, userId);
        /*добавляем новые данные в таблицу friendship*/
        final String sqlInsertQuery = "insert into friendship(user_id,friend_id,status) values(?,?, 1)";
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
        String sqlQuery = "delete from friendship where user_id = ? AND friend_id = ? AND status =1;";
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

        String sqlQuery = "SELECT f1.friend_id " +
                "FROM friendship AS f1 " +
                "JOIN friendship AS f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ? AND f1.status = 1 AND f2.status = 1;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, user1Id, user2Id); //отправка запроса и сохранение результатов в SqlRowSet

        /*превращаем полученные friend_id в список объектов типа User*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем friend_id и складываем в сет
            int friendId = userRows.getInt("friend_id"); //извлекаем значение
            commonFriends.add(this.getById(friendId));
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
