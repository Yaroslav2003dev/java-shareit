package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c.item.id, c from Comment c")
    List<ItemCommentProjection> findAllComments();

    List<Comment> findAllByItemId(Long itemId);
}
