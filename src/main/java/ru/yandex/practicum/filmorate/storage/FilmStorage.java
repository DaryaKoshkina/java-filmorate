package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

interface FilmStorage {
    Collection<Film> getAll() {}
    Film create(Film film) {}
    Film update(Film film) {}
}
