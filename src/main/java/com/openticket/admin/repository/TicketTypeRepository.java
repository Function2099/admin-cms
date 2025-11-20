package com.openticket.admin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openticket.admin.entity.TicketType;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
    List<TicketType> findByIsDefaultFalse(); // 系統模板

    List<TicketType> findByIsDefaultTrueAndUserId(Long userId); // 主辦方自訂

}
