package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingService mpaRatingService, GenreService genreService, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(Film element) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films").usingGeneratedKeyColumns("film_id");
        int createdId = simpleJdbcInsert.executeAndReturnKey(element.toMap()).intValue();
        element.setId(createdId);
        /*создадим записи в таблице film-genre*/
        saveFilmGenres(element);
        return createdId;
    }

    @Override
    public int update(Film element) {
        String sqlQuery = "update films set film_name = ?, description = ?, release_date = ?, duration = ?, rating = ? where film_id = ?";
        /*заполняем параметры для запроса в БД. Складываем в него обновляемые данные*/
        int elementId = element.getId();
        jdbcTemplate.update(sqlQuery, element.getName(), element.getDescription(), element.getReleaseDate().toString(), element.getDuration(), element.getMpa().getId(), elementId);

        /*обновляем данные в таблице film_genre*/
        /*удаляем старые данные в таблице film_genre*/
        sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, elementId);
        /*добавляем новые данные в таблицу film_genre*/
        saveFilmGenres(element);
        return element.getId();
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from films where film_id = ?"; //записи из таблицы film_genre удалятся каскадно.
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select f.film_id, f.film_name, f.description, f.release_date, f.duration, f.rating, r.rating_id, " +
                "r.rating_name, COUNT(l.user_id) AS count " +
                "FROM films AS f " +
                "JOIN rating AS r ON f.rating = r.rating_id " +
                "LEFT JOIN likes AS l ON l.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY count DESC;";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        setGenresForFilmList(filmList); //устанавливаем для всех фильмов их жанры
        return filmList;
    }

    @Override
    public Film getById(int id) {
        /*получаем фильм из БД*/
        String sqlQuery = "select * " +
                "FROM films AS f " +
                "JOIN rating AS r ON f.rating = r.rating_id " +
                "WHERE film_id = ?"; //запрос для получения фильма

        Film film = new Film();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            film.setId(userRows.getInt("film_id"));
            film.setName(userRows.getString("film_name"));
            film.setDescription(userRows.getString("description"));
            film.setReleaseDate(userRows.getDate("release_date").toLocalDate());
            film.setDuration(userRows.getLong("duration"));

            int filmRating = userRows.getInt("rating");
            String mpaName = userRows.getString("rating_name");
            film.getMpa().setId(filmRating);
            film.getMpa().setName(mpaName);
        } else {
            throw new ElementNotFoundException("FilmDbStorage getById фильм не найден");
        }
        /*Складываем в фильм сет из жанров, полученных по его Id*/
        film.setGenres(getGenresByFilm(id));
        return film;
    }

    /**
     * Добавление лайка к фильму
     *
     * @param film фильм
     * @param user лайкнувший пользователь
     */
    public void addLike(Film film, User user) {
        int filmId = film.getId();
        Set<Integer> likedUsers = this.getLikedUsersByFilm(filmId);//взяли из БД все лайки для фильма
        likedUsers.add(user.getId()); //Добавили новый лайк

        /*добавляем новые данные в таблицу likes*/
        final String sqlInsertQuery = "merge into likes(film_id,user_id) KEY (film_id,user_id) values(?,?)";
        likedUsers.forEach(u -> jdbcTemplate.update(sqlInsertQuery, filmId, u));
    }

    /**
     * УДаление лайка у фильма
     *
     * @param film фильм
     * @param user лайкнувший пользователь
     */
    public void deleteLike(Film film, User user) {
        int filmId = film.getId();
        int userId = user.getId();

        /*удаляем Лайк из таблицы likes*/
        String sqlQuery = "delete from likes where film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    /**
     * Получение из БД всех пользователей, лайкнувших фильм.
     *
     * @param filmId - id фильма
     * @return Set из объектов User
     */
    private Set<Integer> getLikedUsersByFilm(int filmId) {
        Set<Integer> users = new HashSet<>();
        /*получаем пользователей, лайкнувших фильм из БД*/
        String sqlQuery = "select * from likes where film_id = ? ORDER BY user_id"; //запрос для получения пользователей по фильму
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId); //отправка запроса и сохранение результатов в SqlRowSet
        /*превращаем полученные записи в сет объектов типа User*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем id и складываем в сет
            users.add(userRows.getInt("user_id"));
        }
        return users;
    }

    /**
     * Получение из БД всех жанров, соответствующих фильму.
     *
     * @param filmId - id фильма
     * @return Set из объектов Genre
     */
    private Set<Genre> getGenresByFilm(int filmId) {
        Set<Genre> genres = new HashSet<>();
        /*получаем жанры фильма из БД*/
        String sqlQuery = "select * " +
                "from film_genre AS fg " +
                "JOIN genre AS g ON g.genre_id = fg.GENRE_ID " +
                "where film_id = ? " +
                "ORDER BY genre_id"; //запрос для получения жанров фильма

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId); //отправка запроса и сохранение результатов в SqlRowSet
        /*превращаем полученные жанры в сет объектов типа Genre*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем жанры и складываем в сет
            int genre_id = userRows.getInt("genre_id"); //извлекаем значение
            String genre_name = userRows.getString("genre_name"); //извлекаем значение
            genres.add(new Genre(genre_id, genre_name));
        }
        return genres;
    }

    /**
     * Метод для имплементации функционального интерфейса RowMapper, описывающий
     * превращение данных из ResultSet в объект типа Film
     *
     * @param resultSet считанный из БД набор данных
     * @param rowNum    номер строки
     * @return объект Film
     * @throws SQLException
     */
    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getInt("film_id"));
        film.setName(resultSet.getString("film_name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getLong("duration"));

        int filmRating = resultSet.getInt("rating");
        String mpaName = resultSet.getString("rating_name");
        film.getMpa().setId(filmRating);
        film.getMpa().setName(mpaName);

        return film;
    }

    /**
     * Сохранение в БД в таблицу film_genre жанры переданного фильма.
     *
     * @param film - фильм, жанры которого надо сохранить в БД
     */
    private void saveFilmGenres(Film film) {
        if (film == null || film.getGenres().isEmpty()) {
            return;
        }
        ArrayList<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate("insert into film_genre(film_id,genre_id) values(?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, film.getId());
                        preparedStatement.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });
    }

    /**
     * Добавление жанров к каждому фильму из списка фильмов (через таблицу film_genre).
     *
     * @param filmList - список фильмов
     */
    private void setGenresForFilmList(List<Film> filmList) {
        /*из списка фильмов получаем список их id. И создаем строку для условия запроса*/
        List<String> filmIdlist = filmList.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        if (filmIdlist.size() != 0) {
            filmIdlist.forEach(f -> stringBuilder.append(f + ","));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");

        String sql = "SELECT fg.film_id, fg.genre_id, g.genre_name " +
                "FROM FILM_GENRE AS fg " +
                "JOIN GENRE AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.film_id IN " + stringBuilder.toString() + ";";

        /*преобразуем ArrayList в мапу <id, film>*/
        Map<Integer, Film> filmMap = filmList.stream().collect(Collectors.toMap(Film::getId, film -> film));
        RowCallbackHandler rowCallbackHandler = new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int filmId = rs.getInt("film_id");
                int genreId = rs.getInt("genre_id");
                String genreName = rs.getString("genre_name");
                filmMap.get(filmId).addGenre(genreId, genreName); //добавление жанра к фильму
            }
        };
        jdbcTemplate.query(sql, rowCallbackHandler); //выполняем запрос и обработку результатов
    }
}
