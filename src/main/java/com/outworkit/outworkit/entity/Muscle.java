package com.outworkit.outworkit.entity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_muscle")
public class Muscle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "muscle_group_id", referencedColumnName = "id")
    private MuscleGroup muscleGroup;

}
