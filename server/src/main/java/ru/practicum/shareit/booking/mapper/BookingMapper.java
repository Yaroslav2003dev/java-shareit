package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.NewBookingRequest;
import ru.practicum.shareit.booking.UpdateBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(NewBookingRequest newBookingRequest, User booker, Item item) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setEnd(newBookingRequest.getEnd());
        booking.setStart(newBookingRequest.getStart());
        booking.setStatus(newBookingRequest.getStatus());
        booking.setItem(item);
        return booking;
    }

    public static List<BookingDto> bookingDtoList(List<Booking> bookingList) {
        return bookingList.stream().map(BookingMapper::toBookingDto).toList();
    }

    public static Booking updateBookingFields(Booking booking, UpdateBookingRequest updateBookingRequest) {
        if (updateBookingRequest.getStatus() != null) {
            booking.setStatus(updateBookingRequest.getStatus());
        }
        return booking;
    }
}
