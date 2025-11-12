package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        validate(user);
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidateException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidateException("Email не должен быть пустым и должен содержать символ '@'");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidateException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        validate(user);
        if (userStorage.findUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return userStorage.update(user);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public void addFriend(int id, int friendId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        if (id < 0 || friendId < 0) {
            throw new NotFoundException("Пользователь не найден.");
        }
        friendStorage.addFriend(id, friendId);
    }

    public List<User> findAllFriends(int id) {
        if (userStorage.findUserById(id).isEmpty()) {
            throw new NotFoundException("Не найден");
        }
        return friendStorage.findAllFriends(id);
    }

    public List<User> findCommonFriends(int id, int otherId) {
        return friendStorage.findCommonFriends(id, otherId);
    }

    public void removeFriend(int id, int friendId) {
        if (userStorage.findUserById(id).isEmpty() || userStorage.findUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден.");
        }
        friendStorage.removeFriend(id, friendId);
    }

    private void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}