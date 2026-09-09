package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;

    @Test
    void testCreateAndGetById() {
        Film film = Film.builder()
                .name("Матрица")
                .description("Красная или синяя таблетка")
                .releaseDate(LocalDate.of(1999, 3, 31))
                .duration(136)
                .mpa(Mpa.builder().id(1).build())
                .build();

        Film createdFilm = filmStorage.create(film);
        Film savedFilm = filmStorage.getById(createdFilm.getId());

        assertNotNull(savedFilm, "Фильм не сохранился в БД");
        assertEquals(createdFilm.getId(), savedFilm.getId());
        assertEquals("Матрица", savedFilm.getName());
    }

    @Test
    void testUpdate() {
        Film film = filmStorage.create(Film.builder().name("Фильм").releaseDate(LocalDate.of(2000, 1, 1)).duration(100).build());
        film.setName("Новое Название");

        filmStorage.update(film);
        Film updatedFilm = filmStorage.getById(film.getId());

        assertEquals("Новое Название", updatedFilm.getName());
    }

    @Test
    void testGetAll() {
        filmStorage.create(Film.builder().name("Фильм").releaseDate(LocalDate.of(2000, 1, 1)).duration(100).build());

        Collection<Film> films = filmStorage.getAll();

        assertEquals(1, films.size());
    }

    @Test
    void testPopularFilms() {
        Film film = filmStorage.create(Film.builder().name("Популярный").releaseDate(LocalDate.of(2000, 1, 1)).duration(100).build());

        List<Film> popular = filmStorage.getPopular(1);

        assertEquals(1, popular.size());
        assertEquals(film.getId(), popular.get(0).getId());
    }
}
