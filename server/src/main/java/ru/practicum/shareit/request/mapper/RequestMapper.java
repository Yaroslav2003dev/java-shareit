package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemOwnerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getDescription(),
                UserMapper.toUserDto(request.getRequestor()),
                LocalDateTime.ofInstant(request.getCreated(), ZoneOffset.UTC)
        );
    }

    public static Request toRequest(RequestDto requestDto, User requestor) {
        Request request = new Request();
        request.setDescription(requestDto.description());
        request.setRequestor(requestor);
        return request;
    }

    public static ItemOwnerDto toItemOwner(Item item) {
        return new ItemOwnerDto(item.getId(),
                item.getName(),
                item.getOwner().getId());
    }

    public static List<ItemOwnerDto> toItemOwnerDtoList(List<Item> items) {
        return items
                .stream()
                .map(RequestMapper::toItemOwner)
                .toList();
    }

    public static RequestItemDto toRequestItemDto(Request request, List<ItemOwnerDto> itemOwnerDtoList) {
        return new RequestItemDto(request.getId(), request.getDescription(), UserMapper.toUserDto(request.getRequestor()), LocalDateTime.ofInstant(request.getCreated(), ZoneOffset.UTC), itemOwnerDtoList);
    }
}
