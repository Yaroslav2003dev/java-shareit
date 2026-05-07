package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Comment;

public interface ItemCommentProjection {
    Long getItemId();

    Comment getComment();
}
