package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Element;

import java.util.List;

public interface Storage <T extends Element> {
    /**
     * Сохранение элемента в хранилище, с предварительной валидацией и присвоением id
     * @param element - элемент, который необходимо сохранить в хранилище
     * @return id добавленного в хранилище элемента
     */
    int create (T element);

    /**
     * обновление элемента в хранилище
     * @param element - обновленный элемент
     */
    int update (T element);

    /**
     * Удаление элемента из хранилища
     * @param id - Id удаляемого элемента
     */
    void delete (int id);

    /**
     * Получение всех элементов из хранилища
     * @return список всех элементов
     */
    List<T> getAll ();

    /**
     * Получение одного элемента из хранилища по Id
     * @param id - Id требуемого элемента
     * @return
     */
    T getById(int id);
}
