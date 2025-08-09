package com.outworkit.outworkit.repository;

import com.outworkit.outworkit.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findByNameIgnoreCase(String name);
    List<Exercise> findByNameContainingIgnoreCase(String name);
}
