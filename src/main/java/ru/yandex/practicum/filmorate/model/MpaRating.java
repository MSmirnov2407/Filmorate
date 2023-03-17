package ru.yandex.practicum.filmorate.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class MpaRating extends Element {
    @Min(1)
    private String name; //название рейтинга фильма

    /**
     * Преобразование объекта MpaRating в HashMap
     * <название поля, поле>
     * @return объект в виде мапы
     */
    public Map<String, Object> toMap(){
        Map<String, Object> values = new HashMap<>();
        values.put("rating_name", name);
        return values;
    }
}
