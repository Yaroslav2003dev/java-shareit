package ru.practicum.shareit.item.dto;

import lombok.Builder;
import ru.practicum.shareit.booking.BookingDto;

import java.util.List;

@Builder
public record ItemDateCommentDto(Long id, String name, String description, Boolean available, Long requestId,
                                 BookingDto nextBooking, BookingDto lastBooking, List<CommentDto> comments) {
}
