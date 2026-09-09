package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserControllerTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private User createValidUser() {
        return User.builder()
                .email("spider-man@gmail.com")
                .login("spider")
                .name("Питер Паркер")
                .birthday(LocalDate.of(2001, 8, 10))
                .build();
    }

    @Test
    void testCreate_whenEmailIsBlank() {
        User user = createValidUser();
        user.setEmail(" ");
        assertFalse(validator.validate(user).isEmpty(), "Email не должен быть пустым");
    }

    @Test
    void testCreate_whenEmailIsInvalid() {
        User user = createValidUser();
        user.setEmail("spider-man");
        assertFalse(validator.validate(user).isEmpty(), "Email должен содержать символ @");
    }

    @Test
    void testCreate_whenLoginIsBlank() {
        User user = createValidUser();
        user.setLogin("");
        assertFalse(validator.validate(user).isEmpty(), "Логин не должен быть пустым");
    }

    @Test
    void testCreate_whenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("spider man");
        assertFalse(validator.validate(user).isEmpty(), "Логин не должен содержать пробелы");
    }

    @Test
    void testCreate_whenBirthdayInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));
        assertFalse(validator.validate(user).isEmpty(), "Дата рождения не может быть в будущем");
    }
}
