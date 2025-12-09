package com.openticket.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_log")
@Getter
@Setter
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "status", nullable = false)
    private String status; // SUCCESS / FAIL

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
