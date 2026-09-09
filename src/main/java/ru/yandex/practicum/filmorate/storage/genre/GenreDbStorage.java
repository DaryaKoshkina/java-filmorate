package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbc;

    public Collection<Genre> getAll() {
        return jdbc.query("SELECT * FROM genres ORDER BY id", new BeanPropertyRowMapper<>(Genre.class));
    }

    public Genre getById(int id) {
        List<Genre> list = jdbc.query("SELECT * FROM genres WHERE id = ?", new BeanPropertyRowMapper<>(Genre.class), id);
        if (list.isEmpty()) throw new NotFoundException("Жанр не найден");
        return list.get(0);
    }
}
