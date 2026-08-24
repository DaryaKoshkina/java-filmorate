package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getByID(int userID) {
        return userStorage.getById(userID);
    }

    public void addFriend(int userID, int friendID) {

        if (userID == friendID) throw new ValidationException("Нельзя добавить себя");
        userStorage.addFriend(userID, friendID);

        log.info("Пользователи {} и {} теперь друзья", userID, friendID);
    }

    public void removeFriend(int userID, int friendID) {

        userStorage.removeFriend(userID, friendID);

        log.info("Пользователи {} и {} больше не друзья", userID, friendID);
    }

    public List<User> getCommonFriends(int userID, int otherID) {
        return userStorage.getCommonFriends(userID, otherID);
    }

    public List<User> getFriends(int userID) {
        return userStorage.getFriends(userID);
    }
}
