package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidateException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidateException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 1) {
            throw new ValidateException("Продолжительность фильма должна быть положительным числом");
        }

        int mpaid = film.getMpa().getId();
        Optional<Mpa> existingMpa = mpaStorage.findMpaById(mpaid);
        if (existingMpa.isEmpty()) {
            throw new NotFoundException("Рейтинг с id " + mpaid + " не найден");
        }

        for (Genre genre : film.getGenres()) {
            Optional<Genre> existingGenre = genreStorage.findGenreById(genre.getId());
            if (existingGenre.isEmpty()) {
                throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");
            }
        }
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (filmStorage.findFilmById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм не найден.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        return filmStorage.update(film);
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        genreStorage.findAllGenresByFilm(films);
        return films;
    }

    public Film findFilmById(int id) {
        Film film = filmStorage.findFilmById(id).orElseThrow(() -> new NotFoundException("Фильм не найден."));
        genreStorage.findAllGenresByFilm(List.of(film));
        return film;
    }

    public void addLike(int id, int userId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        likeStorage.addLike(id, userId);
    }

    public void removeLike(int id, int userId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        likeStorage.removeLike(id, userId);
    }

    public List<Film> findPopular(int count) {
        List<Film> films = filmStorage.findPopular(count);
        genreStorage.findAllGenresByFilm(films);
        return films;
    }

    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(int id) {
        return mpaStorage.findMpaById(id).orElseThrow(() -> new NotFoundException("Рейтинг MPA не найден."));
    }

    public List<Genre> findAllGenres() {
        return genreStorage.findAllGenres();
    }

    public Genre findGenreById(int id) {
        return genreStorage.findGenreById(id).orElseThrow(() -> new NotFoundException("Жанр не найден."));
    }
}