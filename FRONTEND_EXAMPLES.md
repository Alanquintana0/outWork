# Frontend Integration Examples

## React Examples

### 1. Personal Records Dashboard Component

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';

const PersonalRecordsDashboard = ({ userId }) => {
  const [prs, setPrs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPRs = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/v1/analytics/users/${userId}/personal-records`
        );
        setPrs(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching PRs:', error);
        setLoading(false);
      }
    };

    fetchPRs();
  }, [userId]);

  if (loading) return <div>Loading...</div>;

  return (
    <div className="pr-dashboard">
      <h2>🏆 Personal Records</h2>
      <div className="pr-grid">
        {prs.map(pr => (
          <div key={pr.exerciseId} className="pr-card">
            <h3>{pr.exerciseName}</h3>
            <div className="pr-weight">{pr.maxWeight} lbs</div>
            <div className="pr-details">
              {pr.repsAtMaxWeight} reps | 1RM: {pr.oneRepMax.toFixed(1)} lbs
            </div>
            <div className="pr-date">
              {new Date(pr.achievedDate).toLocaleDateString()}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PersonalRecordsDashboard;
```

### 2. Exercise Progress Chart (Chart.js)

```jsx
import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import axios from 'axios';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const ExerciseProgressChart = ({ userId, exerciseId, exerciseName }) => {
  const [progressData, setProgressData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProgress = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/v1/analytics/users/${userId}/exercises/${exerciseId}/progress`
        );
        setProgressData(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching progress:', error);
        setLoading(false);
      }
    };

    fetchProgress();
  }, [userId, exerciseId]);

  if (loading || !progressData) return <div>Loading...</div>;

  const chartData = {
    labels: progressData.progressData.map(d => 
      new Date(d.date).toLocaleDateString()
    ),
    datasets: [
      {
        label: 'Max Weight',
        data: progressData.progressData.map(d => d.maxWeight),
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        tension: 0.4
      },
      {
        label: 'Average Weight',
        data: progressData.progressData.map(d => d.averageWeight),
        borderColor: 'rgb(255, 159, 64)',
        backgroundColor: 'rgba(255, 159, 64, 0.2)',
        tension: 0.4
      }
    ]
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: `${exerciseName} Progress`
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: 'Weight (lbs)'
        }
      }
    }
  };

  return (
    <div className="progress-chart">
      <Line data={chartData} options={options} />
      
      <div className="progress-stats">
        <h3>Statistics</h3>
        <p>Weight Increase: {progressData.stats.weightIncreasePercentage.toFixed(1)}%</p>
        <p>Volume Increase: {progressData.stats.volumeIncreasePercentage.toFixed(1)}%</p>
        <p>Total Workouts: {progressData.stats.totalWorkouts}</p>
        <p>Current PR: {progressData.currentPR?.maxWeight} lbs</p>
      </div>
    </div>
  );
};

