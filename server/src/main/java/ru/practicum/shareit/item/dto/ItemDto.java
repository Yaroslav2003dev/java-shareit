package ru.practicum.shareit.item.dto;

import lombok.Builder;

/**
 * TODO Sprint add-controllers.
 */
@Builder
public record ItemDto(Long id, String name, String description, Boolean available, Long requestId) { }
