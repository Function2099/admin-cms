package com.openticket.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openticket.admin.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