export default ExerciseProgressChart;
```

### 3. User Stats Dashboard

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Pie, Bar } from 'react-chartjs-2';

const UserStatsDashboard = ({ userId }) => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/v1/analytics/users/${userId}/stats`
        );
        setStats(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching stats:', error);
        setLoading(false);
      }
    };

    fetchStats();
  }, [userId]);

  if (loading || !stats) return <div>Loading...</div>;

  const muscleGroupData = {
    labels: Object.keys(stats.muscleGroupBreakdown),
    datasets: [{
      label: 'Volume by Muscle Group',
      data: Object.values(stats.muscleGroupBreakdown).map(mg => mg.totalVolume),
      backgroundColor: [
        'rgba(255, 99, 132, 0.6)',
        'rgba(54, 162, 235, 0.6)',
        'rgba(255, 206, 86, 0.6)',
        'rgba(75, 192, 192, 0.6)',
        'rgba(153, 102, 255, 0.6)',
        'rgba(255, 159, 64, 0.6)',
      ]
    }]
  };

  const frequencyData = {
    labels: stats.workoutFrequency.map(w => w.period),
    datasets: [{
      label: 'Workouts per Week',
      data: stats.workoutFrequency.map(w => w.workoutCount),
      backgroundColor: 'rgba(75, 192, 192, 0.6)',
    }]
  };

  return (
    <div className="stats-dashboard">
      <h1>Welcome back, {stats.userName}!</h1>
      
      {/* Overall Stats Cards */}
      <div className="stats-cards">
        <div className="stat-card">
          <h3>🔥 Current Streak</h3>
          <p className="stat-value">{stats.overall.currentStreak} days</p>
        </div>
        
        <div className="stat-card">
          <h3>💪 Total Workouts</h3>
          <p className="stat-value">{stats.overall.totalWorkouts}</p>
        </div>
        
        <div className="stat-card">
          <h3>📊 Total Volume</h3>
          <p className="stat-value">
            {stats.overall.totalVolumeLifted.toLocaleString()} lbs
          </p>
        </div>
        
        <div className="stat-card">
          <h3>📅 Workouts/Week</h3>
          <p className="stat-value">
            {stats.overall.averageWorkoutsPerWeek.toFixed(1)}
          </p>
        </div>
      </div>

      {/* Top PRs */}
      <div className="top-prs">
        <h2>🏆 Top Personal Records</h2>
        {stats.topPersonalRecords.slice(0, 5).map(pr => (
          <div key={pr.exerciseId} className="pr-item">
            <span className="pr-exercise">{pr.exerciseName}</span>
            <span className="pr-weight">{pr.maxWeight} lbs</span>
            <span className="pr-1rm">1RM: {pr.oneRepMax.toFixed(1)} lbs</span>
          </div>
        ))}
      </div>

      {/* Muscle Group Breakdown */}
      <div className="chart-container">
        <h2>💪 Muscle Group Balance</h2>
        <Pie data={muscleGroupData} />
      </div>

      {/* Workout Frequency */}
      <div className="chart-container">
        <h2>📅 Weekly Workout Frequency</h2>
        <Bar data={frequencyData} />
      </div>
    </div>
  );
};

export default UserStatsDashboard;
```

### 4. Workout Summary Modal (Post-Workout)

```jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './WorkoutSummaryModal.css';

const WorkoutSummaryModal = ({ workoutId, onClose }) => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchSummary = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/v1/analytics/workouts/${workoutId}/summary`
        );
        setSummary(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching summary:', error);
        setLoading(false);
      }
    };

    fetchSummary();
  }, [workoutId]);

  if (loading || !summary) return <div>Loading...</div>;

  const hasPRs = summary.exercises.some(ex => ex.isPersonalRecord);

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          {hasPRs && <span className="celebration">🎉</span>}
          <h2>Workout Complete!</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <div className="workout-info">
          <h3>{summary.splitName}</h3>
          <p className="workout-date">
            {new Date(summary.workoutDate).toLocaleString()}
          </p>
        </div>

        <div className="summary-stats">
          <div className="stat">
            <span className="stat-label">Total Volume</span>
            <span className="stat-value">
              {summary.totalVolume.toLocaleString()} lbs
            </span>
          </div>
          <div className="stat">
            <span className="stat-label">Exercises</span>
            <span className="stat-value">{summary.totalExercises}</span>
          </div>
          <div className="stat">
            <span className="stat-label">Sets</span>
            <span className="stat-value">{summary.totalSets}</span>
          </div>
          <div className="stat">
            <span className="stat-label">Reps</span>
            <span className="stat-value">{summary.totalReps}</span>
          </div>
        </div>

        {hasPRs && (
          <div className="prs-section">
            <h3>🏆 New Personal Records!</h3>
            {summary.exercises
              .filter(ex => ex.isPersonalRecord)
              .map(ex => (
                <div key={ex.exerciseId} className="pr-achievement">
                  <span className="pr-name">{ex.exerciseName}</span>
                  <span className="pr-weight">{ex.maxWeight} lbs</span>
                </div>
              ))}
          </div>
        )}

        <div className="exercises-list">
          <h3>Exercises</h3>
          {summary.exercises.map(ex => (
            <div key={ex.exerciseId} className="exercise-item">
              <div className="exercise-header">
                <span className="exercise-name">
                  {ex.exerciseName}
                  {ex.isPersonalRecord && <span className="pr-badge">⭐ PR</span>}
                </span>
                <span className="muscle-group">{ex.muscleGroup}</span>
              </div>
              <div className="exercise-stats">
                <span>{ex.sets} sets</span>
                <span>{ex.totalReps} reps</span>
                <span>{ex.maxWeight} lbs max</span>
                <span>{ex.volume.toLocaleString()} lbs volume</span>
              </div>
            </div>
          ))}
        </div>

        <button className="share-btn">Share Achievement</button>
      </div>
    </div>
  );
};

export default WorkoutSummaryModal;
```

