package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getByID(int filmID) {
        return filmStorage.getById(filmID);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void addLike(int filmID, int userID) {
        userStorage.getById(userID);
        filmStorage.addLike(filmID, userID);
        log.info("Пользователь с id {} поставил лайк фильму {}", userID, filmID);
    }

    public void removeLike(int filmID, int userID) {
        userStorage.getById(userID);
        filmStorage.removeLike(filmID, userID);
        log.info("Пользователь с id {} удалил лайк у фильма {}", userID, filmID);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрошен список ТОП-{} популярных фильмов", count);
        return filmStorage.getPopular(count);
    }

}
