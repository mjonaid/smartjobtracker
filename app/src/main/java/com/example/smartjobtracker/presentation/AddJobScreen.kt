package com.example.smartjobtracker.presentation

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartjobtracker.data.JobEntity
import com.example.smartjobtracker.utils.DeadlineWorker
import java.util.concurrent.TimeUnit
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import java.util.*
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddJobScreen(
    viewModel: JobViewModel,
    context: Context,
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Upcoming") }

    var deadline by remember { mutableStateOf(System.currentTimeMillis()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Job") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Job Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            TextField(
                value = company,
                onValueChange = { company = it },
                label = { Text("Company") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            DropdownMenuSample(status) { status = it }
            Spacer(Modifier.height(16.dp))

            // ✅ Deadline Picker
            DeadlinePicker(deadline) { selectedTime ->
                deadline = selectedTime
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isNotBlank() && company.isNotBlank()) {
                        val job = JobEntity(
                            title = title,
                            company = company,
                            deadline = deadline,
                            status = status
                        )
                        viewModel.addJob(job)
                        scheduleDeadlineNotification(context, title, deadline)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Job")
            }
        }
    }
}



fun scheduleDeadlineNotification(context: Context, jobTitle: String, deadline: Long) {
    val currentTime = System.currentTimeMillis()

    val timeDiff = deadline - currentTime - (24 * 60 * 60 * 1000)
    if (timeDiff > 0) {
        val data = Data.Builder()
            .putString("jobTitle", jobTitle)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<DeadlineWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

@Composable
fun DropdownMenuSample(selectedStatus: String, onSelect: (String) -> Unit) {
    val items = listOf("Upcoming", "Applied", "Bookmarked")
    var expanded by remember { mutableStateOf(false) }

    // Rotate icon when expanded
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedStatus,
            onValueChange = {},
            label = { Text("Status") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onSelect(status)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun DeadlinePicker(currentDeadline: Long, onDeadlineSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    var selectedDeadline by remember { mutableStateOf(currentDeadline) }

    OutlinedTextField(
        value = dateFormat.format(Date(selectedDeadline)),
        onValueChange = {},
        readOnly = true,
        label = { Text("Deadline") },
        trailingIcon = {
            IconButton(onClick = {
                val datePicker = DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, day)

                        val timePicker = TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                selectedDeadline = calendar.timeInMillis
                                onDeadlineSelected(calendar.timeInMillis)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        )
                        timePicker.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }) {
                Icon(
                    imageVector = Icons.Rounded.DateRange,
                    contentDescription = "Pick Deadline"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


