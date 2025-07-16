package com.example.smartjobtracker.domain

import com.example.smartjobtracker.data.JobEntity
import com.example.smartjobtracker.data.JobDao
import kotlinx.coroutines.flow.Flow

class JobRepository(private val jobDao: JobDao) {
    suspend fun addJob(job: JobEntity) = jobDao.insertJob(job)
    fun getJobs(): Flow<List<JobEntity>> = jobDao.getAllJobs()
}
