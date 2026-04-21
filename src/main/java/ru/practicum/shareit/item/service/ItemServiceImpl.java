package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, NewItemRequest newItemRequest) {
        Item item = ItemMapper.toItem(newItemRequest);
        if (userId == null) {
            throw new InternalServerException("В заголовке не передан userId");
        } else {
            item.setOwner(userRepository.getById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId")));
        }
        Long id = itemRepository.save(item);
        System.out.println("Create " + ItemMapper.toItemDto(itemRepository.getById(id).get()));
        return ItemMapper.toItemDto(itemRepository.getById(id).orElseThrow(() -> new NotFoundException("Предмет не найден")));
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.toItemDto(itemRepository.getById(id).orElseThrow(() -> new NotFoundException("Предмет не найден")));
    }

    @Override
    public List<ItemDto> getAll(Long userId) {
        if (userId == null) {
            throw new InternalServerException("В заголовке не передан userId");
        } else {
            userRepository.getById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        }
        return itemRepository.getAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item item = itemRepository.getById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Эта вещь не принадлежит пользователю");
        }
        System.out.println("Item " + item);
        System.out.println("updateItemRequest " + updateItemRequest);
        Item itemUp = ItemMapper.updateItemFields(item, updateItemRequest);
        System.out.println("Update " + itemUp);
        itemRepository.update(itemId, itemUp);
        return ItemMapper.toItemDto(itemRepository.getById(itemId).orElseThrow(() -> new NotFoundException("Предмет после обновления не найден")));
    }
}
