package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbc;
    private final FilmRowMapper filmRowMapper;

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id";
        List<Film> films = jdbc.query(sql, filmRowMapper);
        films.forEach(film -> film.setGenres(getGenresForFilm(film.getId())));
        return films;
    }

    @Override
    public Film getById(int id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id WHERE f.id = ?";
        List<Film> films = jdbc.query(sql, filmRowMapper, id);
        if (films.isEmpty()) throw new NotFoundException("Фильм с id = " + id + " не найден");
        Film film = films.get(0);
        film.setGenres(getGenresForFilm(film.getId()));
        return film;
    }

    public Film create(Film film) {
        validate(film);
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setObject(5, (film.getMpa() != null) ? film.getMpa().getId() : null);
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        saveGenres(film);
        return getById(film.getId());
    }

    @Override
    public Film update(Film newFilm) {
        validate(newFilm);
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
        Object mpaId = (newFilm.getMpa() != null) ? newFilm.getMpa().getId() : null;
        if (jdbc.update(sql, newFilm.getName(), newFilm.getDescription(), newFilm.getReleaseDate(), newFilm.getDuration(), mpaId, newFilm.getId()) == 0) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", newFilm.getId());
        saveGenres(newFilm);
        return getById(newFilm.getId());
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbc.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        if (jdbc.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId) == 0) {
            throw new NotFoundException("Лайк не найден");
        }
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f LEFT JOIN likes l ON f.id = l.film_id LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id GROUP BY f.id, m.name ORDER BY COUNT(l.user_id) DESC LIMIT ?";
        List<Film> popular = jdbc.query(sql, filmRowMapper, count);
        popular.forEach(film -> film.setGenres(getGenresForFilm(film.getId())));
        return popular;
    }

    private Set<Genre> getGenresForFilm(int filmId) {
        String sql = "SELECT g.id, g.name FROM genres g JOIN film_genres fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id";
        return new LinkedHashSet<>(jdbc.query(sql, new BeanPropertyRowMapper<>(Genre.class), filmId));
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null) return;
        Set<Genre> uniqueGenres = new LinkedHashSet<>(film.getGenres());
        for (Genre genre : uniqueGenres) {
            jdbc.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
        }
    }

    private void validate(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза некорректна");
        }
        if (film.getMpa() != null) {
            getMpaRatingCount(film.getMpa().getId());
        }
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> getGenreCount(genre.getId()));
        }
    }

    private void getMpaRatingCount(int id) {
        // Указываем int.class вместо Integer.class — теперь тут гарантированно число
        if (jdbc.queryForObject("SELECT COUNT(*) FROM mpa_ratings WHERE id = ?", int.class, id) == 0) {
            throw new NotFoundException("MPA не найден");
        }
    }

    private void getGenreCount(int id) {
        // Спринг сам вернет примитивный 0, и IDEA не будет ругаться
        if (jdbc.queryForObject("SELECT COUNT(*) FROM genres WHERE id = ?", int.class, id) == 0) {
            throw new NotFoundException("Жанр не найден");
        }
    }
}
