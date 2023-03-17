package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
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
     * @return объект в виде мапы
     */
    public Map<String, Object> toMap(){
        Map<String, Object> values = new HashMap<>();
        values.put("login", login);
        values.put("email", email);
        values.put("name", name);
        values.put("birthday", birthday);

        return values;
    }
}