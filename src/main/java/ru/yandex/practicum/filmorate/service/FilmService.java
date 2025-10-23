package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public User getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public void likeFilm(Long filmId, Long userId) {
        Collection<User> users = userStorage.getUsers();
        Film film = getFilmById(filmId);
        if (!users.contains(getUserById(userId))) {
            throw new NotFoundException("пользователь не найден");
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void unlikeFilm(Long filmId, Long userId) {
        Collection<User> users = userStorage.getUsers();
        Film film = getFilmById(filmId);
        if (!users.contains(getUserById(userId))) {
            throw new NotFoundException("пользователь не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}