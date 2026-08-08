package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Autowired // Внедрение через конструктор
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        Film film = filmStorage.getById(filmID);
        userStorage.getById(userID);
        film.getLikes().add(userID);
        log.info("Пользователь с id {} поставил лайк фильму {}", userID, filmID);
    }

    public void removeLike(int filmID, int userID) {
        Film film = filmStorage.getById(filmID);
        userStorage.getById(userID);
        film.getLikes().remove(userID);
        log.info("Пользователь с id {} удалил лайк у фильма {}", userID, filmID);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
