package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    @Query("""
            select r
            from Request r
            where r.requestor.id<>?1
            order by r.created desc
            """)
    List<Request> findAllOtherUsersRequests(Long userId);

}
