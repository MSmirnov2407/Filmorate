package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Element;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractElementStorage<T extends Element> implements Storage<T> {

    protected final Map<Integer, T> storagedData = new HashMap<>(); //мапа для хранения элементов по id
    protected int id; //id добавляемых в хранилище элементов

    @Override
    public int create(T element) {
        id++; //сгенерировали новый id
        element.setId(id); //присвоили его новому элементу
        storagedData.put(id, element); //сложили новый элемент в мапу
        return id; //вернули новый id
    }

    @Override
    public int update(T updatedElement) {
        Integer updatedElementId = updatedElement.getId(); //из переданного элемента взали Id
        storagedData.put(updatedElementId, updatedElement); //Обновили данные в мапе
        log.info("Обновлены данные. Тип: {}, Id = {}", updatedElement.getClass(), updatedElement.getId());
        return updatedElementId;
    }

    @Override
    public void delete(int id) {
        storagedData.remove(id);
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<T>(storagedData.values()); //возвращаем значения мапы в виде списка
    }

    @Override
    public T getById(int id) {
        return storagedData.get(id);
    }

}
