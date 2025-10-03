# OutWorkIt Analytics API Documentation

## 🎯 Overview

This document describes the **Analytics & Progress Tracking** features added to OutWorkIt. These features enable comprehensive workout tracking, personal record monitoring, and visual progress representation for fitness enthusiasts.

## 🚀 New Features

### 1. **Personal Records (PRs) Tracking**
- Track maximum weight lifted for each exercise
- Calculate estimated 1-Rep Max using the Epley formula
- View when and where PRs were achieved
- See top 10 personal records across all exercises

### 2. **Exercise Progress Over Time**
- Visualize strength gains and volume increases
- Track average and max weights per workout
- Calculate progress percentages and trends
- View workout-by-workout progression

### 3. **Workout Summaries**
- Comprehensive breakdown of each workout session
- Total volume, sets, reps per workout
- Exercise-by-exercise performance
- Automatic PR detection

### 4. **User Statistics Dashboard**
- Overall training statistics (lifetime totals)
- Muscle group breakdown and balance analysis
- Workout frequency and consistency tracking
- Streak tracking (current and longest)
- Weekly/monthly workout frequency

### 5. **Volume Progression Analysis**
- Track total training volume over time
- Weekly, monthly, or yearly aggregation
- Volume trends and peak performance periods
- Average volume per workout

---

## 📊 API Endpoints

### Base URL: `/api/v1/analytics`

---

### 1. Get User Personal Records

**Endpoint:** `GET /api/v1/analytics/users/{userId}/personal-records`

**Description:** Returns all personal records for a user across all exercises.

**Response Example:**
```json
[
  {
    "exerciseId": 1,
    "exerciseName": "Bench Press",
    "maxWeight": 225.0,
    "repsAtMaxWeight": 5,
    "achievedDate": "2024-09-15T10:30:00",
    "workoutId": 42,
    "oneRepMax": 253.75,
    "totalVolume": 5
  },
  {
    "exerciseId": 2,
    "exerciseName": "Squat",
    "maxWeight": 315.0,
    "repsAtMaxWeight": 3,
    "achievedDate": "2024-09-20T14:00:00",
    "workoutId": 45,
    "oneRepMax": 346.5,
    "totalVolume": 3
  }
]
```

**Use Cases:**
- Dashboard displaying all PRs
- Achievements and milestones
- Motivation and goal setting

---

### 2. Get Exercise Personal Record

**Endpoint:** `GET /api/v1/analytics/users/{userId}/exercises/{exerciseId}/personal-record`

**Description:** Returns the personal record for a specific exercise.

**Response Example:**
```json
{
  "exerciseId": 1,
  "exerciseName": "Bench Press",
  "maxWeight": 225.0,
  "repsAtMaxWeight": 5,
  "achievedDate": "2024-09-15T10:30:00",
  "workoutId": 42,
  "oneRepMax": 253.75,
  "totalVolume": 5
}
```

**Use Cases:**
- Exercise-specific PR display
- Goal tracking per exercise
- PR notification system

---

### 3. Get Exercise Progress

**Endpoint:** `GET /api/v1/analytics/users/{userId}/exercises/{exerciseId}/progress`

**Query Parameters:**
- `startDate` (optional): Filter start date (ISO 8601 format)
- `endDate` (optional): Filter end date (ISO 8601 format)

**Description:** Returns detailed progress data for an exercise over time.

**Response Example:**
```json
{
  "exerciseId": 1,
  "exerciseName": "Bench Press",
  "progressData": [
    {
      "date": "2024-08-01T10:00:00",
      "workoutId": 30,
      "averageWeight": 185.0,
      "maxWeight": 205.0,
      "totalReps": 25,
      "totalSets": 5,
      "totalVolume": 4625.0,
      "estimatedOneRepMax": 225.5
    },
    {
      "date": "2024-08-08T10:00:00",
      "workoutId": 32,
      "averageWeight": 190.0,
      "maxWeight": 215.0,
      "totalReps": 22,
      "totalSets": 5,
      "totalVolume": 4730.0,
      "estimatedOneRepMax": 236.5
    }
  ],
  "currentPR": {
    "exerciseId": 1,
    "exerciseName": "Bench Press",
    "maxWeight": 225.0,
    "repsAtMaxWeight": 5,
    "achievedDate": "2024-09-15T10:30:00",
    "workoutId": 42,
    "oneRepMax": 253.75
  },
  "stats": {
    "weightIncreasePercentage": 19.51,
    "volumeIncreasePercentage": 15.32,
    "totalWorkouts": 12,
    "firstWorkout": "2024-08-01T10:00:00",
    "lastWorkout": "2024-09-15T10:30:00",
    "averageWeightProgression": 3.33
  }
}
```

