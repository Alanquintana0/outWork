# Quick Start Guide - OutWorkIt Analytics

## 🚀 Get Started in 5 Minutes

### Step 1: Start the Application
```bash
# From the project root
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

---

### Step 2: Test the Endpoints

#### Get User Stats (Most Comprehensive)
```bash
curl http://localhost:8080/api/v1/analytics/users/1/stats | json_pp
```

This returns:
- Total workouts, sets, reps, volume
- Current and longest streaks
- Muscle group breakdown
- Top personal records
- Workout frequency

#### Get Personal Records
```bash
curl http://localhost:8080/api/v1/analytics/users/1/personal-records | json_pp
```

#### Get Exercise Progress (e.g., Exercise ID 5)
```bash
curl http://localhost:8080/api/v1/analytics/users/1/exercises/5/progress | json_pp
```

#### Get Workout Summary (e.g., Workout ID 42)
```bash
curl http://localhost:8080/api/v1/analytics/workouts/42/summary | json_pp
```

#### Get Volume Progress (Weekly)
```bash
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=weekly" | json_pp
```

---

### Step 3: Build Your First Frontend Component

#### React Example - Show User Stats

```jsx
import React, { useState, useEffect } from 'react';

function DashboardStats({ userId }) {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/v1/analytics/users/${userId}/stats`)
      .then(res => res.json())
      .then(data => setStats(data));
  }, [userId]);

  if (!stats) return <div>Loading...</div>;

  return (
    <div>
      <h1>Welcome, {stats.userName}!</h1>
      <div>
        <p>🔥 Streak: {stats.overall.currentStreak} days</p>
        <p>💪 Total Workouts: {stats.overall.totalWorkouts}</p>
        <p>📊 Total Volume: {stats.overall.totalVolumeLifted.toLocaleString()} lbs</p>
      </div>
      
      <h2>Top PRs</h2>
      {stats.topPersonalRecords.map(pr => (
        <div key={pr.exerciseId}>
          {pr.exerciseName}: {pr.maxWeight} lbs (1RM: {pr.oneRepMax.toFixed(1)} lbs)
        </div>
      ))}
    </div>
  );
}
```

---

### Step 4: Add a Chart (with Chart.js)

```jsx
import { Line } from 'react-chartjs-2';

function ProgressChart({ userId, exerciseId }) {
  const [progress, setProgress] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/v1/analytics/users/${userId}/exercises/${exerciseId}/progress`)
      .then(res => res.json())
      .then(data => setProgress(data));
  }, [userId, exerciseId]);

  if (!progress) return <div>Loading...</div>;

  const chartData = {
    labels: progress.progressData.map(d => new Date(d.date).toLocaleDateString()),
    datasets: [{
      label: 'Max Weight',
      data: progress.progressData.map(d => d.maxWeight),
      borderColor: 'rgb(75, 192, 192)'
    }]
  };

  return (
    <div>
      <h2>{progress.exerciseName} Progress</h2>
      <Line data={chartData} />
      <p>Weight Increase: {progress.stats.weightIncreasePercentage.toFixed(1)}%</p>
    </div>
  );
}
```

---

## 📊 Data Flow

```
User Workout Session
        ↓
    Database
        ↓
  AnalyticsService
   (Calculations)
        ↓
AnalyticsController
   (REST API)
        ↓
   Your Frontend
  (Visualization)
```

---

## 🎯 Key Endpoints Reference

| What You Want | Endpoint |
|---------------|----------|
| Overview of all stats | `/users/{userId}/stats` |
| All personal records | `/users/{userId}/personal-records` |
| Specific exercise PR | `/users/{userId}/exercises/{exerciseId}/personal-record` |
| Exercise progress chart | `/users/{userId}/exercises/{exerciseId}/progress` |
| Workout details | `/workouts/{workoutId}/summary` |
| Volume trend | `/users/{userId}/volume-progress?period=weekly` |

---

## 💡 Common Use Cases

### 1. Dashboard Page
```javascript
// Fetch user stats for overview
const stats = await fetch('/api/v1/analytics/users/1/stats').then(r => r.json());

// Display:
// - Current streak
// - Total workouts
// - Total volume
// - Top 5 PRs
// - Muscle group pie chart
// - Weekly frequency bar chart
```

### 2. Exercise Detail Page
```javascript
// Fetch exercise progress
const progress = await fetch('/api/v1/analytics/users/1/exercises/5/progress')
  .then(r => r.json());

// Display:
// - Current PR
// - Progress line chart
// - Stats (% increases)
// - All workout data points
```

### 3. Post-Workout Screen
```javascript
// After completing workout, fetch summary
const summary = await fetch('/api/v1/analytics/workouts/42/summary')
  .then(r => r.json());

