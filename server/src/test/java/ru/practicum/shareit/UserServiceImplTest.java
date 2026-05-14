package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
}