**Use Cases:**
- **Line Charts**: Plot weight progression over time
- **Volume Charts**: Visualize total volume changes
- Progress analysis and trend identification
- Identify plateaus or deloads

**Visualization Ideas:**
```javascript
// Chart.js example for weight progression
{
  type: 'line',
  data: {
    labels: progressData.map(d => d.date),
    datasets: [
      {
        label: 'Max Weight',
        data: progressData.map(d => d.maxWeight),
        borderColor: 'rgb(75, 192, 192)'
      },
      {
        label: 'Average Weight',
        data: progressData.map(d => d.averageWeight),
        borderColor: 'rgb(255, 159, 64)'
      }
    ]
  }
}
```

---

### 4. Get Workout Summary

**Endpoint:** `GET /api/v1/analytics/workouts/{workoutId}/summary`

**Description:** Returns a comprehensive summary of a specific workout.

**Response Example:**
```json
{
  "workoutId": 42,
  "workoutDate": "2024-09-15T10:30:00",
  "splitName": "Push Day",
  "totalExercises": 5,
  "totalSets": 25,
  "totalReps": 125,
  "totalVolume": 15750.0,
  "duration": null,
  "exercises": [
    {
      "exerciseId": 1,
      "exerciseName": "Bench Press",
      "muscleGroup": "Chest",
      "sets": 5,
      "totalReps": 25,
      "maxWeight": 225.0,
      "averageWeight": 210.0,
      "volume": 5250.0,
      "isPersonalRecord": true
    },
    {
      "exerciseId": 3,
      "exerciseName": "Overhead Press",
      "muscleGroup": "Shoulders",
      "sets": 4,
      "totalReps": 32,
      "maxWeight": 135.0,
      "averageWeight": 125.0,
      "volume": 4000.0,
      "isPersonalRecord": false
    }
  ]
}
```

**Use Cases:**
- Post-workout review screen
- Workout history with details
- Compare workouts side-by-side
- PR celebration notifications

---

### 5. Get User Statistics

**Endpoint:** `GET /api/v1/analytics/users/{userId}/stats`

**Description:** Returns comprehensive lifetime statistics for a user.

**Response Example:**
```json
{
  "userId": 1,
  "userName": "john_doe",
  "overall": {
    "totalWorkouts": 150,
    "totalExercises": 450,
    "totalSets": 2250,
    "totalReps": 11250,
    "totalVolumeLifted": 562500.0,
    "firstWorkout": "2024-01-01T08:00:00",
    "lastWorkout": "2024-09-30T18:00:00",
    "currentStreak": 12,
    "longestStreak": 45,
    "averageWorkoutsPerWeek": 4.2
  },
  "topPersonalRecords": [
    {
      "exerciseId": 5,
      "exerciseName": "Deadlift",
      "maxWeight": 405.0,
      "oneRepMax": 445.5
    },
    {
      "exerciseId": 2,
      "exerciseName": "Squat",
      "maxWeight": 315.0,
      "oneRepMax": 346.5
    }
  ],
  "muscleGroupBreakdown": {
    "Chest": {
      "muscleGroupName": "Chest",
      "timesWorked": 150,
      "totalVolume": 112500.0,
      "totalSets": 450,
      "lastWorked": "2024-09-30T18:00:00"
    },
    "Legs": {
      "muscleGroupName": "Legs",
      "timesWorked": 200,
      "totalVolume": 187500.0,
      "totalSets": 600,
      "lastWorked": "2024-09-29T10:00:00"
    }
  },
  "workoutFrequency": [
    {
      "period": "2024-W38",
      "workoutCount": 5,
      "totalVolume": 18750.0
    },
    {
      "period": "2024-W39",
      "workoutCount": 4,
      "totalVolume": 16200.0
    }
  ]
}
```

**Use Cases:**
- **Dashboard Overview**: Show lifetime achievements
- **Muscle Balance Charts**: Radar/pie charts of muscle group work
- **Consistency Tracking**: Calendar heatmap of workouts
- **Motivation**: Display streaks and milestones

**Visualization Ideas:**
```javascript
// Muscle Group Pie Chart
{
  type: 'pie',
  data: {
    labels: Object.keys(muscleGroupBreakdown),
    datasets: [{
      data: Object.values(muscleGroupBreakdown).map(mg => mg.totalVolume)
    }]
  }
}

// Workout Frequency Bar Chart
{
  type: 'bar',
  data: {
    labels: workoutFrequency.map(w => w.period),
    datasets: [{
      label: 'Workouts per Week',
      data: workoutFrequency.map(w => w.workoutCount)
    }]
  }
}
```

