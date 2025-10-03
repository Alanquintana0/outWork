package com.outworkit.outworkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for tracking exercise progress over time
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseProgressDTO {
    private Long exerciseId;
    private String exerciseName;
    private List<ProgressDataPoint> progressData;
    private PersonalRecordDTO currentPR;
    private ProgressStats stats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressDataPoint {
        private LocalDateTime date;
        private Long workoutId;
        private Double averageWeight;
        private Double maxWeight;
        private Integer totalReps;
        private Integer totalSets;
        private Double totalVolume; // weight * reps * sets
        private Double estimatedOneRepMax;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressStats {
        private Double weightIncreasePercentage;
        private Double volumeIncreasePercentage;
        private Integer totalWorkouts;
        private LocalDateTime firstWorkout;
        private LocalDateTime lastWorkout;
        private Double averageWeightProgression; // kg or lbs per workout
    }
}
