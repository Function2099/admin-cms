package com.openticket.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openticket.admin.entity.TicketType;

public interface TicketTypeRepository extends JpaRepository<TicketType, Long> {
}
