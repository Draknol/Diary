package io.github.draknol.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.draknol.diary.ui.theme.DiaryTheme


/**
 * @author Reuben Russell - 23004666
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create view model
        val viewModel: DiaryViewModel by viewModels { DiaryViewModelFactory(context = application) }

        enableEdgeToEdge()
        setContent {
            DiaryTheme {
                // Navigation controller for the app
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                // Remembered state of the EntryList
                val entryListState = rememberLazyGridState()

                Scaffold(
                    topBar = {
                        when (currentRoute) {
                            "home" -> TitleBar(
                                title = "Diary",
                                id = R.drawable.menu,
                                contentDescription = "menu",
                                onClick = { /* TODO */ }
                            )
                            "routine" -> TitleBar(
                                title = "Reminders",
                                id = R.drawable.menu,
                                contentDescription = "menu",
                                onClick = { /* TODO */ }
                            )
                            "add" -> TitleBar(
                                title = "New Entry",
                                id = R.drawable.back,
                                contentDescription = "back",
                                onClick = {
                                    navController.navigate(route = "home") {
                                        popUpTo(route = "home") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    },
                    bottomBar = {
                        when (currentRoute) {
                            "home" -> TabBar(navController, currentPage = 0)
                            "routine" -> TabBar(navController, currentPage = 1)
                            else -> TablessBar()
                        }
                    },
                    floatingActionButton = {
                        if (currentRoute == "home") {
                            AddButton(
                                onClick = {
                                    navController.navigate(route = "add") {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier
                            .padding(paddingValues = innerPadding)
                            .fillMaxSize()
                            .background(color = MaterialTheme.colorScheme.surfaceContainer),
                        navController = navController,
                        startDestination = "home",
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None },
                    ) {
                        composable(route = "home") {
                            val entries = viewModel.getAllDesc().collectAsState(initial = emptyList()).value
                            EntryList(state = entryListState, entries = entries)
                        }
                        composable(route = "routine") {
                            /* TODO */
                        }
                        composable(route = "add") {
                            /* TODO */
                        }
                    }
                }
            }
        }
    }
}