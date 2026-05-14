package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {
    private final UserService userService;
    private final RequestService requestService;
    private final EntityManager em;

    @Test
    void testSaveRequest() {
        NewUserRequest newUser1 = NewUserRequest.builder()
                .email("test1@mail")
                .name("Тест")
                .build();
        UserDto userDto1 = userService.create(newUser1);

        RequestDto requestDto = RequestDto.builder()
                .requestor(userDto1)
                .description("Тест")
                .build();

        RequestDto requestDtoSave = requestService.addRequest(requestDto, userDto1.id());

        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.id = :id", Request.class);
        Request user = query.setParameter("id", requestDtoSave.id())
                .getSingleResult();

        assertThat(requestDtoSave.id(), notNullValue());
        assertThat(requestDtoSave.description(), equalTo(requestDto.description()));
        assertThat(requestDtoSave.requestor(), equalTo(requestDto.requestor()));
        assertThat(requestDtoSave.created(), notNullValue());
    }
}
