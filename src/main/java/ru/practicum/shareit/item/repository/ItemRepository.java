package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepository {
    Long id = 0L;
    Map<Long, Item> items = new HashMap<>();

    public Optional<Item> getById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Long save(Item item) {
        item.setId(id);
        items.put(id, item);
        return id++;
    }

    public void update(Long id, Item item) {
        items.put(id, item);
    }

    public List<Item> getAll() {
        return items.values().stream().toList();
    }

    public List<Item> search(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        } else {
            return items.values().stream()
                    .filter(item -> item.getAvailable() == true && (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .toList();
        }
    }
}
