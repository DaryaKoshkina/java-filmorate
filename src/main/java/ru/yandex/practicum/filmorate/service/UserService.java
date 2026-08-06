package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired // Внедрение через конструктор
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        User user = userStorage.getById(userID);
        User friend = userStorage.getById(friendID);

        user.getFriends().add(friendID);
        friend.getFriends().add(userID);

        log.info("Пользователи {} и {} теперь друзья", userID, friendID);
    }

    public void removeFriend(int userID, int friendID) {
        User user = userStorage.getById(userID);
        User friend = userStorage.getById(friendID);

        user.getFriends().remove(friendID);
        friend.getFriends().remove(userID);

        log.info("Пользователи {} и {} больше не друзья", userID, friendID);
    }

    public List<User> getCommonFriends(int userID, int otherID) {

        User user = userStorage.getById(userID);
        User otherUser = userStorage.getById(otherID);

        Set<Integer> userFriends = user.getFriends();
        Set<Integer> otherFriends = otherUser.getFriends();

        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());

    }

    public List<User> getFriends(int userID) {
        User user = userStorage.getById(userID);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}
