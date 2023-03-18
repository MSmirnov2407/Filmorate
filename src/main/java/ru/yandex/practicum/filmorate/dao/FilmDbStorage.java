package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;
    private final UserService userService;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaRatingService mpaRatingService, GenreService genreService, UserService userService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingService = mpaRatingService;
        this.genreService = genreService;
        this.userService = userService;
    }

    @Override
    public int create(Film element) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films").usingGeneratedKeyColumns("film_id");
        int createdId = simpleJdbcInsert.executeAndReturnKey(element.toMap()).intValue();

        /*создадим записи в таблице film-genre*/
        if (!element.getGenres().isEmpty()) {
            final String sqlInsertQuery = "insert into film_genre(film_id,genre_id) values(?,?)";
            element.getGenres().forEach(g -> {
                jdbcTemplate.update(sqlInsertQuery, createdId, g.getId());
            });
        }
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
        if (!element.getGenres().isEmpty()) {
            final String sqlInsertQuery = "insert into film_genre(film_id,genre_id) values(?,?)";
            element.getGenres().forEach(g -> jdbcTemplate.update(sqlInsertQuery, elementId, g.getId()));
        }
        return element.id;
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "delete from films where film_id = ?"; //записи из таблицы film_genre удалятся каскадно.
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Film> getAll() {
        String sqlQuery = "select * from films";
        List<Film> filmList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
        filmList.forEach(f -> f.setGenres(getGenresByFilm(f.getId())));//в каждый фильм добавлляем его жанры
        return filmList;
    }

    @Override
    public Film getById(int id) {
        /*получаем фильм из БД*/
        String sqlQuery = "select * from films where film_id = ?"; //запрос для получения фильма

        Film film = new Film();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            film.setId(userRows.getInt("film_id"));
            film.setName(userRows.getString("film_name"));
            film.setDescription(userRows.getString("description"));
            film.setReleaseDate(userRows.getDate("release_date").toLocalDate());
            film.setDuration(userRows.getLong("duration"));

            int mpaId = userRows.getInt("rating");
            MpaRating mpaRating = mpaRatingService.getById(mpaId);//по id получаем объект рейтинга из таблицы rating
            film.setMpa(mpaRating);
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
        Set<User> likedUsers = new HashSet<>();

        likedUsers = this.getLikedUsersByFilm(filmId);//взяли из БД все лайки для фильма
        likedUsers.add(user); //Добавили новый лайк

        /*обновляем данные в таблице likes*/
        /*удаляем старые данные в таблице likes*/
        String sqlQuery = "delete from likes where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
        /*добавляем новые данные в таблицу likes*/
        final String sqlInsertQuery = "insert into likes(film_id,user_id) values(?,?)";
        likedUsers.forEach(u -> jdbcTemplate.update(sqlInsertQuery, filmId, u.getId()));
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
     * Получение количества лайков у фильма
     *
     * @param filmId id фильма
     * @return количество лайков
     */
    public int getLikeAmountByFilm(int filmId) {
        int likeAmount = 0;
        log.info("FilmDbStorage нужный фильм = " + filmId);

        String sqlQuery = "select count(*) AS likesAmount from likes where film_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId); //отправка запроса и сохранение результатов в SqlRowSet

        if (userRows.next()) {
            likeAmount = userRows.getInt("likesAmount"); //извлекаем значение
        }
        log.info("FilmDbStorage getLikeAmount = " + likeAmount);
        return likeAmount;
    }

    /**
     * Получение из БД всех полтзователей, лайкнувших фильм.
     *
     * @param filmId - id фильма
     * @return Set из объектов User
     */
    private Set<User> getLikedUsersByFilm(int filmId) {
        Set<User> users = new HashSet<>();
        /*получаем пользователей, лайкнувших фильм из БД*/
        String sqlQuery = "select * from likes where film_id = ? ORDER BY user_id"; //запрос для получения пользователей по фильму
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId); //отправка запроса и сохранение результатов в SqlRowSet
        /*превращаем полученные записи в сет объектов типа User*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем id и складываем в сет
            int user_id = userRows.getInt("user_id"); //извлекаем значение
            users.add(userService.getById(user_id));
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
        String sqlQuery = "select * from film_genre where film_id = ? ORDER BY genre_id"; //запрос для получения жанров фильма
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId); //отправка запроса и сохранение результатов в SqlRowSet
        /*превращаем полученные жанры в сет объектов типа Genre*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем жанры и складываем в сет
            int genre_id = userRows.getInt("genre_id"); //извлекаем значение
            genres.add(genreService.getById(genre_id));
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

        int filmRating = resultSet.getInt("rating"); //берем из строки таблицы id рейтинга фильма
        MpaRating mpaRating = mpaRatingService.getById(filmRating);//по id получаем объект рейтинга из таблицы rating
        film.setMpa(mpaRating);

        return film;
    }
}
