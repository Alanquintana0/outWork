package com.outworkit.outworkit.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tbl_exercise_results")
@Data
public class ExerciseResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int reps;
    private int setNumber;
    private double weight;

    @ManyToOne
    @JoinColumn(name = "workout_exercise_id")
    private WorkoutExercise workoutExercise;
}
