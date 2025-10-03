package com.outworkit.outworkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for representing personal records for a specific exercise
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordDTO {
    private Long exerciseId;
    private String exerciseName;
    private Double maxWeight;
    private Integer repsAtMaxWeight;
    private LocalDateTime achievedDate;
    private Long workoutId;
    private Double oneRepMax; // Calculated 1RM using Epley formula
    private Integer totalVolume; // Total reps across all sets at this weight
}
