package com.openticket.admin.service;

import com.openticket.admin.dto.OrderListDTO;
import com.openticket.admin.repository.CheckoutOrderRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private CheckoutOrderRepository repo;

    public List<OrderListDTO> getOrders(List<Long> eventIds, String keyword) {
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }
        return repo.findOrdersByEvents(eventIds, keyword);
    }
}
