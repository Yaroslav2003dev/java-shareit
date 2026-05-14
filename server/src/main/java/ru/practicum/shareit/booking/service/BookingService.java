package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.NewBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(Long userId, NewBookingRequest newBookingRequest);

    BookingDto editBooking(Long userId, Long bookingId, Boolean status);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsBooker(Long userId, State state);

    List<BookingDto> getAllBookingsOwner(Long userId, State state);
}
