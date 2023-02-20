package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен список пользователей");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        User user = userService.getById(id);
        log.info("Получен пользователь по id={}", user.getId());
        return user;
    }

    @PostMapping
    public User postNewUser(@Valid @RequestBody User newUser) {
        User user = userService.create(newUser);
        log.info("Создан Пользователь. Id = {}, email = {}", user.getId(), user.getEmail());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User updatedUser) {
        User newuser = userService.update(updatedUser);
        log.info("Обновлен Пользователь. Id = {}, email = {}", newuser.getId(), newuser.getEmail());
        return newuser;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.delete(id);
        log.info("удален Пользователь. Id = {}", id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void setFriendship(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        log.info("Пользователю с id={} добавлен в друзья пользователь с id={}", id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendship(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
        log.info("У пользователя с id={} удален из друзей пользователь с id={}", id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("У пользователя с id={} запрошен список друзей", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("У пользователей с id={}  и {} запрошен список общих друзей", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}