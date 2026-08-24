package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testCreateAndGetById() {
        User user = User.builder()
                .email("user@mail.ru")
                .login("vanya")
                .name("Иван")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        User createdUser = userStorage.create(user);
        User savedUser = userStorage.getById(createdUser.getId());

        assertNotNull(savedUser, "Пользователь не сохранился в БД");
        assertEquals(createdUser.getId(), savedUser.getId());
        assertEquals("vanya", savedUser.getLogin());
    }

    @Test
    void testUpdate() {
        User user = userStorage.create(User.builder().email("u@mail.ru").login("l").birthday(LocalDate.of(2000, 1, 1)).build());
        user.setName("Новое Имя");

        userStorage.update(user);
        User updatedUser = userStorage.getById(user.getId());

        assertEquals("Новое Имя", updatedUser.getName());
    }

    @Test
    void testGetAll() {
        userStorage.create(User.builder().email("u1@mail.ru").login("l1").birthday(LocalDate.of(2000, 1, 1)).build());

        Collection<User> users = userStorage.getAll();

        assertEquals(1, users.size());
    }

    @Test
    void testFriendsLogic() {
        User user1 = userStorage.create(User.builder().email("u1@mail.ru").login("l1").birthday(LocalDate.of(2000, 1, 1)).build());
        User user2 = userStorage.create(User.builder().email("u2@mail.ru").login("l2").birthday(LocalDate.of(2000, 1, 1)).build());

        userStorage.addFriend(user1.getId(), user2.getId());
        List<User> friends = userStorage.getFriends(user1.getId());

        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.get(0).getId());
    }
}