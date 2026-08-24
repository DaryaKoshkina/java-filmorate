package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    private Film createValidFilm() {
        return Film.builder()
                .name("Человек павук")
                .description("Фильм о мальчике, который смог.")
                .releaseDate(LocalDate.of(2002, 5, 3))
                .duration(121)
                .build();
    }

    @Test
    void testCreate_whenReleaseDateIsOneDayBeforeCinemaBirthday() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> {
            filmService.create(film);
        });
    }

    @Test
    void testUpdate_whenIdZero() {
        Film film = createValidFilm();
        film.setId(0);

        assertThrows(NotFoundException.class, () -> {
            filmService.update(film);
        });
    }

    @Test
    void testUpdate_whenIdUndefined() {
        Film film = createValidFilm();
        film.setId(999);

        assertThrows(NotFoundException.class, () -> {
            filmService.update(film);
        });
    }
}