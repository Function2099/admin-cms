package com.openticket.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openticket.admin.entity.EventTicketType;

public interface EventTicketTypeRepository extends JpaRepository<EventTicketType, Long> {

}
