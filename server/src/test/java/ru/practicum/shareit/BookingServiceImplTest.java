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
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void testSave1Booking() {
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

    @Test
    void testSave2Booking() {
        BookingDto bookingDto = createBooking();
        assertThat(bookingDto.id(), notNullValue());
    }

    @Test
    void testGetBookingById() {
        UserDto owner = createUser("owner@mail", "Owner");
        UserDto booker = createUser("booker@mail", "Booker");

        ItemDto item = createItem(owner.id());

        BookingDto booking = createBooking(owner.id(), booker.id(), item.id());

        BookingDto found = bookingService.getBookingById(booker.id(), booking.id());

        assertThat(found.id(), equalTo(booking.id()));
        assertThat(found.item().id(), equalTo(item.id()));
        assertThat(found.booker().id(), equalTo(booker.id()));
    }

    @Test
    void testEditBookingApprove() {
        UserDto owner = createUser("owner2@mail", "Owner2");
        UserDto booker = createUser("booker2@mail", "Booker2");

        ItemDto item = createItem(owner.id());
        BookingDto booking = createBooking(owner.id(), booker.id(), item.id());

        BookingDto updated = bookingService.editBooking(owner.id(), booking.id(), true);

        assertThat(updated.status().name(), equalTo("APPROVED"));
    }

    @Test
    void testEditBookingReject() {
        UserDto owner = createUser("owner3@mail", "Owner3");
        UserDto booker = createUser("booker3@mail", "Booker3");

        ItemDto item = createItem(owner.id());
        BookingDto booking = createBooking(owner.id(), booker.id(), item.id());

        BookingDto updated = bookingService.editBooking(owner.id(), booking.id(), false);

        assertThat(updated.status().name(), equalTo("REJECTED"));
    }

    @Test
    void testGetAllBookingsBooker() {
        UserDto owner = createUser("owner4@mail", "Owner4");
        UserDto booker = createUser("booker4@mail", "Booker4");

        ItemDto item = createItem(owner.id());
        createBooking(owner.id(), booker.id(), item.id());

        List<BookingDto> bookings =
                bookingService.getAllBookingsBooker(booker.id(), State.ALL);

        assertThat(bookings.size(), equalTo(1));
    }

    @Test
    void testGetAllBookingsOwner() {
        UserDto owner = createUser("owner5@mail", "Owner5");
        UserDto booker = createUser("booker5@mail", "Booker5");

        ItemDto item = createItem(owner.id());
        createBooking(owner.id(), booker.id(), item.id());

        List<BookingDto> bookings =
                bookingService.getAllBookingsOwner(owner.id(), State.ALL);

        assertThat(bookings.size(), equalTo(1));
    }


    private BookingDto createBooking() {
        UserDto owner = createUser("o@mail", "O");
        UserDto booker = createUser("b@mail", "B");
        ItemDto item = createItem(owner.id());
        return createBooking(owner.id(), booker.id(), item.id());
    }

    private BookingDto createBooking(Long ownerId, Long bookerId, Long itemId) {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        NewBookingRequest request = NewBookingRequest.builder()
                .start(start)
                .end(end)
                .bookerId(bookerId)
                .itemId(itemId)
                .build();

        return bookingService.addBooking(bookerId, request);
    }

    private UserDto createUser(String email, String name) {
        return userService.create(
                NewUserRequest.builder()
                        .email(email)
                        .name(name)
                        .build()
        );
    }

    private ItemDto createItem(Long ownerId) {
        return itemService.create(ownerId,
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(TRUE)
                        .build()
        );
    }

    @Test
    void testGetBookingByIdForbidden() {
        UserDto owner = userService.create(new NewUserRequest("owner@mail", "Owner"));
        UserDto booker = userService.create(new NewUserRequest("booker@mail", "Booker"));
        UserDto stranger = userService.create(new NewUserRequest("stranger@mail", "Stranger"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        BookingDto booking = bookingService.addBooking(booker.id(),
                NewBookingRequest.builder()
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .itemId(item.id())
                        .build());

        assertThrows(RuntimeException.class,
                () -> bookingService.getBookingById(stranger.id(), booking.id()));
    }

    @Test
    void testEditBookingRejectByNonOwner() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));
        UserDto booker = userService.create(new NewUserRequest("b@mail", "B"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        BookingDto booking = bookingService.addBooking(booker.id(),
                NewBookingRequest.builder()
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .itemId(item.id())
                        .build());

        assertThrows(RuntimeException.class,
                () -> bookingService.editBooking(booker.id(), booking.id(), false));
    }

    @Test
    void testGetAllBookingsBookerStates() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));
        UserDto booker = userService.create(new NewUserRequest("b@mail", "B"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        bookingService.addBooking(booker.id(),
                NewBookingRequest.builder()
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(2))
                        .itemId(item.id())
                        .build());

        bookingService.getAllBookingsBooker(booker.id(), State.ALL);
        bookingService.getAllBookingsBooker(booker.id(), State.CURRENT);
        bookingService.getAllBookingsBooker(booker.id(), State.FUTURE);
    }
}
