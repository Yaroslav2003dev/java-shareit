package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    @Query("select b from Booking b WHERE b.booker.id=?1 AND CURRENT_TIMESTAMP < b.start order by b.start desc")
    List<Booking> findFutureBookingsByBooker(Long userId);

    @Query("select b from Booking b WHERE b.booker.id=?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end order by b.start desc")
    List<Booking> findCurrentBookingsByBooker(Long userId);

    @Query("select b from Booking b WHERE b.booker.id=?1 AND CURRENT_TIMESTAMP > b.end order by b.start desc")
    List<Booking> findPastBookingsByBooker(Long userId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status);

    @Query("select b from Booking b " +
            "JOIN b.item i WHERE i.owner.id=?1 AND CURRENT_TIMESTAMP<b.start order by b.start desc")
    List<Booking> findAllByOwnerFutureStartDesc(Long userId);

    @Query("select b from Booking b" +
            " JOIN b.item i WHERE i.owner.id=?1 AND CURRENT_TIMESTAMP BETWEEN b.start AND b.end order by b.start desc")
    List<Booking> findAllByOwnerCurrentStartDesc(Long userId);

    @Query("select b from Booking b" +
            " JOIN b.item i WHERE i.owner.id=?1  AND CURRENT_TIMESTAMP > b.end order by b.start desc")
    List<Booking> findAllByOwnerPastStartDesc(Long userId);

    @Query("select b.item.id as itemId, b as booking from Booking b WHERE b.item.id in :setItems AND b.start = (select Min(b1.start) from Booking b1 WHERE b.item.id=b1.item.id AND CURRENT_TIMESTAMP < b1.start)")
    List<ItemIdBookingProjection> findNextByItemIds(@Param("setItems") Set<Long> setItems);

    @Query("select b.item.id as itemId, b as booking from Booking b WHERE b.item.id in :setItems AND b.end = (select Max(b1.end) from Booking b1 WHERE b.item.id=b1.item.id AND CURRENT_TIMESTAMP > b1.end )")
    List<ItemIdBookingProjection> findLastByItemIds(@Param("setItems") Set<Long> setItems);

    @Query("select b from Booking b WHERE b.item.id=?1 AND b.end >= ?2 AND b.start <= ?3")
    List<Booking> findBusyBookingsByItemId(Long itemId, LocalDateTime start, LocalDateTime end);

    @Query("select count(b) > 0 from Booking b WHERE b.item.id=?1 AND b.booker.id=?2 AND b.end < CURRENT_TIMESTAMP AND b.status=?3")
    Boolean existsCompletedBooking(Long itemId, Long bookerId, Status status);

    List<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);
}
