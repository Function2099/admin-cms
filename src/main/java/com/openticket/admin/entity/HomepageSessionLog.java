package com.openticket.admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "homepage_session_log")
@Getter
@Setter
public class HomepageSessionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_visit", nullable = false)
    private LocalDateTime firstVisit;

    @Column(name = "session_id", nullable = false, length = 100)
    private String sessionId;
}
