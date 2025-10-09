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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    private val viewModel: DiaryViewModel by viewModels { DiaryViewModelFactory(context = application) }
    private lateinit var navController: NavHostController
    private var wipEntry: Entry? = null
    private var selectedEntry: Entry? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryTheme {
                // Navigation controller for the app
                navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                // Remembered state of the EntryList
                val entryListState = rememberLazyGridState()

                Scaffold(
                    topBar = topBar(currentRoute),
                    bottomBar = bottomBar(currentRoute),
                    floatingActionButton = floatingActionButton(currentRoute)
                ) { innerPadding ->
                    DiaryNavHost(innerPadding = innerPadding, entryListState = entryListState)
                }
            }
        }
    }


    /**
     * Navigation host for the app.
     * @param innerPadding The padding values for the inner content.
     * @param entryListState The remembered state of the EntryList.
     */
    @Composable
    fun DiaryNavHost(innerPadding: PaddingValues, entryListState: LazyGridState) {
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
                EntryList(state = entryListState, entries = entries, onClick = {
                    selectedEntry = entries.find { entry -> entry.id == it }
                    navController.navigate(route = "view")
                })
            }
            composable(route = "routine") {
                /* TODO */
            }
            composable(route = "add") {
                Column (modifier = Modifier.padding(all = 8.dp)) {
                    val title = rememberSaveable { mutableStateOf("") }
                    TextBox(text = title, placeholder = "Title", singleLine = true)

                    val content = rememberSaveable { mutableStateOf(value = "") }
                    TextBox(text = content, placeholder = "dear diary...", singleLine = false)

                    val date = LocalDate.now().toString()
                    wipEntry = Entry(title = title.value, content = content.value, date = date)
                }
            }
            composable(route = "add_image") {
                /* TODO */
            }
            composable(route = "view") {
                Column (modifier = Modifier.padding(all = 8.dp)) {
                    val title = selectedEntry?.title ?: "no title"
                    val titleStyle = LocalTextStyle.current.copy(fontSize = 24.sp, lineHeight = 24.sp, textAlign = TextAlign.Center)
                    TextBox(text = title, textStyle = titleStyle, singleLine = true)

                    val content = selectedEntry?.content ?: "no entry"
                    val contentStyle = LocalTextStyle.current
                    TextBox(text = content, textStyle = contentStyle, singleLine = false)
                }
            }
            composable(route = "view_image") {
                /* TODO */
            }
        }
    }


    /**
     * Top app bar based on current route.
     * @param currentRoute The current route.
     */
    @Composable
    fun topBar(currentRoute: String?): @Composable () -> Unit = {
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
                    wipEntry = null
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
                    wipEntry = null
                    navController.navigate(route = "home") {
                        popUpTo(route = "home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
            "view", "view_image" -> TitleBar(
                title = selectedEntry?.date ?: "Entry",
                id = R.drawable.back,
                contentDescription = "back",
                onClick = {
                    selectedEntry = null
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
     * @param currentRoute The current route.
     */
    @Composable
    fun bottomBar(currentRoute: String?): @Composable () -> Unit = {
        when (currentRoute) {
            "home" -> MainTabBar(navController, currentPage = 0)
            "routine" -> MainTabBar(navController, currentPage = 1)
            "add" -> AddTabBar(navController, currentPage = 0)
            "add_image" -> AddTabBar(navController, currentPage = 1)
            "view" -> ViewTabBar(navController, currentPage = 0)
            "view_image" -> ViewTabBar(navController, currentPage = 1)
            else -> TablessBar()
        }
    }


    /**
     * Floating action button based on current route.
     * @param currentRoute The current route.
     */
    @Composable
    fun floatingActionButton(currentRoute: String?): @Composable () -> Unit = {
        when (currentRoute) {
            "home" -> ActionButton(
                text = "Add",
                id = R.drawable.add,
                contentDescription = "add",
                onClick = {
                    navController.navigate(route = "add") {
                        launchSingleTop = true
                    }
                }
            )
            "add", "add_image" -> ActionButton(
                text = "Save",
                id = R.drawable.save,
                contentDescription = "save",
                onClick = {
                    wipEntry?.let {
                        if (it.title.isEmpty()) it.title = "no title"
                        if (it.content.isEmpty()) it.content = "entry is empty"
                        CoroutineScope(context = Dispatchers.IO).launch {
                            viewModel.insert(entry = it)
                        }
                    }
                    wipEntry = null
                    navController.navigate(route = "home") {
                        popUpTo(route = "home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
            "view", "view_image" -> ActionButton(
                text = "Edit",
                id = R.drawable.edit,
                contentDescription = "edit",
                onClick = {
                    /*TODO*/
                }
            )
        }
    }
}