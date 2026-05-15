package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, NewItemRequest newItemRequest);

    ItemDateCommentDto getById(Long userId, Long id);

    List<ItemDateCommentDto> getAll(Long userId);

    List<ItemDto> search(String text);

    ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