---

### 6. Get Volume Progress

**Endpoint:** `GET /api/v1/analytics/users/{userId}/volume-progress`

**Query Parameters:**
- `period` (default: "weekly"): Aggregation period ("weekly", "monthly", "yearly")
- `startDate` (optional): Filter start date
- `endDate` (optional): Filter end date

**Description:** Returns volume progression data aggregated by time period.

**Response Example:**
```json
{
  "period": "weekly",
  "dataPoints": [
    {
      "date": "2024-09-01T00:00:00",
      "label": "2024-W35",
      "totalVolume": 18750.0,
      "totalWorkouts": 5,
      "totalSets": 125,
      "totalReps": 625,
      "averageVolumePerWorkout": 3750.0
    },
    {
      "date": "2024-09-08T00:00:00",
      "label": "2024-W36",
      "totalVolume": 19500.0,
      "totalWorkouts": 4,
      "totalSets": 120,
      "totalReps": 600,
      "averageVolumePerWorkout": 4875.0
    }
  ],
  "stats": {
    "averageVolume": 19125.0,
    "peakVolume": 22000.0,
    "peakVolumeDate": "2024-09-15T00:00:00",
    "volumeTrend": 4.0,
    "totalVolumeInPeriod": 76500.0
  }
}
```

**Use Cases:**
- **Volume Trends**: Line chart showing volume over weeks/months
- **Training Load Management**: Identify overtraining or undertraining
- **Periodization Tracking**: Monitor volume phases (accumulation/deload)
- **Recovery Planning**: Spot when to take rest weeks

**Visualization Ideas:**
```javascript
// Volume Trend Line Chart with Moving Average
{
  type: 'line',
  data: {
    labels: dataPoints.map(d => d.label),
    datasets: [
      {
        label: 'Weekly Volume',
        data: dataPoints.map(d => d.totalVolume),
        fill: true,
        backgroundColor: 'rgba(75, 192, 192, 0.2)'
      },
      {
        label: 'Average',
        data: Array(dataPoints.length).fill(stats.averageVolume),
        borderDash: [5, 5]
      }
    ]
  }
}
```

---

## 🧮 Formulas & Calculations

### 1-Rep Max (1RM) Calculation
Uses the **Epley Formula**:
```
1RM = weight × (1 + reps/30)
```

Example: 225 lbs × 5 reps = 225 × (1 + 5/30) = 253.75 lbs

### Volume Calculation
```
Volume = Weight × Reps × Sets
```

Example: 
- Set 1: 225 lbs × 5 reps = 1125
- Set 2: 225 lbs × 5 reps = 1125
- Total: 2250 lbs

### Workout Streak
Consecutive days with at least one workout logged.

---

## 🎨 Frontend Integration Guide

### Sample Dashboard Layout

```
┌─────────────────────────────────────────────────────────┐
│  OutWorkIt Dashboard                                     │
├─────────────────────────────────────────────────────────┤
│  🔥 Current Streak: 12 days | 💪 Total Workouts: 150    │
│  📊 Total Volume: 562,500 lbs | 🏆 PRs This Month: 3    │
├─────────────────────────────────────────────────────────┤
│  📈 Volume Trend (Last 12 Weeks)                         │
│  [Line Chart showing weekly volume]                      │
├─────────────────────────────────────────────────────────┤
│  🏋️ Top Personal Records                                │
│  • Deadlift: 405 lbs (1RM: 445.5 lbs)                   │
│  • Squat: 315 lbs (1RM: 346.5 lbs)                      │
│  • Bench Press: 225 lbs (1RM: 253.75 lbs)               │
├─────────────────────────────────────────────────────────┤
│  💪 Muscle Group Balance                                 │
│  [Radar Chart or Pie Chart]                             │
└─────────────────────────────────────────────────────────┘
```

### Exercise Progress View

```
┌─────────────────────────────────────────────────────────┐
│  Bench Press Progress                                    │
├─────────────────────────────────────────────────────────┤
│  Current PR: 225 lbs × 5 reps (1RM: 253.75 lbs)         │
│  Achieved: Sep 15, 2024                                  │
├─────────────────────────────────────────────────────────┤
│  📈 Weight Progression                                   │
│  [Line Chart: Max Weight & Average Weight over time]    │
├─────────────────────────────────────────────────────────┤
│  Stats:                                                  │
│  • Weight Increase: +19.51%                             │
│  • Volume Increase: +15.32%                             │
│  • Total Workouts: 12                                   │
│  • Avg Progression: 3.33 lbs/workout                    │
└─────────────────────────────────────────────────────────┘
```

