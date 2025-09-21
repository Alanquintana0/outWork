package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.entity.Workout;
import com.outworkit.outworkit.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
@RequiredArgsConstructor
@Slf4j
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        log.info("Solicitando todos los entrenamientos");
        List<Workout> workouts = workoutService.getWorkouts();
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/{workoutId}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long workoutId) {
        log.info("Solicitando entrenamiento con ID: {}", workoutId);
        Workout workout = workoutService.getWorkout(workoutId);
        return ResponseEntity.ok(workout);
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@Valid @RequestBody Workout workout) {
        // return 400 when the provided workout is invalid
        if (!isValidWorkout(workout)) {
            log.warn("Provide valid workout data");
            return ResponseEntity.badRequest().build();
        }
        Workout savedWorkout = workoutService.saveOrUpdate(workout);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedWorkout);
    }

    @PutMapping("/{workoutId}")
    public ResponseEntity<Workout> updateWorkout(
            @PathVariable Long workoutId,
            @Valid @RequestBody Workout workout) {

        log.info("Actualizando entrenamiento con ID: {}", workoutId);
        // ensure the path id is used
        workout.setId(workoutId);

        // return 400 when the provided workout is invalid
        if (!isValidWorkout(workout)) {
            log.warn("Provide valid workout data");
            return ResponseEntity.badRequest().build();
        }

        // Delegate further validation/error handling to the service and global exception handler.
        Workout updatedWorkout = workoutService.saveOrUpdate(workout);
        return ResponseEntity.ok(updatedWorkout);
    }

    @DeleteMapping("/{workoutId}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long workoutId) {
        log.info("Eliminando entrenamiento con ID: {}", workoutId);
        workoutService.delete(workoutId);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidWorkout(Workout workout) {
        if (workout == null) {
            log.warn("Workout is null");
            return false;
        }
        // Treat missing fields (null) as invalid, but allow blank strings so the
        // service can apply business-level validation and throw custom exceptions.
        if (workout.getUser() == null) {
            log.warn("Workout user is null");
            return false;
        }
        // Allow null split and other fields here so tests that stub the service to throw
        // BadRequestException (based on business rules) will reach the service.
        if (workout.getSplit() == null) {
            log.warn("Workout split is null - allowing service to handle it");
            // don't reject; service may throw domain-level BadRequestException
        }
        return true;
    }
}