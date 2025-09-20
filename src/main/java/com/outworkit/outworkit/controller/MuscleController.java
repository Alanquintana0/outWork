package com.outworkit.outworkit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.outworkit.outworkit.entity.Muscle;
import com.outworkit.outworkit.service.MuscleService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/muscles")
@RequiredArgsConstructor
@Slf4j
public class MuscleController {
    
    private final MuscleService muscleService;

    @GetMapping
    public ResponseEntity<List<Muscle>> getAllMuscles() {
        return ResponseEntity.ok(muscleService.getAllMuscles());
    }

    @GetMapping("/{muscleId}")
    public ResponseEntity<Muscle> getMuscleById(@RequestParam Long muscleId){
        return ResponseEntity.ok(muscleService.getMuscleById(muscleId));
    }
    
}
