package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    Long id = 0L;
    Map<Long, User> users = new HashMap<>();

    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void update(Long id, User user) {
        users.put(id, user);
    }

    public Long save(User user) {
        user.setId(id);
        users.put(id, user);
        return id++;
    }

    public User delete(Long id) {
        return users.remove(id);
    }

    public List<String> getEmailUsers() {
        return users.values().stream()
                .map(User::getEmail)
                .toList();
    }
}
