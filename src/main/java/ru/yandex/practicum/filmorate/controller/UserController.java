package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("показ всех пользователей");
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("начало добавлении пользователя");
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("добавление не удалось - email не соответствует требованиям");
            throw new ValidateException("Email не должен быть пустым и должен содержать символ '@'");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("добавление не удалось - логин не соответствует требованиям");
            throw new ValidateException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("добавление не удалось - дата рождения не соответствует требованиям");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("пользователь добавлен успешно");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.info("начало обновления данных пользователя");
        if (newUser.getId() == null) {
            log.warn("id не может быть равен нулю");
            throw new ValidateException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            log.info("пользователь с id {} найден", newUser.getId());
            User oldUser = users.get(newUser.getId());

            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                log.warn("обновление не удалось - email не соответствует требованиям");
                throw new ValidateException("Email не должен быть пустым и должен содержать символ '@'");
            }

            if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                log.warn("обновление не удалось - логин не соответствует требованиям");
                throw new ValidateException("Логин не может быть пустым и содержать пробелы");
            }

            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("обновление не удалось - дата рождения не соответствует требованиям");
                throw new ValidateException("Дата рождения не может быть в будущем");
            }

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());

            if (newUser.getName() == null || newUser.getName().isBlank()) {
                log.info("имя пользователя пусто потому будет присвоим вместо имени логин");
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            log.info("данные пользователя обновились успешно");
            return oldUser;
        }

        log.warn("пользователь с id {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");

    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
