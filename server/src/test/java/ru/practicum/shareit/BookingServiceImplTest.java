package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.NewBookingRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;

    @Test
    void testSaveBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);
        NewUserRequest newUser1 = NewUserRequest.builder()
                .email("test1@mail")
                .name("Тест")
                .build();
        NewUserRequest newUser2 = NewUserRequest.builder()
                .email("test2@mail")
                .name("Тест")
                .build();
        UserDto userDto1 = userService.create(newUser1);
        UserDto userDto2 = userService.create(newUser2);

        NewItemRequest newItem = NewItemRequest.builder()
                .name("Тест")
                .description("Тест")
                .available(TRUE)
                .build();
        ItemDto itemDto = itemService.create(userDto1.id(), newItem);

        NewBookingRequest newBookingRequest = NewBookingRequest.builder()
                .start(start)
                .end(end)
                .bookerId(userDto2.id())
                .itemId(itemDto.id())
                .build();

        BookingDto bookingDto = bookingService.addBooking(userDto2.id(), newBookingRequest);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDto.id())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker().getId(), equalTo(bookingDto.booker().id()));
        assertThat(booking.getEnd(), equalTo(bookingDto.end()));
        assertThat(booking.getStart(), equalTo(bookingDto.start()));
        assertThat(booking.getItem().getId(), equalTo(bookingDto.item().id()));
        assertThat(booking.getStatus(), equalTo(bookingDto.status()));
    }
}
