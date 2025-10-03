package com.outworkit.outworkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for comprehensive user statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private Long userId;
    private String userName;
    private OverallStats overall;
    private List<PersonalRecordDTO> topPersonalRecords;
    private Map<String, MuscleGroupStats> muscleGroupBreakdown;
    private List<WorkoutFrequency> workoutFrequency;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallStats {
        private Integer totalWorkouts;
        private Integer totalExercises;
        private Integer totalSets;
        private Integer totalReps;
        private Double totalVolumeLifted;
        private LocalDateTime firstWorkout;
        private LocalDateTime lastWorkout;
        private Integer currentStreak; // consecutive days with workouts
        private Integer longestStreak;
        private Double averageWorkoutsPerWeek;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MuscleGroupStats {
        private String muscleGroupName;
        private Integer timesWorked;
        private Double totalVolume;
        private Integer totalSets;
        private LocalDateTime lastWorked;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkoutFrequency {
        private String period; // e.g., "2024-W01", "2024-01"
        private Integer workoutCount;
        private Double totalVolume;
    }
}
