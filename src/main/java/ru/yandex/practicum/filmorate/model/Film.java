package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Slf4j
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Long duration;
}
