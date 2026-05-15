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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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


    @Test
    void testUpdateUserEmptyBody() throws Exception {
        mvc.perform(patch("/users/" + userDto.id())
                        .content("{}")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).getById(any());

        mvc.perform(get("/users/" + userDto.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void test1DeleteUserNotFound() throws Exception {
        doThrow(new NotFoundException("User not found"))
                .when(userService).delete(any());

        mvc.perform(delete("/users/" + userDto.id())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUserReturnsNull() throws Exception {
        when(userService.create(any()))
                .thenReturn(null);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void test2GetUserNotFound() throws Exception {
        when(userService.getById(any()))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mvc.perform(get("/users/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUserEdge() throws Exception {
        UserDto deleted = UserDto.builder()
                .id(99L)
                .email("x@mail.com")
                .name("X")
                .build();

        when(userService.delete(any()))
                .thenReturn(deleted);

        mvc.perform(delete("/users/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99));
    }


    @Test
    void testCreateUserInvalidEmail() throws Exception {
        NewUserRequest bad = NewUserRequest.builder()
                .email("not-email")
                .name("x")
                .build();

        when(userService.create(any()))
                .thenThrow(new RuntimeException("bad email"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(bad))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
