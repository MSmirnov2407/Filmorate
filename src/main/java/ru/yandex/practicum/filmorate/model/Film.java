package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Film extends Element {
    public Film() {
        this.mpa = new MpaRating();
        this.genres = new HashSet<>();
    }

    @NotBlank
    private String name; //название
    @Size(max = 200)
    private String description; //описание
    @NotNull
    private LocalDate releaseDate; //дата релиза
    @Min(1)
    private long duration; //продолжительность фильма [мин]
    private MpaRating mpa; //рейтинг фильма
    private Set<Genre> genres; //жанры фильма
    @JsonIgnore
    private Set<User> likedUsers = new HashSet<>(); //пользователи, лайкнувшие фильм

    /**
     * Преобразование объекта Film в HashMap
     * <название поля, поле>
     *
     * @return объект в виде мапы
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating", mpa.getId());

        return values;
    }

    /**
     * Добавление жанра к фильму
     * @param genreId - id жанра
     * @param genreName - название жанра
     */
    public void addGenre(int genreId, String genreName){
        Genre genre = new Genre();
        genre.setId(genreId);
        genre.setName(genreName);
        this.genres.add(genre);
    }
}
