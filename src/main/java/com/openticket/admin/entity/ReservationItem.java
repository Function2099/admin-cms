package com.openticket.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "reservation_items")
@Getter
@Setter
public class ReservationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 活動票種
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_ticket_type_id", nullable = false)
    private EventTicketType eventTicketType;

    // 數量
    @Column(nullable = false)
    private Integer quantity;

    // 所屬預訂單
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservations_id", nullable = false)
    private Reservation reservation;

    // 單價
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
