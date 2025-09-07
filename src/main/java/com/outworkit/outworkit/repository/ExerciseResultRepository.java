package com.outworkit.outworkit.repository;

import com.outworkit.outworkit.entity.ExerciseResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseResultRepository extends JpaRepository<ExerciseResult, Long> {
}
