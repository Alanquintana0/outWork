package com.outworkit.outworkit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_exercise")
@Data
public class Exercise {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "muscle_group_id")
    private MuscleGroup muscleGroup;
}

