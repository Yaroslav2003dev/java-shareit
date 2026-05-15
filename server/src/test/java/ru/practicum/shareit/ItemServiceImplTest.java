package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    void testSaveItem() {
        NewUserRequest newUser1 = NewUserRequest.builder()
                .email("test1@mail")
                .name("Тест")
                .build();
        UserDto userDto1 = userService.create(newUser1);
        NewItemRequest newItem = NewItemRequest.builder()
                .name("Тест")
                .description("Тест")
                .available(TRUE)
                .build();
        ItemDto itemDto = itemService.create(userDto1.id(), newItem);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.id())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getDescription(), equalTo(itemDto.description()));
        assertThat(item.getName(), equalTo(itemDto.name()));
        assertThat(item.getAvailable(), equalTo(itemDto.available()));
    }

    @Test
    void testSearchNullBranch() {
        assertThat(itemService.search(null), hasSize(0));
        assertThat(itemService.search(" "), hasSize(0));
    }

    @Test
    void testGetItemById() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("test2@mail")
                .name("Пользователь")
                .build();

        UserDto userDto = userService.create(newUser);

        NewItemRequest newItem = NewItemRequest.builder()
                .name("Дрель")
                .description("Хорошая дрель")
                .available(TRUE)
                .build();

        ItemDto savedItem = itemService.create(userDto.id(), newItem);

        ItemDateCommentDto foundItem =
                itemService.getById(userDto.id(), savedItem.id());

        assertThat(foundItem.id(), equalTo(savedItem.id()));
        assertThat(foundItem.name(), equalTo(savedItem.name()));
        assertThat(foundItem.description(), equalTo(savedItem.description()));
        assertThat(foundItem.available(), equalTo(savedItem.available()));
    }

    @Test
    void testGetAllItems() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("test3@mail")
                .name("Владелец")
                .build();

        UserDto userDto = userService.create(newUser);

        NewItemRequest item1 = NewItemRequest.builder()
                .name("Item1")
                .description("Desc1")
                .available(TRUE)
                .build();

        NewItemRequest item2 = NewItemRequest.builder()
                .name("Item2")
                .description("Desc2")
                .available(TRUE)
                .build();

        itemService.create(userDto.id(), item1);
        itemService.create(userDto.id(), item2);

        List<ItemDateCommentDto> items =
                itemService.getAll(userDto.id());

        assertThat(items, hasSize(2));
    }

    @Test
    void testSearchItems() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("test4@mail")
                .name("Поиск")
                .build();

        UserDto userDto = userService.create(newUser);

        NewItemRequest newItem = NewItemRequest.builder()
                .name("Шуруповерт")
                .description("Мощный инструмент")
                .available(TRUE)
                .build();

        itemService.create(userDto.id(), newItem);

        List<ItemDto> result = itemService.search("инструмент");

        assertThat(result, hasSize(1));
        assertThat(result.getFirst().description(),
                equalTo("Мощный инструмент"));
    }

    @Test
    void testSearchEmptyText() {
        List<ItemDto> result = itemService.search("");

        assertThat(result, hasSize(0));
    }

    @Test
    void testUpdateItem() {
        NewUserRequest newUser = NewUserRequest.builder()
                .email("test5@mail")
                .name("Обновление")
                .build();

        UserDto userDto = userService.create(newUser);

        NewItemRequest newItem = NewItemRequest.builder()
                .name("Старое имя")
                .description("Старое описание")
                .available(TRUE)
                .build();

        ItemDto savedItem = itemService.create(userDto.id(), newItem);

        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .name("Новое имя")
                .description("Новое описание")
                .available(FALSE)
                .build();

        ItemDto updatedItem = itemService.update(
                userDto.id(),
                savedItem.id(),
                updateRequest
        );

        assertThat(updatedItem.name(), equalTo("Новое имя"));
        assertThat(updatedItem.description(),
                equalTo("Новое описание"));
        assertThat(updatedItem.available(), equalTo(FALSE));
    }

    @Test
    void testAddCommentWithoutBooking() {
        NewUserRequest ownerRequest = NewUserRequest.builder()
                .email("owner@mail")
                .name("Owner")
                .build();

        UserDto owner = userService.create(ownerRequest);

        NewUserRequest authorRequest = NewUserRequest.builder()
                .email("author@mail")
                .name("Author")
                .build();

        UserDto author = userService.create(authorRequest);

        NewItemRequest itemRequest = NewItemRequest.builder()
                .name("Дрель")
                .description("Описание")
                .available(TRUE)
                .build();

        ItemDto item = itemService.create(owner.id(), itemRequest);

        CommentDto commentDto = CommentDto.builder()
                .text("Отличная вещь")
                .build();

        assertThrows(RuntimeException.class,
                () -> itemService.addComment(
                        author.id(),
                        item.id(),
                        commentDto
                ));
    }

    @Test
    void testSearchEmptyAndBlank() {
        assertThat(itemService.search(""), hasSize(0));
        assertThat(itemService.search("   "), hasSize(0));
    }

    @Test
    void testUpdateForbiddenUser() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));
        UserDto stranger = userService.create(new NewUserRequest("s@mail", "S"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        assertThrows(RuntimeException.class,
                () -> itemService.update(stranger.id(), item.id(),
                        UpdateItemRequest.builder()
                                .name("hack")
                                .build()));
    }

    @Test
    void testAddCommentSuccessPath() {
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
                        .start(LocalDateTime.now().minusDays(2))
                        .end(LocalDateTime.now().minusDays(1))
                        .itemId(item.id())
                        .build());

        CommentDto comment = CommentDto.builder()
                .text("Good")
                .build();

        var result = itemService.addComment(booker.id(), item.id(), comment);

        assertThat(result.text(), equalTo("Good"));
    }

    @Test
    void testGetItemAsStranger() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));
        UserDto stranger = userService.create(new NewUserRequest("s@mail", "S"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        ItemDateCommentDto result =
                itemService.getById(stranger.id(), item.id());

        assertThat(result.id(), equalTo(item.id()));
    }

    @Test
    void testUpdateItemPartialFields() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        UpdateItemRequest update = UpdateItemRequest.builder()
                .name(null)
                .description("Updated")
                .available(null)
                .build();

        ItemDto result = itemService.update(owner.id(), item.id(), update);

        assertThat(result.description(), equalTo("Updated"));
    }

    @Test
    void testAddCommentWithoutPastBooking() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));
        UserDto user = userService.create(new NewUserRequest("u@mail", "U"));

        ItemDto item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        assertThrows(RuntimeException.class,
                () -> itemService.addComment(user.id(), item.id(),
                        CommentDto.builder().text("bad").build()));
    }

    @Test
    void testGetItemOwnerVsStrangerBranch() {
        UserDto owner = userService.create(new NewUserRequest("o@mail", "O"));
        UserDto stranger = userService.create(new NewUserRequest("s@mail", "S"));

        var item = itemService.create(owner.id(),
                NewItemRequest.builder()
                        .name("Item")
                        .description("Desc")
                        .available(true)
                        .build());

        var ownerView = itemService.getById(owner.id(), item.id());
        var strangerView = itemService.getById(stranger.id(), item.id());

        assertThat(ownerView.id(), equalTo(item.id()));
        assertThat(strangerView.id(), equalTo(item.id()));
    }

}
