package ru.practicum.shareit.request.dto;

import lombok.Builder;

@Builder
public record ItemOwnerDto(Long id, String name, Long ownerId) { }
