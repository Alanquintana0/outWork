package com.outworkit.outworkit.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.outworkit.outworkit.entity.MuscleGroup;
import com.outworkit.outworkit.service.MuscleGroupService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/muscle-groups")
@RequiredArgsConstructor
@Slf4j
public class MuscleGroupController {
    
    private final MuscleGroupService muscleGroupService;

    @GetMapping
    public ResponseEntity<List<MuscleGroup>> getAllMuscleGroups(){
        return ResponseEntity.ok(muscleGroupService.getAllMuscleGroups());
    }

    @GetMapping("/{muscleGroupId}")
    public ResponseEntity<MuscleGroup> getMuscleGroupById(@RequestParam Long muscleGroupId){
        return ResponseEntity.ok(muscleGroupService.getMuscleGroupById(muscleGroupId));
    }

}
