package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("mpaRatingDbStorage")
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(MpaRating element) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("rating")
                .usingGeneratedKeyColumns("rating_id");
        return simpleJdbcInsert.executeAndReturnKey(element.toMap()).intValue();
    }

    @Override
    public int update(MpaRating element) {
        int elementId = element.getId();
        String sqlQuery = "update rating set name = ? where rating_id = ?;";
        jdbcTemplate.update(sqlQuery
                , element.getName()
                , elementId);
        return elementId;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from rating where rating_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<MpaRating> getAll() {
        String sqlQuery = "select * from rating";
        return jdbcTemplate.query(sqlQuery, this::mapRowToMpaRating);
    }

    @Override
    public MpaRating getById(int id) {
        String sqlQuery = "select * from rating where rating_id = ?";
        MpaRating mpaRating = new MpaRating();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            mpaRating.setId(userRows.getInt("rating_id"));
            mpaRating.setName(userRows.getString("rating_name"));
        } else {
            throw new ElementNotFoundException("MpaDbStorage getById рейтинг не найден");
        }
        return mpaRating;
    }

    /**
     * Метод для имплементации функционального интерфейса RowMapper, описывающий
     * превращение данных из ResultSet в объект типа MpaRating
     *
     * @param resultSet считанный из БД набор данных
     * @param rowNum    номер строки
     * @return объект MpaRating
     * @throws SQLException
     */
    private MpaRating mapRowToMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(resultSet.getInt("rating_id"));
        mpaRating.setName(resultSet.getString("rating_name"));
        return mpaRating;
    }
}
