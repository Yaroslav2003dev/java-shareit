package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

public record BookingDto(Long id, LocalDateTime start, LocalDateTime end, ItemDto item, UserDto booker, Status status) {
}
