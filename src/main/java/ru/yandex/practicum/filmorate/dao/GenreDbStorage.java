package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(Genre element) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("genre")
                .usingGeneratedKeyColumns("genre_id");
        return simpleJdbcInsert.executeAndReturnKey(element.toMap()).intValue();
    }

    @Override
    public int update(Genre element) {
        int elementId = element.getId();
        String sqlQuery = "update genre set name = ? where genre_id = ?;";
        jdbcTemplate.update(sqlQuery,
                element.getName(),
                elementId);
        return elementId;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from genre where genre_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Genre> getAll() {
        String sqlQuery = "select * from genre";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre getById(int id) {
        String sqlQuery = "select * from genre where genre_id = ?";
        Genre genre = new Genre();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            genre.setId(userRows.getInt("genre_id"));
            genre.setName(userRows.getString("genre_name"));
        } else {
            throw new ElementNotFoundException("GenreDbStorage getById жанр не найден");
        }
        return genre;
    }

    /**
     * Метод для имплементации функционального интерфейса RowMapper, описывающий
     * превращение данных из ResultSet в объект типа Genre
     *
     * @param resultSet считанный из БД набор данных
     * @param rowNum    номер строки
     * @return объект User
     * @throws SQLException
     */
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("genre_id"));
        genre.setName(resultSet.getString("genre_name"));
        return genre;
    }
}
