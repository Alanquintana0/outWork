package com.outworkit.outworkit.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_muscle_group")
@Data
public class MuscleGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "muscleGroup")
    private List<Muscle> muscles = new ArrayList<>();

    @OneToMany(mappedBy = "muscleGroup")
    private List<Exercise> exercises = new ArrayList<>();
}

