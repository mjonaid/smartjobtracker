package com.example.smartjobtracker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jobtracker.ui.JobScreen
import com.example.smartjobtracker.presentation.AddJobScreen
import com.example.smartjobtracker.presentation.JobViewModel
import com.example.smartjobtracker.presentation.SplashScreen
import com.example.smartjobtracker.utils.createNotificationChannel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // ✅ Dark mode will automatically switch based on system theme
            val darkTheme = isSystemInDarkTheme()

            MaterialTheme(
                colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                    }
                }

                createNotificationChannel(this)
                val jobViewModel: JobViewModel = koinViewModel()
                AppNavigation(jobViewModel, this)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: JobViewModel, context: Context) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("jobList") {
            JobScreen(viewModel, context, navController)
        }
        composable("addJob") {
            AddJobScreen(viewModel, context, navController)
        }
    }
}
