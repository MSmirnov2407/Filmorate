package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends Element {
    @Email
    @Pattern(regexp=".+@.+\\..+")
    private String email; //электронная почта
    @NotBlank
    private String login; //логин пользователя
    private String name; //имя для отображения
    @NotNull
    private LocalDate birthday; //дата рождения

    private Set<Integer> friends = new HashSet<>(); //друзья пользователя
}
