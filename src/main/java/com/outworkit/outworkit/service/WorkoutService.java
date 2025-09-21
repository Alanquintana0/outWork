package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.Workout;
import com.outworkit.outworkit.repository.WorkoutRepository;
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
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public List<Workout> getWorkouts() {
        try {
            log.debug("Getting workouts");
            List<Workout> workouts = workoutRepository.findAll();
            log.info("Workouts retrieved");
            return workouts;
        } catch (Exception e) {
            log.error("Error retrieving workouts", e);
            throw e;
        }
    }

    public Workout getWorkout(Long id) {
        log.debug("Looking for workout with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        return workoutRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Workout not found with id {}", id);
                    return new ResourceNotFoundException(
                            String.format("Workout not found with ID: %d", id)
                    );
                });
    }

    public Optional<Workout> findWorkoutById(Long workoutId) {
        log.debug("Searching for workout with ID: {}", workoutId);

        if (workoutId == null) {
            return Optional.empty();
        }

        return workoutRepository.findById(workoutId);
    }

    @Transactional
    public Workout saveOrUpdate(Workout workout) {
        log.debug("Saving/updating workout: {}", workout);

        validateWorkout(workout);

        if (workout.getId() != null) {
            if (!workoutRepository.existsById(workout.getId())) {
                throw new ResourceNotFoundException(
                        String.format("Can't update workout not found with ID: %d", workout.getId())
                );
            }
            log.info("Updating existing workout ID: {}", workout.getId());
        } else {
            log.info("Creating a new workout");
        }

        Workout savedWorkout = workoutRepository.save(workout);
        log.info("Workout saved successfully ID: {}", savedWorkout.getId());

        return savedWorkout;
    }

    public void delete(Long id) {
        if (!workoutRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Workout not found with ID: %d", id));
        }
        workoutRepository.deleteById(id);
    }

    private void validateWorkout(Workout workout) {
        if (workout == null) {
            throw new BadRequestException("Workout can't be null");
        }

        if (workout.getUser() == null) {
            throw new BadRequestException("Workout must have a user");
        }

        log.debug("Workout validation performed successfully.");
    }
}