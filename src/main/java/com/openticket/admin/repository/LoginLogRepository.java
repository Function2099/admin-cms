package com.openticket.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openticket.admin.entity.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    @Query("""
            SELECT l FROM LoginLog l
            WHERE
                (:keyword IS NULL OR :keyword = ''
                OR l.ipAddress LIKE CONCAT('%', :keyword, '%')
                OR l.userAgent LIKE CONCAT('%', :keyword, '%')
                OR l.status LIKE CONCAT('%', :keyword, '%')
                OR l.user.account LIKE CONCAT('%', :keyword, '%'))
            ORDER BY l.loginTime DESC
            """)
    Page<LoginLog> searchLoginLogs(
            @Param("keyword") String keyword,
            Pageable pageable);

}
