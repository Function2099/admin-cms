package com.openticket.admin.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company_profile")
@Getter
@Setter
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "company_name")
    private String companyName;

    private String tel;
    private String address;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonBackReference("company-event")
    private List<Event> events;

}
