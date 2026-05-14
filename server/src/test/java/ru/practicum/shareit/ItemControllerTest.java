package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

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


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;


    private NewItemRequest newItemRequest = NewItemRequest.builder()
            .available(TRUE)
            .name("Тетрис")
            .description("Приставка")
            .build();

    private UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
            .name("PS5")
            .description("Крутая приставка")
            .available(TRUE)
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .available(TRUE)
            .name("Тетрис")
            .description("Приставка")
            .build();

    private CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Замечательно")
            .authorName("Ярослав")
            .created(LocalDateTime.now())
            .build();

    private ItemDateCommentDto itemDateCommentDto = ItemDateCommentDto
            .builder()
            .id(1L)
            .comments(List.of(commentDto))
            .lastBooking(null)
            .nextBooking(null)
            .available(TRUE)
            .description("")
            .name("Машина")
            .build();

    @Test
    void testCreateItem() throws Exception {
        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(newItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.name())))
                .andExpect(jsonPath("$.description", is(itemDto.description())))
                .andExpect(jsonPath("$.available", is(itemDto.available())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.update(any(), any(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/" + itemDto.id())
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(updateItemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.name())))
                .andExpect(jsonPath("$.description", is(itemDto.description())))
                .andExpect(jsonPath("$.available", is(itemDto.available())));
    }

    @Test
    void testGetItem() throws Exception {
        when(itemService.getById(any(), any()))
                .thenReturn(itemDateCommentDto);

        mvc.perform(get("/items/" + itemDateCommentDto.id())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDateCommentDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDateCommentDto.name())))
                .andExpect(jsonPath("$.description", is(itemDateCommentDto.description())))
                .andExpect(jsonPath("$.available", is(itemDateCommentDto.available())));
    }

    @Test
    void testGetAllItems() throws Exception {
        when(itemService.getAll(any()))
                .thenReturn(List.of(itemDateCommentDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDateCommentDto.id()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDateCommentDto.name())))
                .andExpect(jsonPath("$[0].description", is(itemDateCommentDto.description())))
                .andExpect(jsonPath("$[0].available", is(itemDateCommentDto.available())));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemService.search(any()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", itemDto.name())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.id()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.name())))
                .andExpect(jsonPath("$[0].description", is(itemDto.description())))
                .andExpect(jsonPath("$[0].available", is(itemDto.available())));
    }

    @Test
    void testAddComment() throws Exception {
        when(itemService.addComment(any(), any(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/" + itemDto.id() + "/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(commentDto.id()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.text())))
                .andExpect(jsonPath("$.authorName", is(commentDto.authorName())));
    }

}
