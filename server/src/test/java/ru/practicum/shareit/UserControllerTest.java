package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .email("yar@mail.com")
            .name("Yar")
            .build();
    private NewUserRequest newUserRequest = NewUserRequest.builder()
            .email("yar@mail.com")
            .name("Yar")
            .build();

    private UpdateUserRequest updateRequest = UpdateUserRequest.builder()
            .name("Новое имя")
            .email("new@mail.com")
            .build();
    private UserDto updatedUser = UserDto.builder()
            .id(1L)
            .name("Новое имя")
            .email("new@mail.com")
            .build();

    @Test
    void testCreateUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(userDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.name())))
                .andExpect(jsonPath("$.email", is(userDto.email())));
    }

    @Test
    void testGetByIdUser() throws Exception {
        when(userService.getById(any()))
                .thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.id())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.name())))
                .andExpect(jsonPath("$.email", is(userDto.email())));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.update(any(), any()))
                .thenReturn(updatedUser);

        mvc.perform(patch("/users/" + userDto.id())
                        .content(mapper.writeValueAsString(updateRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.id()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.name())))
                .andExpect(jsonPath("$.email", is(updatedUser.email())));
    }

    @Test
    void testDelete() throws Exception {
        when(userService.delete(any()))
                .thenReturn(userDto);

        mvc.perform(delete("/users/" + userDto.id())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.id()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.name())))
                .andExpect(jsonPath("$.email", is(userDto.email())));
    }

}
