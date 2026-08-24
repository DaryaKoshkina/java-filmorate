package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {

        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
    }

    private User createValidUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("Питер");
        user.setBirthday(LocalDate.of(2000, 8, 10));
        return user;
    }

    @Test
    void testPositiveAddFriend() {
        User user1 = userService.create(createValidUser("spider-man@gmail.com", "spider"));
        User user2 = userService.create(createValidUser("2spider-man@gmail.com", "2spider"));

        userService.addFriend(user1.getId(), user2.getId());

        assertTrue(userService.getByID(user1.getId()).getFriends().contains(user2.getId()));
        assertTrue(userService.getByID(user2.getId()).getFriends().contains(user1.getId()));
    }

    @Test
    void testNegativeAddFriend_whenUserAddsHimself() {
        User user = userService.create(createValidUser("self@mail.com", "myself"));

        assertThrows(ValidationException.class, () -> {
            userService.addFriend(user.getId(), user.getId());
        });
    }

    @Test
    void testPositiveRemoveFriend() {
        User user1 = userService.create(createValidUser("spider-man@gmail.com", "spider"));
        User user2 = userService.create(createValidUser("2spider-man@gmail.com", "2spider"));

        userService.addFriend(user1.getId(), user2.getId());
        userService.removeFriend(user1.getId(), user2.getId());

        assertFalse(userService.getByID(user1.getId()).getFriends().contains(user2.getId()));
        assertFalse(userService.getByID(user2.getId()).getFriends().contains(user1.getId()));
    }

    @Test
    void testPositiveGetCommonFriends() {
        User user1 = userService.create(createValidUser("spider-man@gmail.com", "spider"));
        User user2 = userService.create(createValidUser("2spider-man@gmail.com", "2spider"));
        User common = userService.create(createValidUser("common@mail.com", "common"));

        userService.addFriend(user1.getId(), common.getId());
        userService.addFriend(user2.getId(), common.getId());

        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());

        assertEquals(1, commonFriends.size());
        assertEquals(common.getId(), commonFriends.getFirst().getId());
    }

    @Test
    void testGetByID_whenUserNotFound() {
        assertThrows(NotFoundException.class, () -> {
            userService.getByID(999);
        });
    }
}
