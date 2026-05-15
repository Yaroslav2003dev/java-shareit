package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDateCommentDto toItemDateCommentDto(Item item, BookingDto bookingNext, BookingDto bookingLast, List<CommentDto> comments) {
        return new ItemDateCommentDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                bookingNext,
                bookingLast,
                comments
        );
    }

    public static Item toItem(NewItemRequest newItemRequest, Request request) {
        Item item = new Item();
        item.setName(newItemRequest.getName());
        item.setAvailable(newItemRequest.getAvailable());
        item.setDescription(newItemRequest.getDescription());
        item.setRequest(request);
        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest updateItemRequest) {
        if (updateItemRequest.getName() != null && !updateItemRequest.getName().isBlank()) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.getDescription() != null && !updateItemRequest.getDescription().isBlank()) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.getAvailable() != null) {
            item.setAvailable(updateItemRequest.getAvailable());
        }
        return item;
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.text());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                LocalDateTime.ofInstant(comment.getCreated(), ZoneOffset.UTC)
        );
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> commentList) {
        return commentList.stream()
                .map(ItemMapper::toCommentDto)
                .toList();
    }

}
