package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.ExerciseResult;
import com.outworkit.outworkit.repository.ExerciseResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ExerciseResultService {

    ExerciseResultRepository exerciseResultRepository;

    public List<ExerciseResult> getExerciseResults(){
        List<ExerciseResult> exerciseResults = exerciseResultRepository.findAll();
        return exerciseResults;
    }

    public ExerciseResult getExerciseResult(Long id){
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        return exerciseResultRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("ExerciseResult not found with id {}", id);
                    return new RuntimeException(
                            String.format("ExerciseResult not found with ID: %d", id)
                    );
                });
    }

    @Transactional
    public ExerciseResult saveOrUpdate(ExerciseResult exerciseResult) {
        log.debug("Saving/updating exerciseResult: {}", exerciseResult);

        validateExerciseResult(exerciseResult);

        if(exerciseResult.getId() != null){
            if(!exerciseResultRepository.existsById(exerciseResult.getId())){
                throw new BadRequestException(
                        String.format("Cannot update ExerciseResult. ExerciseResult with ID %d does not exist.", exerciseResult.getId())
                );
            }
        }else {
            log.info("Creating new ExerciseResult: {}", exerciseResult);
        }

        ExerciseResult savedExerciseResult = exerciseResultRepository.save(exerciseResult);

        log.info("ExerciseResult saved/updated successfully: {}", savedExerciseResult);
        return savedExerciseResult;

    }

    public void delete(Long id){
        if(!exerciseResultRepository.existsById(id)){
            throw new ResourceNotFoundException(String.format("Exercise result not found with ID: %d", id));
        }
        exerciseResultRepository.deleteById(id);
    }


    private void validateExerciseResult(ExerciseResult exerciseResult){
        if(exerciseResult == null){
            throw new BadRequestException("ExerciseResult cannot be null");
        }
    }
}
