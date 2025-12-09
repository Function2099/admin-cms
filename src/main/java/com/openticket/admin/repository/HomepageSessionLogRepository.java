package com.openticket.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openticket.admin.entity.HomepageSessionLog;

public interface HomepageSessionLogRepository extends JpaRepository<HomepageSessionLog, Long> {
}
