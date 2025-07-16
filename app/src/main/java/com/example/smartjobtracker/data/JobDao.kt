package com.example.smartjobtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Query("SELECT * FROM jobs ORDER BY deadline ASC")
    fun getAllJobs(): Flow<List<JobEntity>>
}
