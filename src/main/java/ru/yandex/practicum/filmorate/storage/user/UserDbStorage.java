package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private final UserRowMapper mapper;

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    @Override
    public User create(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().intValue());
        }

        log.info("Пользователь добавлен в БД с ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        if (jdbc.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()) == 0) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        return user;
    }

    @Override
    public User getById(int id) {
        List<User> users = jdbc.query("SELECT * FROM users WHERE id = ?", mapper, id);
        if (users.isEmpty()) throw new NotFoundException("Пользователь с id = " + id + " не найден");
        return users.getFirst();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);
        jdbc.update("INSERT INTO friends (user_id, friend_id, is_confirmed) VALUES (?, ?, ?)", userId, friendId, false);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        getById(userId);
        getById(friendId);
        jdbc.update("DELETE FROM friends WHERE user_id = ? AND friend_id = ?", userId, friendId);
    }


    @Override
    public List<User> getFriends(int userId) {
        getById(userId);
        return jdbc.query("SELECT u.* FROM users u JOIN friends f ON u.id = f.friend_id WHERE f.user_id = ?", mapper, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        String sql = "SELECT u.* FROM users u JOIN friends f1 ON u.id = f1.friend_id JOIN friends f2 ON u.id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbc.query(sql, mapper, userId, otherId);
    }
}
