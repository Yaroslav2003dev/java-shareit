package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.request.dto.ItemOwnerDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .email("yaroslav@mail.com")
            .name("Yar")
            .build();

    private RequestDto requestDto1 = RequestDto.builder()
            .id(1L)
            .description("Тест1")
            .requestor(userDto)
            .created(LocalDateTime.now())
            .build();

    private RequestDto requestDto2 = RequestDto.builder()
            .id(2L)
            .description("Тест2")
            .requestor(userDto)
            .created(LocalDateTime.now().plusHours(3))
            .build();


    private ItemOwnerDto itemOwnerDto1 = ItemOwnerDto.builder()
            .id(1L)
            .name("Item 1")
            .ownerId(userDto.id())
            .build();

    private ItemOwnerDto itemOwnerDto2 = ItemOwnerDto.builder()
            .id(2L)
            .name("Item 2")
            .ownerId(userDto.id())
            .build();

    private List<ItemOwnerDto> itemOwnerDtoList = List.of(itemOwnerDto1, itemOwnerDto2);

    private RequestItemDto requestItemDto = RequestItemDto.builder()
            .id(1L)
            .items(itemOwnerDtoList)
            .requestor(userDto)
            .description("Нужен item")
            .created(LocalDateTime.now())
            .build();


    @Test
    void testAddRequest() throws Exception {
        when(requestService.addRequest(any(), any()))
                .thenReturn(requestDto1);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(requestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(requestDto1.id()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto1.description())))
                .andExpect(jsonPath("$.requestor.id", is(requestDto1.requestor().id()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(requestDto1.requestor().name())));
    }

    @Test
    void testGetMyRequests() throws Exception {
        when(requestService.getMyRequests(any()))
                .thenReturn(List.of(requestItemDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestItemDto.id()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestItemDto.description())))
                .andExpect(jsonPath("$[0].requestor.id", is(requestItemDto.requestor().id()), Long.class))
                .andExpect(jsonPath("$[0].requestor.name", is(requestItemDto.requestor().name())))
                .andExpect(jsonPath("$[0].items[*].name", contains("Item 1", "Item 2")))
                .andExpect(jsonPath("$[0].items[*].id", contains(1, 2)));
    }

    @Test
    void testGetAllRequests() throws Exception {
        when(requestService.getAllRequests(any()))
                .thenReturn(List.of(requestDto1, requestDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(requestDto1.id()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDto1.description())))
                .andExpect(jsonPath("$[0].requestor.id", is(requestDto1.requestor().id()), Long.class))
                .andExpect(jsonPath("$[0].requestor.name", is(requestDto1.requestor().name())))
                .andExpect(jsonPath("$[1].id", is(requestDto2.id()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDto2.description())))
                .andExpect(jsonPath("$[1].requestor.id", is(requestDto2.requestor().id()), Long.class))
                .andExpect(jsonPath("$[1].requestor.name", is(requestDto2.requestor().name())));
    }

    @Test
    void testGetByRequestId() throws Exception {
        when(requestService.getByRequestId(any()))
                .thenReturn(requestItemDto);

        mvc.perform(get("/requests/" + requestItemDto.id())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestItemDto.id()), Long.class))
                .andExpect(jsonPath("$.description", is(requestItemDto.description())))
                .andExpect(jsonPath("$.requestor.id", is(requestItemDto.requestor().id()), Long.class))
                .andExpect(jsonPath("$.requestor.name", is(requestItemDto.requestor().name())))
                .andExpect(jsonPath("$.items[*].name", contains("Item 1", "Item 2")))
                .andExpect(jsonPath("$.items[*].id", contains(1, 2)));
    }

    @Test
    void testGetMyRequestsEmptyList() throws Exception {
        when(requestService.getMyRequests(any()))
                .thenReturn(List.of());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetAllRequestsEmptyList() throws Exception {
        when(requestService.getAllRequests(any()))
                .thenReturn(List.of());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetByRequestIdNotFound() throws Exception {
        when(requestService.getByRequestId(any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/999")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testAddRequestMinimal() throws Exception {
        RequestDto minimal = RequestDto.builder()
                .id(1L)
                .description("Only desc")
                .requestor(userDto)
                .created(LocalDateTime.now())
                .build();

        when(requestService.addRequest(any(), any()))
                .thenReturn(minimal);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(minimal))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Only desc")));
    }

}
