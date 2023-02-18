package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.AbstractService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends Controller<User> {

    AbstractService<User> userService;

    @Autowired
    public UserController(AbstractService<User> userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getById(id);
    }

    @PostMapping
    public User postNewUser(@Valid @RequestBody User newUser) {
        return userService.create(newUser);
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User updatedUser) {
        return userService.update(updatedUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void setFriendship(@PathVariable int id, @PathVariable int friendId) {
        System.out.println("putMapping добавление друзей" + id +" fr "+friendId);
        ((UserService) userService).addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendship(@PathVariable int id, @PathVariable int friendId) {
        ((UserService) userService).removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return ((UserService) userService).getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends (@PathVariable int id, @PathVariable int otherId){
        return  ((UserService) userService).getCommonFriends(id,otherId);
    }
}