package com.outworkit.outworkit.controller;


import com.outworkit.outworkit.entity.ExerciseResult;
import com.outworkit.outworkit.service.ExerciseResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/exercise-results")
@RequiredArgsConstructor
@Slf4j
public class ExerciseResultController {
    private final ExerciseResultService exerciseResultService;

    @GetMapping
    public ResponseEntity<List<ExerciseResult>> getAllExerciseResults(){
        log.info("Requesting exercise results");
        List<ExerciseResult> exerciseResults = exerciseResultService.getExerciseResults();
        return ResponseEntity.ok(exerciseResults);
    }

    @GetMapping("/{exerciseResultId}")
    public ResponseEntity<ExerciseResult> getExerciseResult(@PathVariable Long exerciseResultId){
        log.info("Retrieving exercise result with Id: {}", exerciseResultId);
        ExerciseResult exerciseResult = exerciseResultService.getExerciseResult(exerciseResultId);
        return ResponseEntity.ok(exerciseResult);
    }

    @PostMapping
    public ResponseEntity<ExerciseResult> createExerciseResult(@Valid @RequestBody ExerciseResult exerciseResult){
        log.info("Creating new exercise result with Id: {}", exerciseResult.getId());
        ExerciseResult saveExerciseResult = exerciseResultService.saveOrUpdate(exerciseResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveExerciseResult);
    }

    @PutMapping("/{exerciseResultId}")
    public ResponseEntity<ExerciseResult> updateExcerciseResult(@PathVariable Long exerciseResultId, @Valid @RequestBody ExerciseResult exerciseResult){
        log.info("Updating exercise result with id: {}", exerciseResultId);
        exerciseResult.setId(exerciseResultId);
        ExerciseResult updateExerciseResult = exerciseResultService.saveOrUpdate(exerciseResult);
        return ResponseEntity.ok(updateExerciseResult);
    }

    @DeleteMapping("/{exerciseResulltId}")
    public ResponseEntity<Void> deleteExerciseResult(@PathVariable Long exerciseResultId){
        log.info("Deleting exercise result with id: {}", exerciseResultId);
        exerciseResultService.delete(exerciseResultId);
        return ResponseEntity.noContent().build();
    }
}
