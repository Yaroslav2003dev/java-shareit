package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@ToString(exclude = {"item", "booker"})
@EqualsAndHashCode(exclude = {"item", "booker"})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}
