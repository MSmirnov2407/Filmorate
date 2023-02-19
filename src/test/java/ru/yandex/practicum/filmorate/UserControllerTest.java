package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {
    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    Storage<User> userStorage;
    UserService userService;
    UserController userController;

    @Autowired
    public UserControllerTest(Storage<User> userStorage, UserService userService, UserController userController) {
        this.userStorage = userStorage;
        this.userService = userService;
        this.userController = userController;
    }

    @Test
    public void goodUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2001, 11, 30));
        user.setLogin("Ultras");
        user.setEmail("google@yandex.kz");
        /*проверка валидации, реализованной через аннотации*/
        Set<ConstraintViolation<User>> violations = validator.validate(user); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size() == 0); //если сет пустой, значит проверяемый фильм прошел валидацию
        /*проверка дополнительной валидации, сделанной через метод validate*/
        assertDoesNotThrow(() -> userController.postNewUser(user));

    }

    @Test
    public void badEmailUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2001, 11, 30));
        user.setLogin("Ultras");
        user.setEmail("googleyand.ex@kz");
        Set<ConstraintViolation<User>> violations = validator.validate(user); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size() == 1); //если сет не пустой, значит проверяемый user не прошел валидацию
    }

    @Test
    public void badLoginUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2001, 11, 30));
        user.setLogin("");
        user.setEmail("google@yandex.kz");
        Set<ConstraintViolation<User>> violations = validator.validate(user); //сет с элементами, не прошедшими валидацию
        assertTrue(violations.size() == 1); //если сет не пустой, значит проверяемый user не прошел валидацию
    }

    @Test
    public void badBirthdateUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(3001, 11, 30));
        user.setLogin("Ultras");
        user.setEmail("google@yandex.kz");
        Exception exception = assertThrows(ValidationException.class, () -> userController.postNewUser(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
