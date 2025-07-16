package com.example.jobtracker.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smartjobtracker.utils.generateJobListPdf
import com.example.smartjobtracker.utils.sharePdf
import com.example.smartjobtracker.presentation.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobScreen(
    viewModel: JobViewModel,
    context: Context,
    navController: NavController
) {
    val jobs by viewModel.jobs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }

    // ✅ Tab State
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Upcoming", "Applied", "Bookmarked")

    // ✅ Filter jobs based on selected tab
    val filteredJobs = when (tabs[selectedTabIndex]) {
        "Upcoming" -> jobs.filter { it.status == "Upcoming" }
        "Applied" -> jobs.filter { it.status == "Applied" }
        "Bookmarked" -> jobs.filter { it.status == "Bookmarked" }
        else -> jobs
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Job Tracker") },
                actions = {
                    IconButton(onClick = {
                        val pdf = generateJobListPdf(context, filteredJobs)
                        sharePdf(context, pdf)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addJob") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Job")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // ✅ Tab Menu
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // ✅ Jobs List
            LazyColumn(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

                items(filteredJobs) { job ->
                    Card(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(job.title, style = MaterialTheme.typography.titleMedium)
                                Text("Company: ${job.company}", style = MaterialTheme.typography.bodyMedium)
                                Text("Status: ${job.status}", style = MaterialTheme.typography.bodySmall)
                            }
                            Text(
                                text = "Deadline: ${dateFormat.format(Date(job.deadline))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

        }
    }
}

