package com.openticket.admin.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openticket.admin.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 收入kpi卡片查詢邏輯
    @Query("""
                SELECT DATE(p.paidAt) AS date,
                        SUM(p.amount) AS revenue
                FROM Payment p
                WHERE p.status IN ('SUCCESS','PAID')
                    AND p.order.id IN (
                        SELECT co.order.id
                        FROM CheckoutOrder co
                        JOIN co.eventTicketType ett
                        WHERE ett.event.id IN :eventIds
                    )
                    AND p.paidAt < :end
                    AND p.paidAt >= :start
                GROUP BY DATE(p.paidAt)
                ORDER BY DATE(p.paidAt)
            """)
    List<Object[]> findSalesBetween(
            @Param("eventIds") List<Long> eventIds,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
