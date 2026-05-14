package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;

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
}
