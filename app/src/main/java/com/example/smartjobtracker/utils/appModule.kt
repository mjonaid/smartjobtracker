package com.example.smartjobtracker.utils

import androidx.room.Room
import com.example.smartjobtracker.data.JobDatabase
import com.example.smartjobtracker.domain.JobRepository
import com.example.smartjobtracker.presentation.JobViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {

    // ✅ Provide Room Database
    single {
        Room.databaseBuilder(
            get(),
            JobDatabase::class.java,
            "job_database"
        ).build()
    }

    // ✅ Provide DAO
    single { get<JobDatabase>().jobDao() }

    // ✅ Provide Repository
    single { JobRepository(get()) }

    // ✅ Provide ViewModel
    viewModel { JobViewModel(get()) }
}

