package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RequestItemDto(Long id, String description, UserDto requestor, LocalDateTime created,
                             List<ItemOwnerDto> items) {
}
