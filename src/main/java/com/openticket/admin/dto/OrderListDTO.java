package com.openticket.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openticket.admin.utils.DateTimeUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderListDTO {
    private Long orderId; // 訂單 ID（orders.id）
    private LocalDateTime createdAt; // 訂單建立時間（用 reservations.created_at）
    private String buyerName; // 購買人（user.username）
    private String eventTitle; // 活動名稱（event.title）
    private Long ticketCount; // 總張數（SUM(checkout_orders.quantity)）
    private BigDecimal totalAmount;// 總金額（reservations.totalAmount）
    private String status; // 訂單狀態（orders.status）

    @JsonProperty("createdAt")
    public String getCreatedAtFormatted() {
        return DateTimeUtil.format(createdAt);
    }
}
