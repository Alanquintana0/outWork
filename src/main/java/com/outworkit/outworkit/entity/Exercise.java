package com.outworkit.outworkit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String riskLevel;

    @ManyToOne
    @JoinColumn(name = "muscle_group_id", referencedColumnName = "id")
    private MuscleGroup muscleGroup;

    @ManyToOne
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private Equipment equipment;
}
