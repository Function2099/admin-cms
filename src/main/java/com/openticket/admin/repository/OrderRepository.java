package com.openticket.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.openticket.admin.dto.OrderListDTO;
import com.openticket.admin.entity.CheckoutOrder;

@Repository
public interface OrderRepository extends JpaRepository<CheckoutOrder, Long> {

    @Query("""
                SELECT new com.openticket.admin.dto.OrderListDTO(
                    o.id,
                    r.createdAt,
                    u.username,
                    e.title,
                    SUM(co.quantity),
                    SUM(co.quantity * co.unitPrice),
                    o.status
                )
                FROM CheckoutOrder co
                    JOIN co.order o
                    JOIN o.reservation r
                    JOIN r.user u
                    JOIN co.eventTicketType ett
                    JOIN ett.event e
                WHERE e.id IN :eventIds
                    AND (
                        :keyword IS NULL OR :keyword = ''
                        OR u.username LIKE CONCAT('%', :keyword, '%')
                        OR CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%')
                    )
                GROUP BY
                    o.id, r.createdAt, u.username, e.title, o.status
                ORDER BY r.createdAt DESC
            """)
    List<OrderListDTO> findOrdersByEvents(
            @Param("eventIds") List<Long> eventIds,
            @Param("keyword") String keyword);
}