// Display:
// - Total volume
// - Number of exercises/sets/reps
// - Each exercise breakdown
// - Any new PRs (highlighted!)
```

### 4. Progress Report
```javascript
// Fetch volume progress
const volume = await fetch('/api/v1/analytics/users/1/volume-progress?period=weekly')
  .then(r => r.json());

// Display:
// - Volume trend line chart
// - Peak volume
// - Average volume
// - Trend direction (up/down)
```

---

## 🔍 Query Parameters

### Date Filtering
```bash
# Exercise progress with date range
curl "http://localhost:8080/api/v1/analytics/users/1/exercises/5/progress?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"

# Volume progress with dates
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=monthly&startDate=2024-01-01T00:00:00"
```

### Period Selection
```bash
# Weekly volume
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=weekly"

# Monthly volume
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=monthly"

# Yearly volume
curl "http://localhost:8080/api/v1/analytics/users/1/volume-progress?period=yearly"
```

---

## 🎨 Visualization Ideas

### Dashboard Layout
```
┌──────────────────────────────────────────┐
│  Header: User Name + Streak             │
├──────────────────────────────────────────┤
│  [4 Stat Cards]                          │
│  Workouts | Volume | Streak | Avg/Week  │
├──────────────────────────────────────────┤
│  [Volume Trend Line Chart]               │
├──────────────────────────────────────────┤
│  [Top PRs List]  |  [Muscle Groups Pie]  │
└──────────────────────────────────────────┘
```

### Exercise Progress Page
```
┌──────────────────────────────────────────┐
│  Exercise Name + Current PR              │
├──────────────────────────────────────────┤
│  [Weight Progression Line Chart]         │
│  - Max Weight line                       │
│  - Average Weight line                   │
├──────────────────────────────────────────┤
│  Stats Cards:                            │
│  +19.5% Weight | +15.3% Volume | 12 WOs  │
└──────────────────────────────────────────┘
```

---

## 🔥 Pro Tips

### 1. Caching Strategy
```javascript
// Cache user stats for 5 minutes
const cache = new Map();
const CACHE_DURATION = 5 * 60 * 1000;

async function getUserStats(userId) {
  const cached = cache.get(userId);
  if (cached && Date.now() - cached.timestamp < CACHE_DURATION) {
    return cached.data;
  }
  
  const data = await fetch(`/api/v1/analytics/users/${userId}/stats`)
    .then(r => r.json());
  
  cache.set(userId, { data, timestamp: Date.now() });
  return data;
}
```

### 2. Error Handling
```javascript
async function fetchWithErrorHandling(url) {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    return await response.json();
  } catch (error) {
    console.error('Fetch error:', error);
    // Show user-friendly error message
    return null;
  }
}
```

### 3. Loading States
```jsx
function DataComponent() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    fetch('/api/v1/analytics/users/1/stats')
      .then(res => res.json())
      .then(data => {
        setData(data);
        setError(null);
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage error={error} />;
  if (!data) return <NoData />;
  
  return <DataDisplay data={data} />;
}
```

---

## 📱 Mobile App Structure

```
App/
├── Screens/
│   ├── DashboardScreen.js      → GET /users/{userId}/stats
│   ├── WorkoutsScreen.js       → List workouts
│   ├── WorkoutDetailScreen.js  → GET /workouts/{id}/summary
│   ├── ExerciseScreen.js       → List exercises
│   ├── ExerciseDetailScreen.js → GET /exercises/{id}/progress
│   └── ProfileScreen.js        → User info + settings
├── Components/
│   ├── StatCard.js
│   ├── ProgressChart.js
│   ├── PRBadge.js
│   └── StreakCounter.js
└── Services/
    └── analyticsService.js     → API calls
```

---

## 🎓 Learning Path

1. **Start Simple**: Display user stats on homepage
2. **Add Charts**: Use Chart.js for one exercise
3. **Build Dashboard**: Combine multiple charts
4. **Add Interactivity**: Date filters, exercise selection
5. **Enhance UX**: Animations, loading states, celebrations
6. **Go Pro**: Real-time updates, push notifications

---

## 🐛 Troubleshooting

### Server won't start?
```bash
# Check if MySQL is running
# Verify application.properties has correct DB config
# Check port 8080 is available
```

### No data returned?
```bash
# Make sure you have workouts in database
# Check user ID exists
# Verify exercise ID exists
```

### CORS errors in browser?
```java
// Add to your application
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

---

## 📖 More Examples

See these files for complete examples:
- **ANALYTICS_API.md** - Full API documentation
- **FRONTEND_EXAMPLES.md** - React, Vue, Angular code
- **IMPLEMENTATION_SUMMARY.md** - Technical details

---

## ✨ You're Ready!

You now have everything you need to build a world-class fitness tracking app!

**Start with:**
```bash
./mvnw spring-boot:run
curl http://localhost:8080/api/v1/analytics/users/1/stats
```

**Then build your frontend and watch the gains roll in! 💪📈**

---

*Happy Coding & Happy Training!* 🏋️‍♂️
