package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
    public User create(@RequestBody User user) {
        log.info("Получен запрос POST /users с телом: {}", user);
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с ID: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
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
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
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
        if (user.getLogin().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Валидация не пройдена: некорректный email {}", user.getEmail());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Валидация не пройдена: некорректный логин {}", user.getLogin());
            throw new ValidationException("Почта не может быть пустой и должна содержать @");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Валидация не пройдена: дата рождения в будущем {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя пустое, автоматически заменено на логин: {}", user.getLogin());
        }
    }
}
