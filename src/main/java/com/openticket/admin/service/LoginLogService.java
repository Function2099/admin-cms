package com.openticket.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openticket.admin.entity.LoginLog;
import com.openticket.admin.repository.LoginLogRepository;

@Service
public class LoginLogService {
    @Autowired
    private LoginLogRepository loginLogRepository;

    public Page<LoginLog> searchLoginLogs(String keyword, Pageable pageable) {
        return loginLogRepository.searchLoginLogs(keyword, pageable);
    }
}
