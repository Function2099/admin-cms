package com.openticket.admin.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDTO {
    private String ticketName; // 票種名稱
    private Integer quantity; // 張數
    private BigDecimal unitPrice;// 單價
    private BigDecimal subtotal; // quantity * unitPrice
}
