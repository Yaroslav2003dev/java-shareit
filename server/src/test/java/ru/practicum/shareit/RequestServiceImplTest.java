package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

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
        NewUserRequest newUser = NewUserRequest.builder()
                .email("test1@mail.com")
                .name("Тест")
                .build();
        UserDto userDto = userService.create(newUser);

        RequestDto requestDto = RequestDto.builder()
                .requestor(userDto)
                .description("Тест")
                .build();

        RequestDto requestDtoSave = requestService.addRequest(requestDto, userDto.id());

        TypedQuery<Request> query = em.createQuery(
                "select r from Request r where r.id = :id",
                Request.class
        );
        Request request = query.setParameter("id", requestDtoSave.id())
                .getSingleResult();

        assertThat(requestDtoSave.id(), notNullValue());
        assertThat(requestDtoSave.description(), equalTo(requestDto.description()));
        assertThat(requestDtoSave.requestor(), equalTo(requestDto.requestor()));
        assertThat(requestDtoSave.created(), notNullValue());

        assertThat(request.getId(), equalTo(requestDtoSave.id()));
        assertThat(request.getDescription(), equalTo(requestDtoSave.description()));
        assertThat(request.getRequestor().getId(), equalTo(userDto.id()));
    }

    @Test
    void testGetMyRequests() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("owner@mail")
                .name("Owner")
                .build();

        UserDto userDto = userService.create(newUser);

        RequestDto request1 = RequestDto.builder()
                .description("Нужен шуруповерт")
                .requestor(userDto)
                .build();

        RequestDto request2 = RequestDto.builder()
                .description("Нужна лестница")
                .requestor(userDto)
                .build();

        requestService.addRequest(request1, userDto.id());
        requestService.addRequest(request2, userDto.id());

        List<RequestItemDto> requests = requestService.getMyRequests(userDto.id());

        assertThat(requests, hasSize(2));
        assertThat(requests.get(0).id(), notNullValue());
        assertThat(requests.get(0).description(), notNullValue());
    }

    @Test
    void test1GetAllRequests() {
        NewUserRequest firstUser = NewUserRequest.builder()
                .email("first@mail")
                .name("First")
                .build();

        NewUserRequest secondUser = NewUserRequest.builder()
                .email("second@mail")
                .name("Second")
                .build();

        UserDto firstUserDto = userService.create(firstUser);
        UserDto secondUserDto = userService.create(secondUser);

        RequestDto requestDto = RequestDto.builder()
                .description("Нужен молоток")
                .requestor(firstUserDto)
                .build();

        requestService.addRequest(requestDto, firstUserDto.id());

        List<RequestDto> requests = requestService.getAllRequests(secondUserDto.id());

        assertThat(requests, hasSize(1));
        assertThat(requests.get(0).description(), equalTo("Нужен молоток"));
    }

    @Test
    void testGetRequestById() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("request@mail")
                .name("Requester")
                .build();

        UserDto userDto = userService.create(newUser);

        RequestDto requestDto = RequestDto.builder()
                .description("Нужен велосипед")
                .requestor(userDto)
                .build();

        RequestDto savedRequest = requestService.addRequest(requestDto, userDto.id());

        RequestItemDto foundRequest = requestService.getByRequestId(savedRequest.id());

        assertThat(foundRequest.id(), equalTo(savedRequest.id()));
        assertThat(foundRequest.description(), equalTo(savedRequest.description()));
        assertThat(foundRequest.created(), notNullValue());
    }

    @Test
    void testGetMyRequestsEmpty() {
        UserDto user = userService.create(new NewUserRequest("u@mail", "U"));

        var result = requestService.getMyRequests(user.id());

        assertThat(result, hasSize(0));
    }

    @Test
    void test2GetAllRequests() {
        UserDto user = userService.create(new NewUserRequest("u@mail", "U"));

        requestService.addRequest(
                RequestDto.builder()
                        .description("Нужна вещь")
                        .build(),
                user.id()
        );

        var result = requestService.getAllRequests(user.id());

        assertThat(result.size(), equalTo(0));
    }

}
