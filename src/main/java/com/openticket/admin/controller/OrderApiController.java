package com.openticket.admin.controller;

import com.openticket.admin.dto.OrderListDTO;
import com.openticket.admin.service.OrderService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderListDTO> listOrders(
            @RequestParam List<Long> eventIds,
            @RequestParam(required = false) String keyword) {

        return orderService.getOrders(eventIds, keyword);
    }

}
