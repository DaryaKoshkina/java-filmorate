package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void testNegativeAddFriend_whenUserAddsHimself() {
        assertThrows(ValidationException.class, () -> {
            userService.addFriend(1, 1);
        });
    }

    @Test
    void testGetByID_whenUserNotFound() {
        assertThrows(NotFoundException.class, () -> {
            userService.getByID(999);
        });
    }
}
