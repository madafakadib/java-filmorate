package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Slf4j
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends = new HashSet<>();
}
