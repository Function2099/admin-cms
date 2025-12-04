package com.openticket.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checkout_orders")
@Getter
@Setter
@NoArgsConstructor
public class CheckoutOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 關聯到對應的活動票種(event_ticket_type_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_ticket_type_id", nullable = false)
    private EventTicketType eventTicketType;

    // 關聯到訂單主檔(order_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // price_at_purchase(購買時票價快照)
    @Column(name = "price_at_purchase", nullable = false)
    private java.math.BigDecimal priceAtPurchase;

    // quantity(購買張數)
    @Column(nullable = false)
    private Integer quantity;

}
