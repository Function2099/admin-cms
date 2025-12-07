package com.openticket.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.openticket.admin.dto.OrderItemDTO;
import com.openticket.admin.entity.ReservationItem;

@Repository
public interface OrderItemRepository extends JpaRepository<ReservationItem, Long> {

    // 已付款 → 查 checkout_orders
    @Query("""
                SELECT new com.openticket.admin.dto.OrderItemDTO(
                    tt.name,
                    co.quantity,
                    co.unitPrice,
                    (co.quantity * co.unitPrice)
                )
                FROM CheckoutOrder co
                JOIN co.eventTicketType ett
                JOIN ett.ticketTemplate tt
                WHERE co.order.id = :orderId
            """)
    List<OrderItemDTO> findPaidItems(Long orderId);

    // 未付款 → 查 reservation_items
    @Query("""
                SELECT new com.openticket.admin.dto.OrderItemDTO(
                    tt.name,
                    ri.quantity,
                    ri.unitPrice,
                    (ri.quantity * ri.unitPrice)
                )
                FROM ReservationItem ri
                JOIN ri.eventTicketType ett
                JOIN ett.ticketTemplate tt
                WHERE ri.reservation.id = :reservationId
            """)
    List<OrderItemDTO> findPendingItems(Long reservationId);
}
