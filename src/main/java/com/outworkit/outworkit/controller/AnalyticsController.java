package com.outworkit.outworkit.controller;

import com.outworkit.outworkit.dto.*;
import com.outworkit.outworkit.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for analytics and progress tracking endpoints
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Get all personal records for a user
     * GET /api/v1/analytics/users/{userId}/personal-records
     */
    @GetMapping("/users/{userId}/personal-records")
    public ResponseEntity<List<PersonalRecordDTO>> getUserPersonalRecords(@PathVariable Long userId) {
        log.info("Request to get personal records for user: {}", userId);
        List<PersonalRecordDTO> records = analyticsService.getUserPersonalRecords(userId);
        return ResponseEntity.ok(records);
    }

    /**
     * Get personal record for a specific exercise
     * GET /api/v1/analytics/users/{userId}/exercises/{exerciseId}/personal-record
     */
    @GetMapping("/users/{userId}/exercises/{exerciseId}/personal-record")
    public ResponseEntity<PersonalRecordDTO> getExercisePersonalRecord(
            @PathVariable Long userId,
            @PathVariable Long exerciseId) {
        log.info("Request to get personal record for user: {} and exercise: {}", userId, exerciseId);
        PersonalRecordDTO record = analyticsService.getExercisePersonalRecord(userId, exerciseId);
        
        if (record == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(record);
    }

    /**
     * Get progress data for a specific exercise
     * GET /api/v1/analytics/users/{userId}/exercises/{exerciseId}/progress
     * Optional query params: startDate, endDate (format: yyyy-MM-dd'T'HH:mm:ss)
     */
    @GetMapping("/users/{userId}/exercises/{exerciseId}/progress")
    public ResponseEntity<ExerciseProgressDTO> getExerciseProgress(
            @PathVariable Long userId,
            @PathVariable Long exerciseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Request to get exercise progress for user: {}, exercise: {}, from: {}, to: {}", 
                userId, exerciseId, startDate, endDate);
        
        ExerciseProgressDTO progress = analyticsService.getExerciseProgress(userId, exerciseId, startDate, endDate);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get summary for a specific workout
     * GET /api/v1/analytics/workouts/{workoutId}/summary
     */
    @GetMapping("/workouts/{workoutId}/summary")
    public ResponseEntity<WorkoutSummaryDTO> getWorkoutSummary(@PathVariable Long workoutId) {
        log.info("Request to get workout summary for workout: {}", workoutId);
        WorkoutSummaryDTO summary = analyticsService.getWorkoutSummary(workoutId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get comprehensive user statistics
     * GET /api/v1/analytics/users/{userId}/stats
     */
    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<UserStatsDTO> getUserStats(@PathVariable Long userId) {
        log.info("Request to get comprehensive stats for user: {}", userId);
        UserStatsDTO stats = analyticsService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get volume progress over time
     * GET /api/v1/analytics/users/{userId}/volume-progress
     * Query params: 
     *   - period: "weekly", "monthly", "yearly" (default: "weekly")
     *   - startDate, endDate: optional date filters (format: yyyy-MM-dd'T'HH:mm:ss)
     */
    @GetMapping("/users/{userId}/volume-progress")
    public ResponseEntity<VolumeProgressDTO> getVolumeProgress(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "weekly") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Request to get volume progress for user: {}, period: {}, from: {}, to: {}", 
                userId, period, startDate, endDate);
        
        VolumeProgressDTO progress = analyticsService.getVolumeProgress(userId, period, startDate, endDate);
        return ResponseEntity.ok(progress);
    }

    /**
     * Get workout summaries for multiple workouts
     * GET /api/v1/analytics/users/{userId}/workout-summaries
     * Optional query params: startDate, endDate
     */
    @GetMapping("/users/{userId}/workout-summaries")
    public ResponseEntity<List<WorkoutSummaryDTO>> getUserWorkoutSummaries(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        log.info("Request to get workout summaries for user: {}, from: {}, to: {}", userId, startDate, endDate);
        
        // This would need additional implementation in the service
        // For now, return a not implemented response or implement if needed
        return ResponseEntity.status(501).build(); // Not Implemented
    }
}
