package com.outworkit.outworkit.service;

import com.outworkit.outworkit.controller.exception.BadRequestException;
import com.outworkit.outworkit.controller.exception.ResourceNotFoundException;
import com.outworkit.outworkit.entity.Muscle;
import com.outworkit.outworkit.repository.MuscleRepository;
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
public class MuscleService {
    
    private final MuscleRepository muscleRepository;

    public List<Muscle> getAllMuscles() {
        try {
            log.debug("Getting muscles");
            List<Muscle> muscles = muscleRepository.findAll();
            log.info("Muscles retrieved");
            return muscles;
        } catch (Exception e) {
            log.error("Error retrieving muscles", e);
            throw e;
        }
    }

    public Muscle getMuscleById(Long id) {
        log.debug("Looking for muscle with ID: {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        return muscleRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Muscle not found with id {}", id);
                    return new ResourceNotFoundException(
                            String.format("Muscle not found with ID: %d", id)
                    );
                });
    }

    public Optional<Muscle> findMuscleById(Long muscleId) {
        log.debug("Searching for muscle with ID: {}", muscleId);

        if (muscleId == null) {
            return Optional.empty();
        }

        return muscleRepository.findById(muscleId);
    }

    @Transactional
    public Muscle saveOrUpdate(Muscle muscle) {
        log.debug("Saving/updating muscle: {}", muscle);

        validateMuscle(muscle);

        if (muscle.getId() != null) {
            if (!muscleRepository.existsById(muscle.getId())) {
                throw new ResourceNotFoundException(
                        String.format("Can't update muscle not found with ID: %d", muscle.getId())
                );
            }
            log.info("Updating existing muscle ID: {}", muscle.getId());
        } else {
            log.info("Creating a new muscle: {}", muscle.getName());
        }

        Muscle savedMuscle = muscleRepository.save(muscle);
        log.info("Muscle saved successfully ID: {}", savedMuscle.getId());

        return savedMuscle;
    }

    public void delete(Long id) {
        if (!muscleRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Muscle not found with ID: %d", id));
        }
        muscleRepository.deleteById(id);
    }

    private void validateMuscle(Muscle muscle) {
        if (muscle == null) {
            throw new BadRequestException("Muscle can't be null");
        }

        if (muscle.getName() == null || muscle.getName().trim().isEmpty()) {
            throw new BadRequestException("Muscle name is required");
        }

        if (muscle.getName().trim().length() > 255) {
            throw new BadRequestException("Muscle name can't exceed 255 characters");
        }

        log.debug("Muscle validation performed successfully.");
    }
}
