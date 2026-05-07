package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, NewBookingRequest newBookingRequest) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " отсутствует"));
        Item item = itemRepository.findById(newBookingRequest.getItemId()).orElseThrow(() -> new NotFoundException("Предмет с id = " + newBookingRequest.getItemId() + " отсутствует"));
        if (!newBookingRequest.getStart().isBefore(newBookingRequest.getEnd())) {
            throw new ValidationException("Некорректные даты");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя бронировать свою вещь");
        }
        List<Booking> bookingList = bookingRepository.findBusyBookingsByItemId(newBookingRequest.getItemId(), newBookingRequest.getStart(), newBookingRequest.getEnd());
        if (!bookingList.isEmpty()) {
            throw new ValidationException("Вещь уже забронирована");
        }
        Booking booking = BookingMapper.toBooking(newBookingRequest, booker, item);
        booking = bookingRepository.save(booking);
        log.info("Оформлено бронирование с id = "+booking.getId());
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto editBooking(Long userId, Long bookingId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " отсутствует"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем вещи из бронирования с id = " + bookingId);
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        log.info("Отредактировано бронирование с id = "+bookingId+" пользователем "+userId);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " отсутствует"));
        if (!((userId.equals(booking.getItem().getOwner().getId())) || (userId.equals(booking.getBooker().getId())))) {
            throw new ValidationException("Пользователь с id = " + userId + " не является владельцем вещи или инициатором бронирования с id = " + bookingId);
        }
        log.info("Просмотр бронирования с id "+bookingId+" пользователем "+userId);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllBookingsBooker(Long userId, State state) {
        List<Booking> bookingList;
        switch (state) {
            case State.REJECTED:
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case State.WAITING:
                bookingList = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case State.FUTURE:
                bookingList = bookingRepository.findFutureBookingsByBooker(userId);
                break;
            case State.CURRENT:
                bookingList = bookingRepository.findCurrentBookingsByBooker(userId);
                break;
            case State.PAST:
                bookingList = bookingRepository.findPastBookingsByBooker(userId);
                break;
            default:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        }
        return BookingMapper.bookingDtoList(bookingList);
    }

    @Override
    public List<BookingDto> getAllBookingsOwner(Long userId, State state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> bookingList;
        switch (state) {
            case REJECTED:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;
            case WAITING:
                bookingList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByOwnerFutureStartDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository.findAllByOwnerCurrentStartDesc(userId);
                break;
            case PAST:
                bookingList = bookingRepository.findAllByOwnerPastStartDesc(userId);
                break;
            default:
                bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        }

        return BookingMapper.bookingDtoList(bookingList);
    }
}
