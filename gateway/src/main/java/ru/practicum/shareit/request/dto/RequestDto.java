package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

public record RequestDto(Long id, String description, UserDto requestor, LocalDateTime created) {
}
