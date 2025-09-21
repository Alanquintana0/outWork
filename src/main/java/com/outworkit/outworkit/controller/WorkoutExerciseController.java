package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.entity.WorkoutExercise;
import com.outworkit.outworkit.service.WorkoutExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workout-exercises")
@RequiredArgsConstructor
@Slf4j
public class WorkoutExerciseController {

    private final WorkoutExerciseService workoutExerciseService;

    @GetMapping
    public ResponseEntity<List<WorkoutExercise>> getAllWorkoutExercises() {
        log.info("Solicitando todos los ejercicios de entrenamiento");
        List<WorkoutExercise> workoutExercises = workoutExerciseService.getWorkoutExercises();
        return ResponseEntity.ok(workoutExercises);
    }

    @GetMapping("/{workoutExerciseId}")
    public ResponseEntity<WorkoutExercise> getWorkoutExerciseById(@PathVariable Long workoutExerciseId) {
        log.info("Solicitando ejercicio de entrenamiento con ID: {}", workoutExerciseId);
        WorkoutExercise workoutExercise = workoutExerciseService.getWorkoutExercise(workoutExerciseId);
        return ResponseEntity.ok(workoutExercise);
    }

    @PostMapping
    public ResponseEntity<WorkoutExercise> createWorkoutExercise(@Valid @RequestBody WorkoutExercise workoutExercise) {
        // return 400 when the provided workout exercise is invalid
        if (!isValidWorkoutExercise(workoutExercise)) {
            log.warn("Provide valid workout exercise data");
            return ResponseEntity.badRequest().build();
        }
        WorkoutExercise savedWorkoutExercise = workoutExerciseService.saveOrUpdate(workoutExercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWorkoutExercise);
    }

    @PutMapping("/{workoutExerciseId}")
    public ResponseEntity<WorkoutExercise> updateWorkoutExercise(
            @PathVariable Long workoutExerciseId,
            @Valid @RequestBody WorkoutExercise workoutExercise) {

        log.info("Actualizando ejercicio de entrenamiento con ID: {}", workoutExerciseId);
        // ensure the path id is used
        workoutExercise.setId(workoutExerciseId);

        // return 400 when the provided workout exercise is invalid
        if (!isValidWorkoutExercise(workoutExercise)) {
            log.warn("Provide valid workout exercise data");
            return ResponseEntity.badRequest().build();
        }

        // Delegate further validation/error handling to the service and global exception handler.
        WorkoutExercise updatedWorkoutExercise = workoutExerciseService.saveOrUpdate(workoutExercise);
        return ResponseEntity.ok(updatedWorkoutExercise);
    }

    @DeleteMapping("/{workoutExerciseId}")
    public ResponseEntity<Void> deleteWorkoutExercise(@PathVariable Long workoutExerciseId) {
        log.info("Eliminando ejercicio de entrenamiento con ID: {}", workoutExerciseId);
        workoutExerciseService.delete(workoutExerciseId);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidWorkoutExercise(WorkoutExercise workoutExercise) {
        if (workoutExercise == null) {
            log.warn("WorkoutExercise is null");
            return false;
        }
        // Treat missing fields (null) as invalid, but allow blank strings so the
        // service can apply business-level validation and throw custom exceptions.
        if (workoutExercise.getWorkout() == null) {
            log.warn("WorkoutExercise workout is null");
            return false;
        }
        if (workoutExercise.getExercise() == null) {
            log.warn("WorkoutExercise exercise is null");
            return false;
        }
        return true;
    }
}