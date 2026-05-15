package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final UserService userService;
    private final EntityManager em;

    @Test
    void testSaveUser() {
        NewUserRequest newUser1 = NewUserRequest.builder()
                .email("test1@mail")
                .name("Тест")
                .build();
        UserDto userDto1 = userService.create(newUser1);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userDto1.id())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getEmail(), equalTo(userDto1.email()));
        assertThat(user.getName(), equalTo(userDto1.name()));
    }

    @Test
    void testGetUserById() {
        NewUserRequest request = NewUserRequest.builder()
                .email("get@mail.com")
                .name("Get User")
                .build();

        UserDto createdUser = userService.create(request);

        UserDto foundUser = userService.getById(createdUser.id());

        assertThat(foundUser.id(), equalTo(createdUser.id()));
        assertThat(foundUser.email(), equalTo(createdUser.email()));
        assertThat(foundUser.name(), equalTo(createdUser.name()));
    }

    @Test
    void testGetUserByIdNotFound() {
        assertThrows(NotFoundException.class,
                () -> userService.getById(9999L));
    }

    @Test
    void testUpdateUserName() {
        NewUserRequest request = NewUserRequest.builder()
                .email("update@mail.com")
                .name("Old Name")
                .build();

        UserDto createdUser = userService.create(request);

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("New Name")
                .build();

        UserDto updatedUser = userService.update(createdUser.id(), updateRequest);

        assertThat(updatedUser.name(), equalTo("New Name"));
        assertThat(updatedUser.email(), equalTo("update@mail.com"));
    }

    @Test
    void testUpdateUserEmail() {
        NewUserRequest request = NewUserRequest.builder()
                .email("old@mail.com")
                .name("User")
                .build();

        UserDto createdUser = userService.create(request);

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .email("new@mail.com")
                .build();

        UserDto updatedUser = userService.update(createdUser.id(), updateRequest);

        assertThat(updatedUser.email(), equalTo("new@mail.com"));
    }

    @Test
    void testUpdateUserNotFound() {
        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .name("Name")
                .build();

        assertThrows(NotFoundException.class,
                () -> userService.update(9999L, updateRequest));
    }

    @Test
    void testDeleteUser() {
        NewUserRequest request = NewUserRequest.builder()
                .email("delete@mail.com")
                .name("Delete User")
                .build();

        UserDto createdUser = userService.create(request);

        userService.delete(createdUser.id());

        assertThrows(NotFoundException.class,
                () -> userService.getById(createdUser.id()));
    }

    @Test
    void testDeleteUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> userService.delete(9999L));
    }

    @Test
    void testCreateUsersWithDifferentEmails() {
        NewUserRequest request1 = NewUserRequest.builder()
                .email("first@mail.com")
                .name("First")
                .build();

        NewUserRequest request2 = NewUserRequest.builder()
                .email("second@mail.com")
                .name("Second")
                .build();

        UserDto user1 = userService.create(request1);
        UserDto user2 = userService.create(request2);

        assertThat(user1.id(), notNullValue());
        assertThat(user2.id(), notNullValue());
    }
}
