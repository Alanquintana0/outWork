package com.outworkit.outworkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for tracking volume progression over time
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolumeProgressDTO {
    private String period; // "weekly", "monthly", "yearly"
    private List<VolumeDataPoint> dataPoints;
    private VolumeStats stats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeDataPoint {
        private LocalDateTime date;
        private String label; // e.g., "Week 1", "January"
        private Double totalVolume;
        private Integer totalWorkouts;
        private Integer totalSets;
        private Integer totalReps;
        private Double averageVolumePerWorkout;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeStats {
        private Double averageVolume;
        private Double peakVolume;
        private LocalDateTime peakVolumeDate;
        private Double volumeTrend; // positive or negative trend
        private Double totalVolumeInPeriod;
    }
}
