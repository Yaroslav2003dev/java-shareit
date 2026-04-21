package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(NewItemRequest newItemRequest) {
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setAvailable(newItemRequest.getAvailable());
        item.setDescription(newItemRequest.getDescription());
        item.setRequest(newItemRequest.getRequest());
        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest updateItemRequest) {
        if (updateItemRequest.getName() != null && !updateItemRequest.getName().isBlank()) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.getDescription() != null && !updateItemRequest.getDescription().isBlank()) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.getAvailable() != null) {
            item.setAvailable(updateItemRequest.getAvailable());
        }
        return item;
    }

}
