package io.github.draknol.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.draknol.diary.ui.theme.DiaryTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * @author Reuben Russell - 23004666
 */
class MainActivity : ComponentActivity() {
    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory(context = application)
    }


    /**
     * Called when the activity is starting.
     * Sets the content of the activity to the DiaryTheme.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    topBar = topBar(navController, currentRoute),
                    bottomBar = bottomBar(navController, currentRoute),
                    floatingActionButton = floatingActionButton(navController, currentRoute)
                ) { innerPadding ->
                    DiaryNavHost(navController, innerPadding, entryListState)
                }
            }
        }
    }


    /**
     * Navigation host for the app.
     * @param navController The navigation controller.
     * @param innerPadding The padding values for the inner content.
     * @param entryListState The remembered state of the EntryList.
     */
    @Composable
    fun DiaryNavHost(
        navController: NavHostController,
        innerPadding: PaddingValues,
        entryListState: LazyGridState
    ) {
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
            // Home page with a list of viewable entries
            composable(route = "home") {
                val entries = viewModel.getAllDesc().collectAsState(initial = emptyList()).value
                EntryList(state = entryListState, entries = entries, onClick = { id ->
                    entries.find { entry -> entry.id == id }?.let { entry ->
                        viewModel.selectedEntry.value = entry
                        navController.navigate(route = "view")
                    }
                })
            }

            // Routine page, set time for reminder
            composable(route = "routine") {
                /* TODO */
            }

            // Add page, allows the user to add a new entry
            composable(route = "add") {
                Column (modifier = Modifier.padding(all = 8.dp)) {
                    TextBox(
                        text = viewModel.selectedEntry.value.title,
                        onValueChange = { viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(title = it) },
                        placeholder = "Title",
                        singleLine = true
                    )

                    TextBox(
                        text = viewModel.selectedEntry.value.content,
                        onValueChange = { viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(content = it) },
                        placeholder = "dear diary...",
                        singleLine = false
                    )
                }
            }

            // Add image page, allows the user to add an image
            composable(route = "add_image") {
                /* TODO */
            }

            // View page, allows the user to view and edit an entry
            composable(route = "view") {
                Column (modifier = Modifier.padding(all = 8.dp)) {
                    TextBox(
                        text = viewModel.selectedEntry.value.title,
                        onValueChange = { viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(title = it) },
                        placeholder = "Title",
                        singleLine = true
                    )

                    TextBox(
                        text = viewModel.selectedEntry.value.content,
                        onValueChange = { viewModel.selectedEntry.value = viewModel.selectedEntry.value.copy(content = it) },
                        placeholder = "dear diary...",
                        singleLine = false
                    )
                }
            }

            // View image page, allows the user to view and swap an image
            composable(route = "view_image") {
                /* TODO */
            }
        }
    }


    /**
     * Top app bar based on current route.
     * @param navController The navigation controller.
     * @param currentRoute The current route.
     */
    @Composable
    fun topBar(
        navController: NavHostController,
        currentRoute: String?
    ): @Composable () -> Unit = {
        when (currentRoute) {
            // Topbar with title
            "home" -> TitleBar(
                title = "Diary",
                id = R.drawable.ic_launcher_foreground,
                contentDescription = "logo"
            )
            "routine" -> TitleBar(
                title = "Reminders",
                id = R.drawable.ic_launcher_foreground,
                contentDescription = "logo"
            )

            // Topbar with title and back button
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
            "add_image" -> TitleBar(
                title = "Add Image",
                id = R.drawable.back,
                contentDescription = "back",
                onClick = {
                    navController.navigate(route = "home") {
                        popUpTo(route = "home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )

            // Topbar with date and back button
            "view", "view_image" -> TitleBar(
                title = viewModel.selectedEntry.value.date,
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
    }


    /**
     * Bottom app bar based on current route.
     * @param navController The navigation controller.
     * @param currentRoute The current route.
     */
    @Composable
    fun bottomBar(navController: NavHostController, currentRoute: String?): @Composable () -> Unit = {
        when (currentRoute) {
            "home" -> TabBar(
                navController = navController, currentPage = 0,
                id1 = R.drawable.home, contentDescription1 = "home", destination1 = "home",
                id2 = R.drawable.routine, contentDescription2 = "routine", destination2 = "routine"
            )
            "routine" -> TabBar(
                navController = navController, currentPage = 1,
                id1 = R.drawable.home, contentDescription1 = "home", destination1 = "home",
                id2 = R.drawable.routine, contentDescription2 = "routine", destination2 = "routine"
            )
            "add" -> TabBar(
                navController = navController, currentPage = 0,
                id1 = R.drawable.text, contentDescription1 = "text", destination1 = "add",
                id2 = R.drawable.image, contentDescription2 = "image", destination2 = "add_image"
            )
            "add_image" -> TabBar(
                navController = navController, currentPage = 1,
                id1 = R.drawable.text, contentDescription1 = "text", destination1 = "add",
                id2 = R.drawable.image, contentDescription2 = "image", destination2 = "add_image"
            )
            "view" -> TabBar(
                navController = navController, currentPage = 0,
                id1 = R.drawable.text, contentDescription1 = "text", destination1 = "view",
                id2 = R.drawable.image, contentDescription2 = "image", destination2 = "view_image"
            )
            "view_image" -> TabBar(
                navController = navController, currentPage = 1,
                id1 = R.drawable.text, contentDescription1 = "text", destination1 = "view",
                id2 = R.drawable.image, contentDescription2 = "image", destination2 = "view_image"
            )
        }
    }


    /**
     * Floating action button based on current route.
     * @param navController The navigation controller.
     * @param currentRoute The current route.
     */
    @Composable
    fun floatingActionButton(
        navController: NavHostController,
        currentRoute: String?
    ): @Composable () -> Unit = {
        when (currentRoute) {
            // Floating action button for adding a new entry
            "home" -> ActionButton(
                text = "Add",
                id = R.drawable.add,
                contentDescription = "add",
                width  = 72.dp,
                onClick = {
                    viewModel.selectedEntry.value = Entry(
                        title = "",
                        content = "",
                        date = LocalDate.now().toString()
                    )
                    navController.navigate(route = "add") {
                        launchSingleTop = true
                    }
                }
            )

            // Floating action button for saving new entry
            "add" -> ActionButton(
                text = "Save",
                id = R.drawable.save,
                contentDescription = "save",
                width  = 72.dp,
                onClick = {
                    viewModel.insert(entry = viewModel.selectedEntry.value)
                    navController.navigate(route = "home") {
                        popUpTo(route = "home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )

            // Floating action button for saving edited entry
            "view" -> ActionButton(
                text = "Save",
                id = R.drawable.save,
                contentDescription = "save",
                width  = 72.dp,
                onClick = {
                    viewModel.update(entry = viewModel.selectedEntry.value)
                    navController.navigate(route = "home") {
                        popUpTo(route = "home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )

            // Floating action button for adding an image
            "add_image", "view_image" -> ActionButton(
                text = "Attach",
                id = R.drawable.attach,
                contentDescription = "attach",
                width = 90.dp,
                onClick = {
                    /*TODO*/
                }
            )
        }
    }
}