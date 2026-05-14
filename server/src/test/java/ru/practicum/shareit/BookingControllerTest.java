package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .available(TRUE)
            .name("Тетрис")
            .description("Приставка")
            .build();

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .email("yar@mail.com")
            .name("Yar")
            .build();

    private BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusDays(2))
            .item(itemDto)
            .booker(userDto)
            .status(Status.WAITING)
            .build();


    @Test
    void testAddBooking() throws Exception {
        when(bookingService.addBooking(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.item().id()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.item().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.booker().id()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.booker().name())))
                .andExpect(jsonPath("$.status").value(Status.WAITING.name()));
    }

    @Test
    void testEditBooking() throws Exception {
        when(bookingService.editBooking(any(), any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingDto.id())
                        .param("approved", "TRUE")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.item().id()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.item().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.booker().id()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.booker().name())))
                .andExpect(jsonPath("$.status").value(Status.WAITING.name()));
    }

    @Test
    void testBookingById() throws Exception {
        when(bookingService.getBookingById(any(), any()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + bookingDto.id())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.item().id()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingDto.item().name())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.booker().id()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingDto.booker().name())))
                .andExpect(jsonPath("$.status").value(Status.WAITING.name()));
    }

    @Test
    void testGetAllBookingsBooker() throws Exception {
        when(bookingService.getAllBookingsBooker(any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.item().id()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.item().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.booker().id()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.booker().name())))
                .andExpect(jsonPath("$[0].status").value(Status.WAITING.name()));
    }


    @Test
    void testGetAllBookingsOwner() throws Exception {
        when(bookingService.getAllBookingsOwner(any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookingDto.id()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.item().id()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookingDto.item().name())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.booker().id()), Long.class))
                .andExpect(jsonPath("$[0].booker.name", is(bookingDto.booker().name())))
                .andExpect(jsonPath("$[0].status").value(Status.WAITING.name()));
    }

    @Test
    void testGetAllBookingsBookerEmpty() throws Exception {
        when(bookingService.getAllBookingsBooker(any(), any()))
                .thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetAllBookingsOwnerEmpty() throws Exception {
        when(bookingService.getAllBookingsOwner(any(), any()))
                .thenReturn(List.of());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testEditBookingFalse() throws Exception {
        BookingDto rejected = BookingDto.builder()
                .id(1L)
                .start(bookingDto.start())
                .end(bookingDto.end())
                .item(bookingDto.item())
                .booker(bookingDto.booker())
                .status(Status.REJECTED)
                .build();

        when(bookingService.editBooking(any(), any(), any()))
                .thenReturn(rejected);

        mvc.perform(patch("/bookings/" + bookingDto.id())
                        .param("approved", "false")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.REJECTED.name()));
    }

    @Test
    void testGetAllBookingsBookerDifferentState() throws Exception {
        when(bookingService.getAllBookingsBooker(any(), any()))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .param("state", "WAITING")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(Status.WAITING.name()));
    }

    @Test
    void testGetBookingByIdNotFound() throws Exception {
        when(bookingService.getBookingById(any(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

}
