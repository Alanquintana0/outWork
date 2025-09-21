package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.entity.Muscle;
import com.outworkit.outworkit.service.MuscleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/muscles")
@RequiredArgsConstructor
@Slf4j
public class MuscleController {
    
    private final MuscleService muscleService;

    @GetMapping
    public ResponseEntity<List<Muscle>> getAllMuscles() {
        log.info("Solicitando todos los músculos");
        List<Muscle> muscles = muscleService.getAllMuscles();
        return ResponseEntity.ok(muscles);
    }

    @GetMapping("/{muscleId}")
    public ResponseEntity<Muscle> getMuscleById(@PathVariable Long muscleId) {
        log.info("Solicitando músculo con ID: {}", muscleId);
        Muscle muscle = muscleService.getMuscleById(muscleId);
        return ResponseEntity.ok(muscle);
    }

    @PostMapping
    public ResponseEntity<Muscle> createMuscle(@Valid @RequestBody Muscle muscle) {
        // return 400 when the provided muscle is invalid
        if (!isValidMuscle(muscle)) {
            log.warn("Provide valid muscle data");
            return ResponseEntity.badRequest().build();
        }
        Muscle savedMuscle = muscleService.saveOrUpdate(muscle);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMuscle);
    }

    @PutMapping("/{muscleId}")
    public ResponseEntity<Muscle> updateMuscle(
            @PathVariable Long muscleId,
            @Valid @RequestBody Muscle muscle) {

        log.info("Actualizando músculo con ID: {}", muscleId);
        // ensure the path id is used
        muscle.setId(muscleId);

        // return 400 when the provided muscle is invalid
        if (!isValidMuscle(muscle)) {
            log.warn("Provide valid muscle data");
            return ResponseEntity.badRequest().build();
        }

        // Delegate further validation/error handling to the service and global exception handler.
        Muscle updatedMuscle = muscleService.saveOrUpdate(muscle);
        return ResponseEntity.ok(updatedMuscle);
    }

    @DeleteMapping("/{muscleId}")
    public ResponseEntity<Void> deleteMuscle(@PathVariable Long muscleId) {
        log.info("Eliminando músculo con ID: {}", muscleId);
        muscleService.delete(muscleId);
        return ResponseEntity.noContent().build();
    }

    private boolean isValidMuscle(Muscle muscle) {
        if (muscle == null) {
            log.warn("Muscle is null");
            return false;
        }
        // Treat missing fields (null) as invalid, but allow blank strings so the
        // service can apply business-level validation and throw custom exceptions.
        if (muscle.getName() == null) {
            log.warn("Muscle name is null");
            return false;
        }
        return true;
    }
}
