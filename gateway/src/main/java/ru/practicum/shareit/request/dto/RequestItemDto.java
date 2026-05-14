package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public record RequestItemDto(Long id, String description, UserDto requestor, LocalDateTime created,
                             List<ItemOwnerDto> items) {
}
