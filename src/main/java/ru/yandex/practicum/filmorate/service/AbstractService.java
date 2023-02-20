package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Element;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Slf4j
public abstract class AbstractService<T extends Element, S extends Storage> {
    protected S storage; //хранилище элементов

    /**
     * Сохранение элемента в хранилище, с предварительной валидацией и присвоением id
     *
     * @param element - элемент, который необходимо сохранить в хранилище
     * @return id добавленного в хранилище элемента
     */
    public T create(T element) {
        validate(element); //проверка валидности данных
        int newId = storage.create(element); //сохранили в хранилище
        return (T) storage.getById(newId);
    }

    /**
     * обновление элемента в хранилище
     *
     * @param updatedElement - обновленный элемент
     */
    public T update(T updatedElement) {
        Integer updatedElementId = updatedElement.getId(); //из переданного элемента взали Id

        if (storage.getById(updatedElementId) == null) { //если не существует - исключение
            log.warn("Ошибка обновления: не найден элемент");
            throw new ValidationException("Ошибка обновления данных: не найден элемент");
        }
        validate(updatedElement); //проверка корректности переданных данных перед обновление
        int id = storage.update(updatedElement); //обновили данные в хранилище

        return (T) storage.getById(id);
    }

    /**
     * Удаление элемента из хранилища
     *
     * @param id - Id удаляемого элемента
     */
    public void delete(int id) {
        storage.delete(id);
    }

    /**
     * Получение всех элементов из хранилища
     *
     * @return список всех элементов
     */
    public List<T> getAll() {
        return storage.getAll();
    }

    /**
     * Полечение одного элемента из хранилища по Id
     *
     * @param id - Id требуемого элемента
     * @return
     */
    public T getById(int id) {
        T element = (T) storage.getById(id);
        if (element == null) {
            throw new ElementNotFoundException("Элемент не найден");
        }
        return element;
    }

    /**
     * Метод валидации данных по заданным критериям.
     * В случае неудачной валидации выбрасывается исключение
     *
     * @param element - проверяемый элемент
     */
    abstract void validate(T element);
}
