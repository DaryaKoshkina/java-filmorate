package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("yandex_user");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    void testCreate_whenRequestBodyIsEmpty() {
        assertThrows(NullPointerException.class, () -> {
            userController.create(null);
        });
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testCreate_whenEmailIsEmpty() {
        User user = createValidUser();
        user.setEmail(""); // пустая почта

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testCreate_whenEmailDoesNotContainAtSign() {
        User user = createValidUser();
        user.setEmail("yandex.ru"); // нет знака @

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testCreate_whenLoginIsEmpty() {
        User user = createValidUser();
        user.setLogin(""); // пустой логин

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testCreate_whenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("yandex user"); // логин с пробелом

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testCreate_whenBirthdayIsTomorrow() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1)); // дата в будущем

        assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testCreate_whenBirthdayIsToday() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now()); // граничное значение: сегодня

        User created = userController.create(user);
        assertNotNull(created);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    void testCreate_whenNameIsBlank_usesLoginAsName() {
        User user = createValidUser();
        user.setName("   "); // пустое имя (из пробелов)

        User created = userController.create(user);
        assertNotNull(created);
        assertEquals(1, userController.getAll().size());
        assertEquals("yandex_user", created.getName()); // имя должно стать как логин
    }

    @Test
    void testGetAll_whenEmpty() {
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void testGetAll_whenUsersExist() {
        userController.create(createValidUser());
        assertEquals(1, userController.getAll().size());
    }

    @Test
    void testUpdate_whenIdIsZero() {
        User user = createValidUser();
        user.setId(0);

        assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
    }

    @Test
    void testUpdate_whenIdIsUndefined() {
        User user = createValidUser();
        user.setId(999);

        assertThrows(ValidationException.class, () -> {
            userController.update(user);
        });
    }
}
