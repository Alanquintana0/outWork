package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.Exercise;
import com.outworkit.outworkit.repository.ExerciseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ExerciseService {

    ExerciseRepository exerciseRepository;

    public List<Exercise> getExercises(){
        List<Exercise> exercises = exerciseRepository.findAll();
        return exercises;
    }

    public Exercise getExercise(Long id){
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return exerciseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Equipment not found with id {}", id);
                    return new ResourceNotFoundException(
                            String.format("Exercise not found with ID: %d", id)
                    );
                });
    }

    public Optional<Exercise> findExerciseById(Long exerciseId){
        log.debug("Seaching for exercise with id: {}", exerciseId);

        if(exerciseId == null){
            return Optional.empty();
        }

        return exerciseRepository.findById(exerciseId);
    }

    @Transactional
    public Exercise saveOrUpdate(Exercise exercise){
        log.debug("Saving/updating exercise: {}", exercise);

        validateExercise(exercise);

        if(exercise.getId() != null){
            if(!exerciseRepository.existsById(exercise.getId())){
                throw new ResourceNotFoundException(
                        String.format("Cant update exercise not found with ID: %d", exercise.getId())
                );
            }
        }else{
            log.info("Creating a new exercise: {}", exercise.getName());
        }

        Exercise savedExercise = exerciseRepository.save(exercise);
        log.info("Exercise saved succesfully with ID: {}", savedExercise.getId());
        return savedExercise;
    }

    public void delete(Long id){
        if(!exerciseRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format("Exercise not found with ID: %d", id));
        }

        exerciseRepository.deleteById(id);
    }

    private void validateExercise(Exercise exercise){
        if(exercise == null){
            throw new BadRequestException("Exercise cant be null");
        }

        if(exercise.getName() == null || exercise.getName().trim().isEmpty()){
            throw new BadRequestException("Exercise name is required");
        }

        if (exercise.getName().trim().length() > 255) {
            throw new BadRequestException("equipment name cant exceed 255");
        }

        if(exercise.getEquipment() == null || exercise.getEquipment().getId() == null){
            throw new BadRequestException("Exercise equipment is required");
        }

        if(exercise.getMuscleGroup() == null || exercise.getMuscleGroup().getId() == null){
            throw new BadRequestException("Exercise muscle group is required");
        }

        Optional<Exercise> existingEquipment = exerciseRepository.findByNameIgnoreCase(exercise.getName().trim());
        if (existingEquipment.isPresent() &&
                !existingEquipment.get().getId().equals(exercise.getId())) {
            throw new BadRequestException(
                    String.format("Theres already an equipment with that name: %s", exercise.getName())
            );
        }

        log.debug("equipment validation performed successfully.");
    }
}
