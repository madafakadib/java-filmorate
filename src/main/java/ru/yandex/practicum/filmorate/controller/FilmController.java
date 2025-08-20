package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/film")
public class FilmController {

    private Map<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    private Long nextId() {
        return this.id++;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
//        if (film.getName() == null || film.getName().isBlank()) {
//            log.warn("Добавление не удалось. Ошибка в названии {}", film.getName());
//            throw new ValidationException("Название не может быть пустым");
//        }
//        if (film.getDescription().length() > 200) {
//            log.warn("Добавление не удалось. Ошибка в описании {}", film.getDescription());
//            throw new ValidationException("Максимальная длина описания - 200 символов");
//        }
//        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
//            log.warn("Добавление не удалось. Дата не может начинаться раньше 1895.12.28 : {}", film.getReleaseDate());
//            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года;");
//        }
//        if (film.getDuration().toSeconds() <= 0) {
//            log.warn("Добавление не удалось. Продолжительность фильма должна быть положительная");
//            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
//        }
        film.setId(nextId());
        film.setDuration(Duration.ofMinutes(100));
        films.put(film.getId(), film);
        //log.info("Добавление фильма {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
//        if (film.getId() == null) {
//            log.warn("Обновление не удалось: ID фильма не указан. Film: {}", film);
//            throw new ValidationException("Id должен быть указан");
//        }
//
//        if (films.containsKey(film.getId())) {
//            Film oldFilm = films.get(film.getId());
//
//            if (film.getName() == null || film.getName().isBlank()) {
//                log.warn("Обновление не удалось. Ошибка в названии {}", film.getName());
//                throw new ValidationException("Название не может быть пустым");
//            } else {
//                oldFilm.setName(film.getName());
//                log.info("Установленно новое название для фильма {}", oldFilm.getName());
//            }
//            if (film.getDescription().length() > 200) {
//                log.warn("Обновление не удалось. Ошибка в описании {}", oldFilm.getDescription());
//                throw new ValidationException("Максимальная длина описания - 200 символов");
//            } else {
//                oldFilm.setDescription(film.getDescription());
//                log.info("Установленно новое описание фильма {}", oldFilm.getDescription());
//            }
//            log.info("Обновленный фильм {}", oldFilm);
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            oldFilm.setDuration(Duration.ofMinutes(50));
            return oldFilm;
        }
        throw new ValidationException("Фильм с ID = " + film.getId() + " не найден");
    }
}
