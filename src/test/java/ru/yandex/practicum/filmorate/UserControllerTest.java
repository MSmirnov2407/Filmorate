package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {

    UserController userController = new UserController();

    @Test
    public void goodUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2001, 11, 30));
        user.setLogin("Ultras");
        user.setEmail("google@yandex.kz");
        userController.validate(user);
    }

    @Test
    public void badEmailUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2001, 11, 30));
        user.setLogin("Ultras");
        user.setEmail("googleyandex.kz");
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Передан пользователь с некорректным email", exception.getMessage());
    }

    @Test
    public void badLoginUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2001, 11, 30));
        user.setLogin("Ult ras");
        user.setEmail("google@yandex.kz");
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Некорректный логин", exception.getMessage());
    }

    @Test
    public void badBirthdateUserValidation() {
        User user = new User();
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(3001, 11, 30));
        user.setLogin("Ultras");
        user.setEmail("google@yandex.kz");
        Exception exception = assertThrows(ValidationException.class, () -> userController.validate(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
