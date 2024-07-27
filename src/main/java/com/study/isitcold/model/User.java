package com.study.isitcold.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String role;
    private String provider;
    @Column(name = "provider_id")
    private String providerId;
    @CreationTimestamp
    @Column(name = "create_date")
    private Timestamp createDate;

    private Double temp;



    @Builder
    public User(String username, String password, String email, String role,
                String provider, String providerId, Timestamp createdDate) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.createDate = createdDate;
    }
}
