package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.WorkoutExercise;
import com.outworkit.outworkit.repository.WorkoutExerciseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkoutExerciseService {

    private final WorkoutExerciseRepository workoutExerciseRepository;

    public List<WorkoutExercise> getWorkoutExercises() {
        try {
            log.debug("Getting workout exercises");
            List<WorkoutExercise> workoutExercises = workoutExerciseRepository.findAll();
            log.info("Workout exercises retrieved");
            return workoutExercises;
        } catch (Exception e) {
            log.error("Error retrieving workout exercises", e);
            throw e;
        }
    }

    public WorkoutExercise getWorkoutExercise(Long id) {
        log.debug("Looking for workout exercise with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        return workoutExerciseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Workout exercise not found with id {}", id);
                    return new ResourceNotFoundException(
                            String.format("Workout exercise not found with ID: %d", id)
                    );
                });
    }

    public Optional<WorkoutExercise> findWorkoutExerciseById(Long workoutExerciseId) {
        log.debug("Searching for workout exercise with ID: {}", workoutExerciseId);

        if (workoutExerciseId == null) {
            return Optional.empty();
        }

        return workoutExerciseRepository.findById(workoutExerciseId);
    }

    @Transactional
    public WorkoutExercise saveOrUpdate(WorkoutExercise workoutExercise) {
        log.debug("Saving/updating workout exercise: {}", workoutExercise);

        validateWorkoutExercise(workoutExercise);

        if (workoutExercise.getId() != null) {
            if (!workoutExerciseRepository.existsById(workoutExercise.getId())) {
                throw new ResourceNotFoundException(
                        String.format("Can't update workout exercise not found with ID: %d", workoutExercise.getId())
                );
            }
            log.info("Updating existing workout exercise ID: {}", workoutExercise.getId());
        } else {
            log.info("Creating a new workout exercise");
        }

        WorkoutExercise savedWorkoutExercise = workoutExerciseRepository.save(workoutExercise);
        log.info("Workout exercise saved successfully ID: {}", savedWorkoutExercise.getId());

        return savedWorkoutExercise;
    }

    public void delete(Long id) {
        if (!workoutExerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Workout exercise not found with ID: %d", id));
        }
        workoutExerciseRepository.deleteById(id);
    }

    private void validateWorkoutExercise(WorkoutExercise workoutExercise) {
        if (workoutExercise == null) {
            throw new BadRequestException("Workout exercise can't be null");
        }

        if (workoutExercise.getWorkout() == null) {
            throw new BadRequestException("Workout exercise must have a workout");
        }

        if (workoutExercise.getExercise() == null) {
            throw new BadRequestException("Workout exercise must have an exercise");
        }

        log.debug("Workout exercise validation performed successfully.");
    }
}