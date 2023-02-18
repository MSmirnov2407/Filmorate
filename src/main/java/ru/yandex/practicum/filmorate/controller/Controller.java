package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Element;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

public abstract class Controller<T extends Element> {

}
