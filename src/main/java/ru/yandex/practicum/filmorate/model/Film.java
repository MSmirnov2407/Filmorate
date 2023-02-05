package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class Film extends Element {
    private String name; //название
    private String description; //описание
    private LocalDate releaseDate; //дата релиза
    private long duration; //продолжительность фильма [мин]
}
