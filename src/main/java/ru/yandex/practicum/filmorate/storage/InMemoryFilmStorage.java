package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        log.info("Показ всех фильмов");
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        log.info("добавление фильма");
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("добавление не удалось - название фильма не соответствует требованиям");
            throw new ValidateException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("добавление не удалось - описание не соответствует требованиям");
            throw new ValidateException("Максимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("добавление не удалось - дата релиза не соответствует требованиям");
            throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 1) {
            log.warn("добавление не удалось - длительность не соответствует требованиям");
            throw new ValidateException("Продолжительность фильма должна быть положительным числом");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("фильм добавился успешно");
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("id не может быть равен нулю");
            throw new ValidateException("Id должен быть указан");
        }

        log.info("обновление данных фильма");
        if (films.containsKey(newFilm.getId())) {
            log.info("фильм с id {} найден", newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.warn("не удалось обновить - название фильма не соответствует требованиям");
                throw new ValidateException("Название фильма не может быть пустым");
            }

            if (newFilm.getDescription().length() > 200) {
                log.warn("не удалось обновить - описание фильма не соответствует требованиям");
                throw new ValidateException("Максимальная длина описания — 200 символов");
            }

            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("не удалось обновить - дата релиза фильма не соответствует требованиям");
                throw new ValidateException("Дата релиза — не раньше 28 декабря 1895 года");
            }

            if (newFilm.getDuration() < 1) {
                log.warn("не удалось обновить - длина фильма не соответствует требованиям");
                throw new ValidateException("Продолжительность фильма должна быть положительным числом");
            }

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setLikes(newFilm.getLikes());
            log.info("обновление прошло успешно");
            return oldFilm;
        }
        log.warn("фильм с id {} не найден", newFilm.getId());
        throw new NotFoundException("Пользователь с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Optional<Film> getFilmById(Long userId) {
        if (userId == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return Optional.ofNullable(films.get(userId));
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
