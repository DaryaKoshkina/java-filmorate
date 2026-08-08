package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmServiceTest {
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
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

        assertThrows(ValidationException.class, () -> {
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

    @Test
    void testGetPopularFilms() {
        Film film1 = createValidFilm();
        film1.setName("Человек павук 1");

        Film film2 = createValidFilm();
        film2.setName("Человек павук 2");

        Film film3 = createValidFilm();
        film3.setName("Человек павук 3");

        film1 = filmService.create(film1);
        film2 = filmService.create(film2);
        film3 = filmService.create(film3);

        film2.getLikes().add(101);
        film2.getLikes().add(102);
        film1.getLikes().add(101);

        List<Film> popular = filmService.getPopularFilms(3);

        assertEquals(3, popular.size());
        assertEquals(film2.getId(), popular.get(0).getId());
        assertEquals(film1.getId(), popular.get(1).getId());
        assertEquals(film3.getId(), popular.get(2).getId());
    }
}
