package com.outworkit.outworkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for summarizing workout performance
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSummaryDTO {
    private Long workoutId;
    private LocalDateTime workoutDate;
    private String splitName;
    private Integer totalExercises;
    private Integer totalSets;
    private Integer totalReps;
    private Double totalVolume; // Total weight lifted (sum of weight * reps)
    private Integer duration; // in minutes (if tracked)
    private List<ExerciseSummary> exercises;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseSummary {
        private Long exerciseId;
        private String exerciseName;
        private String muscleGroup;
        private Integer sets;
        private Integer totalReps;
        private Double maxWeight;
        private Double averageWeight;
        private Double volume;
        private Boolean isPersonalRecord;
    }
}