### 5. Volume Trend Chart

```jsx
import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import axios from 'axios';

const VolumeTrendChart = ({ userId, period = 'weekly' }) => {
  const [volumeData, setVolumeData] = useState(null);
  const [selectedPeriod, setSelectedPeriod] = useState(period);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchVolumeData = async () => {
      setLoading(true);
      try {
        const response = await axios.get(
          `http://localhost:8080/api/v1/analytics/users/${userId}/volume-progress`,
          { params: { period: selectedPeriod } }
        );
        setVolumeData(response.data);
        setLoading(false);
      } catch (error) {
        console.error('Error fetching volume data:', error);
        setLoading(false);
      }
    };

    fetchVolumeData();
  }, [userId, selectedPeriod]);

  if (loading || !volumeData) return <div>Loading...</div>;

  const chartData = {
    labels: volumeData.dataPoints.map(d => d.label),
    datasets: [
      {
        label: 'Total Volume',
        data: volumeData.dataPoints.map(d => d.totalVolume),
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        fill: true,
        tension: 0.4
      },
      {
        label: 'Average',
        data: Array(volumeData.dataPoints.length).fill(volumeData.stats.averageVolume),
        borderColor: 'rgb(255, 99, 132)',
        borderDash: [5, 5],
        pointRadius: 0
      }
    ]
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: `Volume Trend (${selectedPeriod})`
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        title: {
          display: true,
          text: 'Volume (lbs)'
        }
      }
    }
  };

  return (
    <div className="volume-chart">
      <div className="period-selector">
        <button 
          onClick={() => setSelectedPeriod('weekly')}
          className={selectedPeriod === 'weekly' ? 'active' : ''}
        >
          Weekly
        </button>
        <button 
          onClick={() => setSelectedPeriod('monthly')}
          className={selectedPeriod === 'monthly' ? 'active' : ''}
        >
          Monthly
        </button>
        <button 
          onClick={() => setSelectedPeriod('yearly')}
          className={selectedPeriod === 'yearly' ? 'active' : ''}
        >
          Yearly
        </button>
      </div>

      <Line data={chartData} options={options} />

      <div className="volume-stats">
        <div className="stat-item">
          <span>Peak Volume:</span>
          <span>{volumeData.stats.peakVolume.toLocaleString()} lbs</span>
        </div>
        <div className="stat-item">
          <span>Average Volume:</span>
          <span>{volumeData.stats.averageVolume.toLocaleString()} lbs</span>
        </div>
        <div className="stat-item">
          <span>Trend:</span>
          <span className={volumeData.stats.volumeTrend > 0 ? 'positive' : 'negative'}>
            {volumeData.stats.volumeTrend > 0 ? '↑' : '↓'} 
            {Math.abs(volumeData.stats.volumeTrend).toFixed(1)}%
          </span>
        </div>
      </div>
    </div>
  );
};

