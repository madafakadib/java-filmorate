package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    private Long nextId() {
        return this.id++;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() && !user.getEmail().contains("@")) {
            log.warn("Добавление не удалось. User: {}", user);
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() && user.getLogin().contains(" ")) {
            log.warn("Добавление не удалось. User: {}", user);
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Добавление не удалось. User: {}", user);
            user.setName(user.getLogin());
        }
        if (user.getDate().isAfter(LocalDate.now())) {
            log.warn("Добавление не удалось. User: {}", user);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(nextId());
        users.put(user.getId(), user);
        log.info("Добавление новой сущности. User {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Обновление не удалось: ID пользователя не указан. User: {}", user);
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            oldUser.setId(user.getId());
            if (user.getLogin() == null || user.getLogin().isBlank() && user.getLogin().contains(" ")) {
                log.warn("Обновление не удалось. В логине допущены ошибки {}", user);
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            } else {
                oldUser.setLogin(user.getLogin());
                log.info("Новый логин {} для пользователя с id {}", user.getLogin(), user.getId());
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
                log.info("Присвоение имени логина user.name: {}", user.getName());
            } else {
                oldUser.setName(user.getName());
                log.info("Обновлено имя для пользователя user: {}", user.getName());
            }
            if (user.getEmail() == null || user.getEmail().isBlank() && !user.getEmail().contains("@")) {
                log.warn("Обновление не удалось. В email допущены ошибки {}", user.getEmail());
                throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
            } else {
                oldUser.setEmail(user.getEmail());
                log.info("Установление новый email {}", user.getEmail());
            }
            log.info("Обновленная сущность user {}", user);
            return oldUser;
        }
        throw new ValidationException("Пользователь с ID = " + user.getId() + " не найден");
    }
}
