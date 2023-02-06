package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Element;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller<T extends Element> {
    protected Map<Integer, T> elements = new HashMap<>();
    protected int id = 0;

    /**
     * Добавление элемента в общюю мапу элементов.
     * Выполняется валидация данных и генерация id
     *
     * @param newElement - добавлемый элемент
     * @return id добавленного в мапу элемента
     */
    public int create(T newElement) {
        validate(newElement); //проверка валидности данных
        id++; //сгенерировали новый id
        newElement.setId(id); //присвоили его новому элементу
        elements.put(id, newElement); //сложили новый элемент в мапу
        return id; //вернули новый id
    }

    /**
     * Метод валидации данных по заданным критериям.
     * В случае неудачной валидации выбрасывается исключение
     * @param element - проверяемый элемент
     */
    abstract void validate(T element);
}
