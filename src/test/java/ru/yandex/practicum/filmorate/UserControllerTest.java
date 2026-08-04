package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
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
        user.setEmail("");

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void testCreate_whenEmailDoesNotContainAtSign() {
        User user = createValidUser();
        user.setEmail("yandex.ru");

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void testCreate_whenLoginIsEmpty() {
        User user = createValidUser();
        user.setLogin("");

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void testCreate_whenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("yandex user");

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void testCreate_whenBirthdayIsTomorrow() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertFalse(validator.validate(user).isEmpty());
    }

    @Test
    void testCreate_whenBirthdayIsToday() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now());

        assertTrue(validator.validate(user).isEmpty());
        User created = userController.create(user);
        assertNotNull(created);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    void testCreate_whenNameIsBlank_usesLoginAsName() {
        User user = createValidUser();
        user.setName(" ");

        User created = userController.create(user);
        assertNotNull(created);
        assertEquals(1, userController.getAll().size());
        assertEquals("yandex_user", created.getName());
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

        assertThrows(ResponseStatusException.class, () -> {
            userController.update(user);
        });
    }
}
