package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;
    private Validator validator;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Интерстеллар");
        film.setDescription("Фильм о космосе.");
        film.setReleaseDate(LocalDate.of(2014, 11, 6));
        film.setDuration(169);
        return film;
    }

    @Test
    void testCreate_whenNameIsBlank() {
        Film film = createValidFilm();
        film.setName(" ");

        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void testCreate_whenDescriptionIsExactly201Characters() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void testCreate_whenDurationIsMinusOne() {
        Film film = createValidFilm();
        film.setDuration(-1);

        assertFalse(validator.validate(film).isEmpty());
    }

    @Test
    void testCreate_whenReleaseDateIsOneDayBeforeCinemaBirthday() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> {
            filmController.create(film);
        });
    }

    @Test
    void testUpdate_whenIdZero() {
        Film film = createValidFilm();
        film.setId(0);

        assertThrows(ValidationException.class, () -> {
            filmController.update(film);
        });
    }

    @Test
    void testUpdate_whenIdUndefined() {
        Film film = createValidFilm();
        film.setId(999);

        assertThrows(ResponseStatusException.class, () -> {
            filmController.update(film);
        });
    }
}
