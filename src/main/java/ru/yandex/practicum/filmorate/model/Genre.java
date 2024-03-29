package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class Genre extends Element {

    public Genre(){

    }
    public Genre(int id, String name){
        this.id = id;
        this.name = name;
    }
    @Min(1)
    @Max(255)
    private String name; //название жанра

    /**
     * Преобразование объекта Rating в HashMap
     * <название поля, поле>
     *
     * @return объект в виде мапы
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("genre_name", name);
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre)) return false;
        if (!super.equals(o)) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
