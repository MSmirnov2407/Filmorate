package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/schema.sql", "/data_local.sql"}) //перед каждым тестом запускается создание исходной базы по заданным скриптам
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    /**
     * проверка метода getById.
     * Запрашиваем элемент из базы по id
     */
    @Test
    public void testFindUserById() {

        Optional<User> userOptional = Optional.of(userStorage.getById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    /**
     * проверка метода create.
     * Создаем элемент и запрашиваем его из базы
     */
    @Test
    public void testCreateUser() {
        User newUser = new User();
        newUser.setName("NewName");
        newUser.setEmail("NewEmail@mail.ru");
        newUser.setLogin("NewLogin");
        newUser.setBirthday(LocalDate.of(1990, 01, 01));

        int id = userStorage.create(newUser);
        Optional<User> userOptional = Optional.of(userStorage.getById(id));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 9))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "NewName"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "NewEmail@mail.ru"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "NewLogin"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 01, 01)));
    }

    /**
     * проверка метода update.
     * обновляем элемент по Id, запрашиваем его из базы и проверяем поля
     */
    @Test
    public void testUpdateUser() {
        User newUser = new User();
        newUser.setId(2);
        newUser.setName("222NewName");
        newUser.setEmail("222NewEmail@mail.ru");
        newUser.setLogin("22NewLogin");
        newUser.setBirthday(LocalDate.of(1990, 01, 01));

        int id = userStorage.update(newUser);
        Optional<User> userOptional = Optional.of(userStorage.getById(id));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 2))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("name", "222NewName"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "222NewEmail@mail.ru"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "22NewLogin"))
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1990, 01, 01)));
    }

    /**
     * Проверка метода delete.
     * Удаляем элемент по id, затем в списке всех элементов
     * проверяем отсутсвие элемента с указанным id
     */
    @Test
    public void testDeleteUser() {

        userStorage.delete(1);
        List<User> userList = userStorage.getAll();

        assertThat(userList.stream()
                .filter(us -> us.getId() == 1)
                .findFirst())
                .isNotPresent();
    }

    /**
     * Проверка метода getAll
     * Запрашиваем из базы список элементов и проверяем количество элементов.
     */
    @Test
    public void testGetAllUser() {

        List<User> userList = userStorage.getAll();

        assertThat(userList.size())
                .isEqualTo(8);
    }

    /**
     * проверка метода getById.
     * Запрашиваем элемент из базы по id
     */
    @Test
    public void testFindFilmById() {

        Optional<Film> userOptional = Optional.of(filmStorage.getById(1));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    /**
     * проверка метода create.
     * Создаем элемент и запрашиваем его из базы
     */
    @Test
    public void testCreateFilm() {
        Film newFilm = new Film();
        newFilm.setName("NewName");
        newFilm.setDescription("NewDescription");
        newFilm.setDuration(101);
        newFilm.setReleaseDate(LocalDate.of(1990, 01, 01));

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        int id = filmStorage.create(newFilm);
        Optional<Film> filmOptional = Optional.of(filmStorage.getById(id));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 6))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "NewName"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "NewDescription"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 101L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1990, 01, 01)));
    }

    /**
     * проверка метода update.
     * обновляем элемент по Id, запрашиваем его из базы и проверяем поля
     */
    @Test
    public void testUpdateFilm() {
        Film newFilm = new Film();
        newFilm.setId(2);
        newFilm.setName("NewName");
        newFilm.setDescription("NewDescription");
        newFilm.setDuration(101);
        newFilm.setReleaseDate(LocalDate.of(1990, 01, 01));

        MpaRating mpa = new MpaRating();
        mpa.setId(1);
        newFilm.setMpa(mpa);

        int id = filmStorage.update(newFilm);
        Optional<Film> filmOptional = Optional.of(filmStorage.getById(id));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 2))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("name", "NewName"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("description", "NewDescription"))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("duration", 101L))
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1990, 01, 01)));
    }

    /**
     * Проверка метода delete.
     * Удаляем элемент по id, затем в списке всех элементов
     * проверяем отсутсвие элемента с указанным id
     */
    @Test
    public void testDeleteFilm() {

        filmStorage.delete(1);
        List<Film> filmList = filmStorage.getAll();

        assertThat(filmList.stream()
                .filter(film -> film.getId() == 1)
                .findFirst())
                .isNotPresent();
    }

    /**
     * Проверка метода getAll
     * Запрашиваем из базы список элементов и проверяем количество элементов.
     */
    @Test
    public void testGetAllFilms() {

        List<Film> filmList = filmStorage.getAll();

        assertThat(filmList.size())
                .isEqualTo(5);
    }
}
