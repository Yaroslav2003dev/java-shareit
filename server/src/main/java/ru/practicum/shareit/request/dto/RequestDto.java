package ru.practicum.shareit.request.dto;

import lombok.Builder;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Builder
public record RequestDto(Long id, String description, UserDto requestor, LocalDateTime created) {
}
