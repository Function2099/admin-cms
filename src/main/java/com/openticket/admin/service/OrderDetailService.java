package com.openticket.admin.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.openticket.admin.entity.Order;

import com.openticket.admin.dto.OrderItemDTO;
import com.openticket.admin.repository.OrderItemRepository;
import com.openticket.admin.repository.OrderRepository;

@Service
public class OrderDetailService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository itemRepo;

    public List<OrderItemDTO> getOrderDetail(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("PAID".equals(order.getStatus())) {
            return itemRepo.findPaidItems(orderId);
        } else {
            Long reservationId = order.getReservation().getId();
            return itemRepo.findPendingItems(reservationId);
        }
    }
}
