package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        validate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен с ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
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
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User getById(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id);
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
