package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilmControllerTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Человек павук");
        film.setDescription("Фильм о мальчике, который смог.");
        film.setReleaseDate(LocalDate.of(2002, 5, 3));
        film.setDuration(121);
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
}
