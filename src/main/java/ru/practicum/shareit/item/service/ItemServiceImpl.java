package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.repository.ItemIdBookingProjection;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemCommentProjection;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, NewItemRequest newItemRequest) {
        Item item = ItemMapper.toItem(newItemRequest);
        if (userId == null) {
            throw new InternalServerException("В заголовке не передан userId");
        } else {
            item.setOwner(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId")));
        }
        Item saveItem = itemRepository.save(item);
        log.info("Создан предмет с id = " + saveItem.getId());
        return ItemMapper.toItemDto(saveItem);
    }

    @Override
    public ItemDateCommentDto getById(Long userId, Long id) {
        log.info("Поиск предмета с id = " + id);
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        boolean isOwner = item.getOwner().getId().equals(userId);
        BookingDto lastBookingDto = null;
        BookingDto nextBookingDto = null;
        Set<Long> ids = new HashSet<>();
        ids.add(item.getId());
        if (isOwner) {
            Map<Long, Booking> lastBooking = bookingRepository.findLastByItemIds(ids)
                    .stream()
                    .collect(Collectors.toMap(ItemIdBookingProjection::getItemId,
                            ItemIdBookingProjection::getBooking));
            Map<Long, Booking> nextBooking = bookingRepository.findNextByItemIds(ids)
                    .stream()
                    .collect(Collectors.toMap(ItemIdBookingProjection::getItemId,
                            ItemIdBookingProjection::getBooking));
            lastBookingDto = Optional.ofNullable(lastBooking.get(id))
                    .map(BookingMapper::toBookingDto)
                    .orElse(null);

            nextBookingDto = Optional.ofNullable(nextBooking.get(id))
                    .map(BookingMapper::toBookingDto)
                    .orElse(null);
        }
        List<CommentDto> comments = ItemMapper.toCommentDtoList(commentRepository.findAllByItemId(id));
        return ItemMapper.toItemDateCommentDto(item, nextBookingDto, lastBookingDto, comments);
    }

    @Override
    public List<ItemDateCommentDto> getAll(Long userId) {
        if (userId == null) {
            throw new InternalServerException("В заголовке не передан userId");
        } else {
            userRepository.findById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        }
        log.info("Поиск предметов пользователя с userId = " + userId);

        Map<Long, Item> itemMap = itemRepository.findAllByOwnerId(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        Map<Long, Booking> lastBookings = bookingRepository.findLastByItemIds(itemMap.keySet())
                .stream()
                .collect(Collectors.toMap(ItemIdBookingProjection::getItemId,
                        ItemIdBookingProjection::getBooking));

        Map<Long, Booking> nextBookings = bookingRepository.findNextByItemIds(itemMap.keySet())
                .stream()
                .collect(Collectors.toMap(ItemIdBookingProjection::getItemId,
                        ItemIdBookingProjection::getBooking));

        Map<Long, List<Comment>> commentItem = commentRepository.findAllComments().stream()
                .collect(Collectors.groupingBy(
                        ItemCommentProjection::getItemId,
                        Collectors.mapping(ItemCommentProjection::getComment, Collectors.toList())
                ));

        return itemMap.values()
                .stream()
                .map(item -> {
                    BookingDto lastBookingDto = Optional.ofNullable(lastBookings.get(item.getId()))
                            .map(BookingMapper::toBookingDto)
                            .orElse(null);

                    BookingDto nextBookingDto = Optional.ofNullable(nextBookings.get(item.getId()))
                            .map(BookingMapper::toBookingDto)
                            .orElse(null);

                    List<CommentDto> commentList = Optional.ofNullable(commentItem.get(item.getId()))
                            .orElse(List.of())
                            .stream()
                            .map(ItemMapper::toCommentDto)
                            .toList();

                    return ItemMapper.toItemDateCommentDto(item, lastBookingDto, nextBookingDto, commentList);
                })
                .toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Поиск предметов по слову = " + text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequest updateItemRequest) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Эта вещь не принадлежит пользователю");
        }
        Item itemUp = ItemMapper.updateItemFields(item, updateItemRequest);
        log.info("Предмет с id = " + itemId + " обновлён");
        return ItemMapper.toItemDto(itemUp);
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + "не найден"));
        List<Booking> bookingsList = bookingRepository.findAllByItemIdAndBookerId(itemId, userId);
        if (bookingsList.isEmpty()) {
            throw new ValidationException("Пользователь с id " + userId + " не арендовывал вещь с id = " + item.getId());
        }
        boolean isCompletedBooking = bookingRepository.hasCompletedBooking(itemId, userId);
        if (!isCompletedBooking) {
            throw new ValidationException("Пользователь написал комментарий не после завершения бронирования");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("В заголовке передан не существующий userId"));
        Comment comment = ItemMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        commentRepository.save(comment);
        log.info("Добавлен комментарий к предмету с id = " + itemId);
        return ItemMapper.toCommentDto(comment);
    }


}
