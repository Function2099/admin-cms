package com.openticket.admin.controller;

import com.openticket.admin.dto.OrderItemDTO;
import com.openticket.admin.dto.OrderListDTO;
import com.openticket.admin.service.OrderService;
import com.openticket.admin.service.OrderDetailService;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    public OrderApiController(OrderService orderService, OrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    // 訂單列表
    @GetMapping
    public List<OrderListDTO> listOrders(
            @RequestParam List<Long> eventIds,
            @RequestParam(required = false) String keyword) {

        return orderService.getOrders(eventIds, keyword);
    }

    @GetMapping("/detail/{orderId}")
    public List<OrderItemDTO> getOrderDetail(@PathVariable Long orderId) {
        return orderDetailService.getOrderDetail(orderId);
    }
}
