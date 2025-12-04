package com.openticket.admin.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class TicketSalesDTO {
    private String ticketName;
    private Long totalQuantity;
}
