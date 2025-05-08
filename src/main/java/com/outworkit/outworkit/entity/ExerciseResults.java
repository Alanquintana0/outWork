package com.outworkit.outworkit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_exerciseResults")
public class ExerciseResults {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int setNumber;

    private int reps;

    private double weight;

    @ManyToOne
    @JoinColumn(name = "workoutExercise_id", referencedColumnName = "id")
    private WorkoutExercise workoutExercise;
}
