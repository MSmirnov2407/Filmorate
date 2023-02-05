package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends Controller<User> {
    @GetMapping
    public List<User> getFilms() {
        return new ArrayList(elements.values());
    }

    @PostMapping
    public User postNewFilm(@RequestBody User newUser) {
        int newId = create(newUser); //сложили нового юзера в мапу, получили его id
        log.info("Создан Пользователь. Id = {}, email = {}",newUser.getId(), newUser.getEmail());
        return elements.get(newId); //вернули юзер из общей мапы юзеров
    }

    @PutMapping
    public User postUser(@RequestBody User updatedUser) {
        Integer updatedUserId = updatedUser.getId(); //из переданного юзера взали Id
        if (!elements.containsKey(updatedUserId)) { //если не существует id - исключение
            log.warn("Ошибка обновления юзера: не найден id");
            throw new ValidationException("Ошибка обновления юзера: не найден id");
        }
        validate(updatedUser); //проверка корректности переданных данных перед обновлением
        elements.put(updatedUserId, updatedUser); //Обновили юзера в мапе
        log.info("Обновлен Пользователь. Id = {}, email = {}",updatedUser.getId(), updatedUser.getEmail());
        return elements.get(updatedUser.getId()); //вернули обновленного юзера из общей мапы юзеров
    }

    @Override
    public void validate(User user) {
        String validationError = ""; //текст ошибки валидации
        boolean badValidation = false; //флаг неуспешной валидации
        String login = user.getLogin(); //логин проверяемого пользователя
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) { //валидация email
            validationError = "Передан пользователь с некорректным email";
            log.warn(validationError);
            badValidation = true;
        }
        if (login == null || login.isBlank() || login.contains(" ")) { //валидация login
            validationError = "Некорректный логин";
            log.warn(validationError);
            badValidation = true;
        } else if (user.getName()==null || user.getName().isBlank()) { // если логин корректный, а имя пустое, то подставляем логин
            user.setName(login);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) { //валидация даты рождения
            validationError = "Дата рождения не может быть в будущем";
            log.warn(validationError);
            badValidation = true;
        }
        if (badValidation) { //в случае неуспешной валидации выбрасывается исключение
            throw new ValidationException(validationError);
        }
    }
}
