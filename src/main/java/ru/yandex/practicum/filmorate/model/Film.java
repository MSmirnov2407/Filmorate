package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Film extends Element {
    @NotBlank
    private String name; //название
    @Size(max = 200)
    private String description; //описание
    @NotNull
    private LocalDate releaseDate; //дата релиза
    @Min(1)
    private long duration; //продолжительность фильма [мин]
    private Set<User> likedUsers = new HashSet<>(); //пользователи, лайкнувшие фильм
}
