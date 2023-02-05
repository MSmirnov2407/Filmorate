package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;


import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class User extends Element {
    private String email; //электронная почта
    private String login; //логин пользователя
    private String name; //имя для отображения
    private LocalDate birthday; //дата рождения
}
