package com.outworkit.outworkit.service;

import org.springframework.stereotype.Service;

import com.outworkit.outworkit.repository.MuscleGroupRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.MuscleGroup;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MuscleGroupService {
    
    MuscleGroupRepository muscleGroupRepository;

    public List<MuscleGroup> getAllMuscleGroups(){
        try{
            return muscleGroupRepository.findAll();
        }catch(Exception e){
            log.error("Error retrieving muscle groups", e);
            throw e;
        }
    }

    public MuscleGroup getMuscleGroupById(Long id){
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return muscleGroupRepository.findById(id).orElseThrow(() -> {
            log.warn("Muscle group not found with id {}", id);
            return new ResourceNotFoundException(
                    String.format("Muscle group not found with ID: %d", id)
            );
        });
    }


}
