package com.outworkit.outworkit.service;

import org.springframework.stereotype.Service;

import com.outworkit.outworkit.repository.MuscleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.Muscle;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MuscleService {
    
    MuscleRepository muscleRepository;

    public List<Muscle> getAllMuscles(){
        try{
            return muscleRepository.findAll();
        }catch(Exception e){
            log.error("Error retrieving muscles", e);
            throw e;
        }
    }

    public Muscle getMuscleById(Long id){
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return muscleRepository.findById(id).orElseThrow(() -> {
            log.warn("Muscle not found with id {}", id);
            return new ResourceNotFoundException(
                    String.format("Muscle not found with ID: %d", id)
            );
        });
    }
}
