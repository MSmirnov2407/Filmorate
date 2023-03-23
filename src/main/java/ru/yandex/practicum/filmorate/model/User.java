package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.*;

@Data
public class User extends Element {
    @NotBlank
    private String login; //логин пользователя
    @Email
    @Pattern(regexp = ".+@.+\\..+")
    private String email; //электронная почта
    private String name; //имя для отображения
    @NotNull
    private LocalDate birthday; //дата рождения

    @JsonIgnore
    private Set<Integer> friends = new HashSet<>(); //друзья пользователя

    @JsonIgnore
    private Set<Film> likedFilms; //фильмы, которые лайкнул пользователь

    /**
     * Преобразование объекта User в HashMap
     * <название поля, поле>
     *
     * @return объект в виде мапы
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("login", login);
        values.put("email", email);
        values.put("name", name);
        values.put("birthday", birthday);

        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(login, user.login) && Objects.equals(email, user.email) && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, login);
    }
}