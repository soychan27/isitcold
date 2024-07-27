package com.study.isitcold.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private int nx;

    @Column(nullable = false)
    private int ny;

    @Column(nullable = false)
    private String level1;

    private String level2;

    private String level3;

}
