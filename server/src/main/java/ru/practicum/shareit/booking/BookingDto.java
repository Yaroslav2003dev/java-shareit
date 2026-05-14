package ru.practicum.shareit.booking;

import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Builder(toBuilder = true)
public record BookingDto(Long id, LocalDateTime start, LocalDateTime end, ItemDto item, UserDto booker, Status status) {
}
