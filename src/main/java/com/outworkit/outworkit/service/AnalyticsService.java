package com.outworkit.outworkit.service;

import com.outworkit.outworkit.dto.*;
import com.outworkit.outworkit.entity.*;
import com.outworkit.outworkit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for generating analytics and progress tracking data
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseResultRepository exerciseResultRepository;
    private final UserRepository userRepository;

    /**
     * Get personal records for a user across all exercises
     */
    public List<PersonalRecordDTO> getUserPersonalRecords(Long userId) {
        log.info("Fetching personal records for user: {}", userId);
        
        List<Workout> workouts = workoutRepository.findByUserId(userId);
        Map<Long, PersonalRecordDTO> exercisePRs = new HashMap<>();

        for (Workout workout : workouts) {
            for (WorkoutExercise we : workout.getWorkoutExercises()) {
                Long exerciseId = we.getExercise().getId();
                String exerciseName = we.getExercise().getName();

                for (ExerciseResult result : we.getResults()) {
                    double weight = result.getWeight();
                    int reps = result.getReps();
                    double oneRepMax = calculateOneRepMax(weight, reps);

                    PersonalRecordDTO existingPR = exercisePRs.get(exerciseId);

                    if (existingPR == null || weight > existingPR.getMaxWeight()) {
                        exercisePRs.put(exerciseId, PersonalRecordDTO.builder()
                                .exerciseId(exerciseId)
                                .exerciseName(exerciseName)
                                .maxWeight(weight)
                                .repsAtMaxWeight(reps)
                                .achievedDate(workout.getCreatedAt())
                                .workoutId(workout.getId())
                                .oneRepMax(oneRepMax)
                                .totalVolume(reps)
                                .build());
                    }
                }
            }
        }

        return new ArrayList<>(exercisePRs.values());
    }

    /**
     * Get personal record for a specific exercise
     */
    public PersonalRecordDTO getExercisePersonalRecord(Long userId, Long exerciseId) {
        log.info("Fetching personal record for user: {} and exercise: {}", userId, exerciseId);
        
        List<Workout> workouts = workoutRepository.findByUserId(userId);
        PersonalRecordDTO pr = null;

        for (Workout workout : workouts) {
            for (WorkoutExercise we : workout.getWorkoutExercises()) {
                if (we.getExercise().getId().equals(exerciseId)) {
                    for (ExerciseResult result : we.getResults()) {
                        double weight = result.getWeight();
                        int reps = result.getReps();

                        if (pr == null || weight > pr.getMaxWeight()) {
                            pr = PersonalRecordDTO.builder()
                                    .exerciseId(exerciseId)
                                    .exerciseName(we.getExercise().getName())
                                    .maxWeight(weight)
                                    .repsAtMaxWeight(reps)
                                    .achievedDate(workout.getCreatedAt())
                                    .workoutId(workout.getId())
                                    .oneRepMax(calculateOneRepMax(weight, reps))
                                    .build();
                        }
                    }
                }
            }
        }

        return pr;
    }

    /**
     * Get progress data for a specific exercise over time
     */
    public ExerciseProgressDTO getExerciseProgress(Long userId, Long exerciseId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching exercise progress for user: {}, exercise: {} from {} to {}", 
                userId, exerciseId, startDate, endDate);

        List<Workout> workouts = workoutRepository.findByUserId(userId);
        List<ExerciseProgressDTO.ProgressDataPoint> dataPoints = new ArrayList<>();
        
        LocalDateTime firstDate = null;
        LocalDateTime lastDate = null;
        double firstMaxWeight = 0;
        double lastMaxWeight = 0;
        String exerciseName = null;

        for (Workout workout : workouts) {
            LocalDateTime workoutDate = workout.getCreatedAt();
            
            // Filter by date range
            if ((startDate != null && workoutDate.isBefore(startDate)) ||
                (endDate != null && workoutDate.isAfter(endDate))) {
                continue;
            }

            for (WorkoutExercise we : workout.getWorkoutExercises()) {
                if (we.getExercise().getId().equals(exerciseId)) {
                    exerciseName = we.getExercise().getName();
                    List<ExerciseResult> results = we.getResults();

                    if (!results.isEmpty()) {
                        double totalWeight = 0;
                        double maxWeight = 0;
                        int totalReps = 0;
                        int totalSets = results.size();
                        double totalVolume = 0;

                        for (ExerciseResult result : results) {
                            totalWeight += result.getWeight();
                            maxWeight = Math.max(maxWeight, result.getWeight());
                            totalReps += result.getReps();
                            totalVolume += result.getWeight() * result.getReps();
                        }

                        double avgWeight = totalWeight / totalSets;
                        final double finalMaxWeight = maxWeight;
                        double estimatedOneRepMax = calculateOneRepMax(maxWeight, 
                                results.stream().filter(r -> r.getWeight() == finalMaxWeight)
                                        .findFirst().map(ExerciseResult::getReps).orElse(1));

                        dataPoints.add(ExerciseProgressDTO.ProgressDataPoint.builder()
                                .date(workoutDate)
                                .workoutId(workout.getId())
                                .averageWeight(avgWeight)
                                .maxWeight(maxWeight)
                                .totalReps(totalReps)
                                .totalSets(totalSets)
                                .totalVolume(totalVolume)
                                .estimatedOneRepMax(estimatedOneRepMax)
                                .build());

                        if (firstDate == null) {
                            firstDate = workoutDate;
                            firstMaxWeight = maxWeight;
                        }
                        lastDate = workoutDate;
                        lastMaxWeight = maxWeight;
                    }
                }
            }
        }

        // Sort by date
        dataPoints.sort(Comparator.comparing(ExerciseProgressDTO.ProgressDataPoint::getDate));

        // Calculate stats
        ExerciseProgressDTO.ProgressStats stats = null;
        if (!dataPoints.isEmpty() && firstMaxWeight > 0) {
            double weightIncrease = ((lastMaxWeight - firstMaxWeight) / firstMaxWeight) * 100;
            double firstVolume = dataPoints.get(0).getTotalVolume();
            double lastVolume = dataPoints.get(dataPoints.size() - 1).getTotalVolume();
            double volumeIncrease = firstVolume > 0 ? ((lastVolume - firstVolume) / firstVolume) * 100 : 0;
            
            long daysBetween = ChronoUnit.DAYS.between(firstDate, lastDate);
            double avgProgression = daysBetween > 0 ? (lastMaxWeight - firstMaxWeight) / dataPoints.size() : 0;

            stats = ExerciseProgressDTO.ProgressStats.builder()
                    .weightIncreasePercentage(weightIncrease)
                    .volumeIncreasePercentage(volumeIncrease)
                    .totalWorkouts(dataPoints.size())
                    .firstWorkout(firstDate)
                    .lastWorkout(lastDate)
                    .averageWeightProgression(avgProgression)
                    .build();
        }

        PersonalRecordDTO currentPR = getExercisePersonalRecord(userId, exerciseId);

        return ExerciseProgressDTO.builder()
                .exerciseId(exerciseId)
                .exerciseName(exerciseName != null ? exerciseName : "Unknown")
                .progressData(dataPoints)
                .currentPR(currentPR)
                .stats(stats)
                .build();
    }

    /**
     * Get summary for a specific workout
     */
    public WorkoutSummaryDTO getWorkoutSummary(Long workoutId) {
        log.info("Generating summary for workout: {}", workoutId);
        
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found: " + workoutId));

        int totalSets = 0;
        int totalReps = 0;
        double totalVolume = 0;
        List<WorkoutSummaryDTO.ExerciseSummary> exerciseSummaries = new ArrayList<>();

        for (WorkoutExercise we : workout.getWorkoutExercises()) {
            Exercise exercise = we.getExercise();
            List<ExerciseResult> results = we.getResults();

            int exerciseSets = results.size();
            int exerciseTotalReps = 0;
            double maxWeight = 0;
            double totalWeight = 0;
            double exerciseVolume = 0;

            for (ExerciseResult result : results) {
                exerciseTotalReps += result.getReps();
                maxWeight = Math.max(maxWeight, result.getWeight());
                totalWeight += result.getWeight();
                exerciseVolume += result.getWeight() * result.getReps();
            }

            totalSets += exerciseSets;
            totalReps += exerciseTotalReps;
            totalVolume += exerciseVolume;

            double avgWeight = exerciseSets > 0 ? totalWeight / exerciseSets : 0;

            // Check if this is a personal record
            PersonalRecordDTO pr = getExercisePersonalRecord(workout.getUser().getId(), exercise.getId());
            boolean isPR = pr != null && pr.getWorkoutId().equals(workoutId);

            exerciseSummaries.add(WorkoutSummaryDTO.ExerciseSummary.builder()
                    .exerciseId(exercise.getId())
                    .exerciseName(exercise.getName())
                    .muscleGroup(exercise.getMuscleGroup() != null ? exercise.getMuscleGroup().getName() : "Unknown")
                    .sets(exerciseSets)
                    .totalReps(exerciseTotalReps)
                    .maxWeight(maxWeight)
                    .averageWeight(avgWeight)
                    .volume(exerciseVolume)
                    .isPersonalRecord(isPR)
                    .build());
        }

        return WorkoutSummaryDTO.builder()
                .workoutId(workoutId)
                .workoutDate(workout.getCreatedAt())
                .splitName(workout.getSplit() != null ? workout.getSplit().getName() : "No Split")
                .totalExercises(workout.getWorkoutExercises().size())
                .totalSets(totalSets)
                .totalReps(totalReps)
                .totalVolume(totalVolume)
                .exercises(exerciseSummaries)
                .build();
    }

    /**
     * Get comprehensive user statistics
     */
    public UserStatsDTO getUserStats(Long userId) {
        log.info("Generating comprehensive stats for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<Workout> workouts = workoutRepository.findByUserId(userId);
        workouts.sort(Comparator.comparing(Workout::getCreatedAt));

        // Calculate overall stats
        int totalWorkouts = workouts.size();
        int totalExercises = 0;
        int totalSets = 0;
        int totalReps = 0;
        double totalVolume = 0;
        LocalDateTime firstWorkout = !workouts.isEmpty() ? workouts.get(0).getCreatedAt() : null;
        LocalDateTime lastWorkout = !workouts.isEmpty() ? workouts.get(workouts.size() - 1).getCreatedAt() : null;

        Map<String, UserStatsDTO.MuscleGroupStats> muscleGroupMap = new HashMap<>();
        Map<String, UserStatsDTO.WorkoutFrequency> weeklyFrequency = new LinkedHashMap<>();

        for (Workout workout : workouts) {
            // Weekly frequency
            String weekKey = workout.getCreatedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("YYYY-'W'ww"));
            
            UserStatsDTO.WorkoutFrequency freq = weeklyFrequency.getOrDefault(weekKey,
                    UserStatsDTO.WorkoutFrequency.builder()
                            .period(weekKey)
                            .workoutCount(0)
                            .totalVolume(0.0)
                            .build());
            
            double workoutVolume = 0;

            for (WorkoutExercise we : workout.getWorkoutExercises()) {
                totalExercises++;
                Exercise exercise = we.getExercise();
                String muscleGroupName = exercise.getMuscleGroup() != null ? 
                        exercise.getMuscleGroup().getName() : "Unknown";

                for (ExerciseResult result : we.getResults()) {
                    totalSets++;
                    totalReps += result.getReps();
                    double volume = result.getWeight() * result.getReps();
                    totalVolume += volume;
                    workoutVolume += volume;

                    // Update muscle group stats
                    UserStatsDTO.MuscleGroupStats mgStats = muscleGroupMap.getOrDefault(muscleGroupName,
                            UserStatsDTO.MuscleGroupStats.builder()
                                    .muscleGroupName(muscleGroupName)
                                    .timesWorked(0)
                                    .totalVolume(0.0)
                                    .totalSets(0)
                                    .lastWorked(workout.getCreatedAt())
                                    .build());
                    
                    mgStats.setTimesWorked(mgStats.getTimesWorked() + 1);
                    mgStats.setTotalVolume(mgStats.getTotalVolume() + volume);
                    mgStats.setTotalSets(mgStats.getTotalSets() + 1);
                    mgStats.setLastWorked(workout.getCreatedAt());
                    muscleGroupMap.put(muscleGroupName, mgStats);
                }
            }

            freq.setWorkoutCount(freq.getWorkoutCount() + 1);
            freq.setTotalVolume(freq.getTotalVolume() + workoutVolume);
            weeklyFrequency.put(weekKey, freq);
        }

        // Calculate streaks
        int currentStreak = calculateCurrentStreak(workouts);
        int longestStreak = calculateLongestStreak(workouts);

        // Calculate average workouts per week
        double avgWorkoutsPerWeek = 0;
        if (firstWorkout != null && lastWorkout != null) {
            long weeksBetween = ChronoUnit.WEEKS.between(firstWorkout, lastWorkout);
            avgWorkoutsPerWeek = weeksBetween > 0 ? (double) totalWorkouts / weeksBetween : totalWorkouts;
        }

        UserStatsDTO.OverallStats overallStats = UserStatsDTO.OverallStats.builder()
                .totalWorkouts(totalWorkouts)
                .totalExercises(totalExercises)
                .totalSets(totalSets)
                .totalReps(totalReps)
                .totalVolumeLifted(totalVolume)
                .firstWorkout(firstWorkout)
                .lastWorkout(lastWorkout)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .averageWorkoutsPerWeek(avgWorkoutsPerWeek)
                .build();

        // Get top 10 personal records
        List<PersonalRecordDTO> topPRs = getUserPersonalRecords(userId).stream()
                .sorted(Comparator.comparing(PersonalRecordDTO::getOneRepMax).reversed())
                .limit(10)
                .collect(Collectors.toList());

        return UserStatsDTO.builder()
                .userId(userId)
                .userName(user.getUserName())
                .overall(overallStats)
                .topPersonalRecords(topPRs)
                .muscleGroupBreakdown(muscleGroupMap)
                .workoutFrequency(new ArrayList<>(weeklyFrequency.values()))
                .build();
    }

    /**
     * Get volume progress over time
     */
    public VolumeProgressDTO getVolumeProgress(Long userId, String period, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching volume progress for user: {} with period: {}", userId, period);
        
        List<Workout> workouts = workoutRepository.findByUserId(userId);
        Map<String, VolumeProgressDTO.VolumeDataPoint> dataPointMap = new LinkedHashMap<>();

        for (Workout workout : workouts) {
            LocalDateTime workoutDate = workout.getCreatedAt();
            
            // Filter by date range
            if ((startDate != null && workoutDate.isBefore(startDate)) ||
                (endDate != null && workoutDate.isAfter(endDate))) {
                continue;
            }

            String label = formatPeriodLabel(workoutDate, period);

            double workoutVolume = 0;
            int workoutSets = 0;
            int workoutReps = 0;

            for (WorkoutExercise we : workout.getWorkoutExercises()) {
                for (ExerciseResult result : we.getResults()) {
                    workoutVolume += result.getWeight() * result.getReps();
                    workoutSets++;
                    workoutReps += result.getReps();
                }
            }

            VolumeProgressDTO.VolumeDataPoint dataPoint = dataPointMap.getOrDefault(label,
                    VolumeProgressDTO.VolumeDataPoint.builder()
                            .date(workoutDate)
                            .label(label)
                            .totalVolume(0.0)
                            .totalWorkouts(0)
                            .totalSets(0)
                            .totalReps(0)
                            .averageVolumePerWorkout(0.0)
                            .build());

            dataPoint.setTotalVolume(dataPoint.getTotalVolume() + workoutVolume);
            dataPoint.setTotalWorkouts(dataPoint.getTotalWorkouts() + 1);
            dataPoint.setTotalSets(dataPoint.getTotalSets() + workoutSets);
            dataPoint.setTotalReps(dataPoint.getTotalReps() + workoutReps);
            dataPoint.setAverageVolumePerWorkout(dataPoint.getTotalVolume() / dataPoint.getTotalWorkouts());

            dataPointMap.put(label, dataPoint);
        }

        List<VolumeProgressDTO.VolumeDataPoint> dataPoints = new ArrayList<>(dataPointMap.values());
        
        // Calculate stats
        VolumeProgressDTO.VolumeStats stats = calculateVolumeStats(dataPoints);

        return VolumeProgressDTO.builder()
                .period(period)
                .dataPoints(dataPoints)
                .stats(stats)
                .build();
    }

    // Helper methods

    private double calculateOneRepMax(double weight, int reps) {
        if (reps == 1) return weight;
        // Epley formula: 1RM = weight * (1 + reps/30)
        return weight * (1 + reps / 30.0);
    }

    private int calculateCurrentStreak(List<Workout> workouts) {
        if (workouts.isEmpty()) return 0;

        workouts.sort(Comparator.comparing(Workout::getCreatedAt).reversed());
        
        int streak = 0;
        LocalDateTime currentDate = LocalDateTime.now();
        Set<String> workoutDates = workouts.stream()
                .map(w -> w.getCreatedAt().toLocalDate().toString())
                .collect(Collectors.toSet());

        while (workoutDates.contains(currentDate.toLocalDate().toString()) ||
               workoutDates.contains(currentDate.minusDays(1).toLocalDate().toString())) {
            if (workoutDates.contains(currentDate.toLocalDate().toString())) {
                streak++;
            }
            currentDate = currentDate.minusDays(1);
        }

        return streak;
    }

    private int calculateLongestStreak(List<Workout> workouts) {
        if (workouts.isEmpty()) return 0;

        Set<String> workoutDates = workouts.stream()
                .map(w -> w.getCreatedAt().toLocalDate().toString())
                .collect(Collectors.toSet());

        List<String> sortedDates = new ArrayList<>(workoutDates);
        Collections.sort(sortedDates);

        int longestStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < sortedDates.size(); i++) {
            LocalDateTime prevDate = LocalDateTime.parse(sortedDates.get(i - 1) + "T00:00:00");
            LocalDateTime currDate = LocalDateTime.parse(sortedDates.get(i) + "T00:00:00");

            if (ChronoUnit.DAYS.between(prevDate, currDate) == 1) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return longestStreak;
    }

    private String formatPeriodLabel(LocalDateTime date, String period) {
        switch (period.toLowerCase()) {
            case "weekly":
                return date.format(java.time.format.DateTimeFormatter.ofPattern("YYYY-'W'ww"));
            case "monthly":
                return date.format(java.time.format.DateTimeFormatter.ofPattern("YYYY-MM"));
            case "yearly":
                return date.format(java.time.format.DateTimeFormatter.ofPattern("YYYY"));
            default:
                return date.toLocalDate().toString();
        }
    }

    private VolumeProgressDTO.VolumeStats calculateVolumeStats(List<VolumeProgressDTO.VolumeDataPoint> dataPoints) {
        if (dataPoints.isEmpty()) {
            return VolumeProgressDTO.VolumeStats.builder().build();
        }

        double totalVolume = dataPoints.stream().mapToDouble(VolumeProgressDTO.VolumeDataPoint::getTotalVolume).sum();
        double avgVolume = totalVolume / dataPoints.size();
        
        VolumeProgressDTO.VolumeDataPoint peakPoint = dataPoints.stream()
                .max(Comparator.comparing(VolumeProgressDTO.VolumeDataPoint::getTotalVolume))
                .orElse(null);

        // Calculate trend (simple linear regression slope)
        double trend = 0;
        if (dataPoints.size() > 1) {
            double firstVolume = dataPoints.get(0).getTotalVolume();
            double lastVolume = dataPoints.get(dataPoints.size() - 1).getTotalVolume();
            trend = ((lastVolume - firstVolume) / firstVolume) * 100;
        }

        return VolumeProgressDTO.VolumeStats.builder()
                .averageVolume(avgVolume)
                .peakVolume(peakPoint != null ? peakPoint.getTotalVolume() : 0)
                .peakVolumeDate(peakPoint != null ? peakPoint.getDate() : null)
                .volumeTrend(trend)
                .totalVolumeInPeriod(totalVolume)
                .build();
    }
}
