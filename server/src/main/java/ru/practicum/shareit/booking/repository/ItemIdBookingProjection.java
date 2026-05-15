package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.Booking;


public interface ItemIdBookingProjection {
    Long getItemId();

    Booking getBooking();
}
