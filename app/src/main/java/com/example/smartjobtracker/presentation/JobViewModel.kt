package com.example.smartjobtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartjobtracker.data.JobEntity
import com.example.smartjobtracker.domain.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JobViewModel(private val repository: JobRepository) : ViewModel() {

    private val _jobs = MutableStateFlow<List<JobEntity>>(emptyList())
    val jobs: StateFlow<List<JobEntity>> = _jobs

    fun loadJobs() {
        viewModelScope.launch {
            repository.getJobs().collect {
                _jobs.value = it
            }
        }
    }

    fun addJob(job: JobEntity) {
        viewModelScope.launch {
            repository.addJob(job)
        }
    }
}