### Post-Workout Summary

```
┌─────────────────────────────────────────────────────────┐
│  🎉 Workout Complete!                                    │
├─────────────────────────────────────────────────────────┤
│  Push Day - Sep 30, 2024                                │
│                                                          │
│  📊 Summary:                                             │
│  • Total Volume: 15,750 lbs                             │
│  • Exercises: 5                                         │
│  • Sets: 25                                             │
│  • Reps: 125                                            │
│                                                          │
│  🏆 Personal Records:                                   │
│  • Bench Press: 225 lbs × 5 reps (NEW PR!)             │
│                                                          │
│  💪 Exercises:                                           │
│  ├─ Bench Press: 5×5 @ 225 lbs (5,250 lbs) ⭐          │
│  ├─ Overhead Press: 4×8 @ 135 lbs (4,000 lbs)          │
│  ├─ Incline Press: 4×10 @ 95 lbs (3,800 lbs)           │
│  ├─ Lateral Raise: 3×12 @ 25 lbs (900 lbs)             │
│  └─ Tricep Extension: 3×15 @ 30 lbs (1,350 lbs)        │
└─────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technical Implementation Notes

### New Components Added

1. **DTOs** (`dto/` package):
   - `PersonalRecordDTO.java`
   - `ExerciseProgressDTO.java`
   - `WorkoutSummaryDTO.java`
   - `UserStatsDTO.java`
   - `VolumeProgressDTO.java`

2. **Services** (`service/` package):
   - `AnalyticsService.java` - Core analytics logic

3. **Controllers** (`controller/` package):
   - `AnalyticsController.java` - REST endpoints

4. **Repository Enhancement**:
   - Added `findByUserId()` to `WorkoutRepository`

### No Breaking Changes
- ✅ All existing code remains unchanged
- ✅ All existing endpoints still work
- ✅ Database schema unchanged
- ✅ Only new features added

---

## 🚀 Testing the Endpoints

### Using cURL

```bash
# Get personal records
curl http://localhost:8080/api/v1/analytics/users/1/personal-records

# Get exercise progress
curl "http://localhost:8080/api/v1/analytics/users/1/exercises/5/progress?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"

# Get workout summary
curl http://localhost:8080/api/v1/analytics/workouts/42/summary

# Get user stats
curl http://localhost:8080/api/v1/analytics/users/1/stats

# Get volume progress (weekly)
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=weekly"

# Get volume progress (monthly, with date range)
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=monthly&startDate=2024-01-01T00:00:00"
```

---

## 📱 Mobile App Integration Ideas

### Home Screen Widgets
- Current streak counter
- Today's workout reminder
- Latest PR achieved
- Weekly volume comparison

### Notifications
- "🎉 New PR! You just hit 225 lbs on Bench Press!"
- "🔥 7-day streak! Keep it going!"
- "📊 You're up 15% in volume this month!"

### Social Features
- Share PR achievements
- Compare stats with friends
- Leaderboards by muscle group or total volume

---

## 🎯 Future Enhancement Ideas

### Possible Additions:
1. **Exercise Recommendations**: AI-based suggestions for next exercises
2. **Deload Detection**: Automatic detection when user needs rest
3. **Plateau Breaker**: Suggestions when progress stalls
4. **Body Composition Tracking**: Integrate weight/body fat tracking
5. **Nutrition Integration**: Correlate nutrition with performance
6. **Photos Progress**: Before/after photo timeline
7. **Video Form Analysis**: Record and analyze exercise form
8. **Workout Templates**: Create and share workout templates
9. **Training Programs**: Follow structured programs (5x5, PPL, etc.)
10. **Export Data**: CSV/PDF reports for coaches

---

## 📚 Related Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Chart.js for Visualizations](https://www.chartjs.org/)
- [REST API Best Practices](https://restfulapi.net/)

---

## 🤝 Contributing

When adding new analytics features:
1. Add DTOs in `dto/` package
2. Implement business logic in `AnalyticsService`
3. Expose via `AnalyticsController`
4. Document in this file
5. Add examples for frontend integration

---

## 📞 Support

For questions or issues:
- Review existing endpoints in this documentation
- Check the service layer for calculation logic
- Examine DTOs for available data fields

---

**Happy Tracking! 💪🏋️‍♂️📈**
