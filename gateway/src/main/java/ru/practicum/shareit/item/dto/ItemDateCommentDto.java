package ru.practicum.shareit.item.dto;


import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public record ItemDateCommentDto(Long id, String name, String description, Boolean available, Long requestId,
                                 BookingDto nextBooking, BookingDto lastBooking, List<CommentDto> comments) {
}
