package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос GET /users");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос POST /users с телом: {}", user);
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Получен запрос PUT /users с телом: {}", newUser);
        if (newUser.getId() == 0) {
            log.warn("Валидация не пройдена: не указан id пользователя для обновления");
            throw new ValidationException("id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            validate(newUser);
            users.put(newUser.getId(), newUser);
            log.info("Пользователь с ID успешно обновлен: {}", newUser.getId());
            return newUser;
        }
        log.warn("Валидация не пройдена: пользователь с данным id {} не найден", newUser.getId());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id = " + newUser.getId() + " не найден");
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, автоматически заменено на логин: {}", user.getLogin());
        }
    }
}
