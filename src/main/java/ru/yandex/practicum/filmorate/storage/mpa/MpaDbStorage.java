package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbc;

    public Collection<Mpa> getAll() {
        return jdbc.query("SELECT * FROM mpa_ratings ORDER BY id", new BeanPropertyRowMapper<>(Mpa.class));
    }

    public Mpa getById(int id) {
        List<Mpa> list = jdbc.query("SELECT * FROM mpa_ratings WHERE id = ?", new BeanPropertyRowMapper<>(Mpa.class), id);
        if (list.isEmpty()) throw new NotFoundException("MPA не найден");
        return list.getFirst();
    }
}