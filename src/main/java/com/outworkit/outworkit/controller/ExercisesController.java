package com.outworkit.outworkit.controller;


import com.outworkit.outworkit.entity.Exercise;
import com.outworkit.outworkit.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Slf4j
public class ExercisesController {

    private final ExerciseService exerciseService;


    @GetMapping
    public ResponseEntity<List<Exercise>> getAllExercises(){
        log.info("Requesting exercises");
        List<Exercise> exercises = exerciseService.getExercises();
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{exerciseId}")
    public ResponseEntity<Exercise> getExerciseById(@PathVariable Long exerciseId){
        log.info("Requesting exercise with Id: {}", exerciseId);
        Exercise exercise = exerciseService.getExercise(exerciseId);
        return ResponseEntity.ok(exercise);
    }

    @PostMapping
    public ResponseEntity<Exercise> createExercise(@Valid @RequestBody Exercise exercise){
        log.info("Creating new exercise with Id: {}", exercise.getId());
        Exercise saveExercise = exerciseService.saveOrUpdate(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveExercise);
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long exerciseId, @Valid @RequestBody Exercise exercise){
        log.info("Updating exercise with id: {}", exerciseId);
        exercise.setId(exerciseId);
        Exercise updateExercise = exerciseService.saveOrUpdate(exercise);
        return ResponseEntity.ok(updateExercise);
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long exerciseId){
        log.info("Deleting exercise with id: {}", exerciseId);
        exerciseService.delete(exerciseId);
        return ResponseEntity.noContent().build();
    }
}