export default VolumeTrendChart;
```

---

## Vue.js Examples

### Composable for Analytics API

```javascript
// composables/useAnalytics.js
import { ref } from 'vue';
import axios from 'axios';

export function useAnalytics() {
  const loading = ref(false);
  const error = ref(null);

  const getPersonalRecords = async (userId) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/analytics/users/${userId}/personal-records`
      );
      return response.data;
    } catch (err) {
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const getExerciseProgress = async (userId, exerciseId, startDate, endDate) => {
    loading.value = true;
    error.value = null;
    try {
      const params = {};
      if (startDate) params.startDate = startDate;
      if (endDate) params.endDate = endDate;
      
      const response = await axios.get(
        `http://localhost:8080/api/v1/analytics/users/${userId}/exercises/${exerciseId}/progress`,
        { params }
      );
      return response.data;
    } catch (err) {
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const getUserStats = async (userId) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/analytics/users/${userId}/stats`
      );
      return response.data;
    } catch (err) {
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const getWorkoutSummary = async (workoutId) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/analytics/workouts/${workoutId}/summary`
      );
      return response.data;
    } catch (err) {
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };

  const getVolumeProgress = async (userId, period = 'weekly', startDate, endDate) => {
    loading.value = true;
    error.value = null;
    try {
      const params = { period };
      if (startDate) params.startDate = startDate;
      if (endDate) params.endDate = endDate;
      
      const response = await axios.get(
        `http://localhost:8080/api/v1/analytics/users/${userId}/volume-progress`,
        { params }
      );
      return response.data;
    } catch (err) {
      error.value = err.message;
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    loading,
    error,
    getPersonalRecords,
    getExerciseProgress,
    getUserStats,
    getWorkoutSummary,
    getVolumeProgress
  };
}
```

---

## Angular Service

```typescript
// analytics.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

interface PersonalRecordDTO {
  exerciseId: number;
  exerciseName: string;
  maxWeight: number;
  repsAtMaxWeight: number;
  achievedDate: string;
  workoutId: number;
  oneRepMax: number;
  totalVolume: number;
}

interface UserStatsDTO {
  userId: number;
  userName: string;
  overall: any;
  topPersonalRecords: PersonalRecordDTO[];
  muscleGroupBreakdown: any;
  workoutFrequency: any[];
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private baseUrl = 'http://localhost:8080/api/v1/analytics';

  constructor(private http: HttpClient) {}

  getPersonalRecords(userId: number): Observable<PersonalRecordDTO[]> {
    return this.http.get<PersonalRecordDTO[]>(
      `${this.baseUrl}/users/${userId}/personal-records`
    );
  }

  getExerciseProgress(
    userId: number, 
    exerciseId: number, 
    startDate?: string, 
    endDate?: string
  ): Observable<any> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get(
      `${this.baseUrl}/users/${userId}/exercises/${exerciseId}/progress`,
      { params }
    );
  }

  getUserStats(userId: number): Observable<UserStatsDTO> {
    return this.http.get<UserStatsDTO>(
      `${this.baseUrl}/users/${userId}/stats`
    );
  }

  getWorkoutSummary(workoutId: number): Observable<any> {
    return this.http.get(
      `${this.baseUrl}/workouts/${workoutId}/summary`
    );
  }

  getVolumeProgress(
    userId: number, 
    period: string = 'weekly', 
    startDate?: string, 
    endDate?: string
  ): Observable<any> {
    let params = new HttpParams().set('period', period);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get(
      `${this.baseUrl}/users/${userId}/volume-progress`,
      { params }
    );
  }
}
```

---

## CSS Styling Examples

```css
/* WorkoutSummaryModal.css */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.celebration {
  font-size: 48px;
  animation: bounce 0.5s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin: 24px 0;
}

.stat {
  text-align: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.pr-badge {
  background: gold;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-left: 8px;
}

.exercise-item {
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  margin-bottom: 12px;
}

.exercise-stats {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}
```

---

**Happy Coding! 🚀**
