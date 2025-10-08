package io.github.draknol.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Top app bar for the main page of the app.
 * Displays title `"Diary"`
 * Also has a non-functioning menu icon.
 * @param title The title of the page.
 * @param id The resource ID of the navigationIcon.
 * @param contentDescription The description of the navigationIcon.
 * @param onClick The action to be performed when the navigationIcon is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(title: String, id: Int, contentDescription: String, onClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 32.sp
            )
        },
        navigationIcon = {
            IconButton(onClick) {
                Icon(
                    painter = painterResource(id),
                    contentDescription = contentDescription,
                    modifier = Modifier.padding(6.dp).size(36.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}


/**
 * Floating action button for adding a new entry.
 * @param text The text to display on the button.
 * @param id The resource ID of the icon.
 * @param onClick The action to be performed when the button is clicked.
 */
@Composable
fun ActionButton(text: String, id: Int, contentDescription: String, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(size = 24.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Row (
            modifier = Modifier.width(width = 72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = id),
                contentDescription = contentDescription,
                modifier = Modifier.size(size = 28.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = text,
                fontSize = 20.sp
            )
        }
    }
}


/**
 * Bottom app bar for the main page of the app.
 * Displays two tabs: "Home" and "Routine".
 * @param navController The navigation controller.
 * @param currentPage The current page (0 for "home", 1 for "routine").
 */
@Composable
fun TabBar(navController: NavController, currentPage: Int) {
    TablessBar()
    BottomAppBar(
        modifier = Modifier
            .navigationBarsPadding()
            .height(height = 48.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabIconToggleButton(
                    id = R.drawable.home,
                    contentDescription = "home",
                    checked = currentPage == 0,
                    onClick = {
                        if (currentPage != 0) {
                            navController.navigate(route = "home") {
                                popUpTo(route = "home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
                TabIconToggleButton(
                    id = R.drawable.routine,
                    contentDescription = "routine",
                    checked = currentPage == 1,
                    onClick = {
                        if (currentPage != 1) {
                            navController.navigate(route = "routine") {
                                popUpTo(route = "home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    )
}


/**
 * Blank bottom app bar with no tabs.
 */
@Composable
fun TablessBar() {
    // Get height of navigation bar
    val navHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            .fillMaxWidth()
            .height(height = navHeight + 48.dp)
    )
}


/**
 * Icon for a tab in the bottom app bar.
 * @param id The resource ID of the icon.
 * @param contentDescription The description of the icon.
 * @param checked Whether the tab is currently checked.
 * @param onClick The action to be performed when the tab is clicked.
 */
@Composable
fun TabIconToggleButton(id: Int, contentDescription: String, checked: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(width = 64.dp, height = 32.dp),
        shape = RoundedCornerShape(size = 24.dp),
        color = if (checked) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceContainerHigh,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = contentDescription,
            modifier = Modifier.padding(all = 4.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}


/**
 * Lazy vertical grid of diary entries.
 * @param state The state of the grid.
 * @param entries The list of entries to display.
 */
@Composable
fun EntryList(state: LazyGridState, entries: List<Entry>) {
    LazyVerticalGrid(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainer),
        columns = GridCells.Fixed(count = 1),
        state = state
    ) {
        items(count = entries.size) { index ->
            val entry = entries[index]
            Entry(date = entry.date, title = entry.title)
        }
    }
}


/**
 * Displays a single diary entry
 * @param date When the entry is for.
 * @param title The title of the entry.
 */
@Composable
fun Entry(date: String, title: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(size = 8.dp)
            )
            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            text = date,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 10.sp,
        )
    }
}


/**
 * Text box for writing in.
 * Doesn't scroll correctly when keyboard comes up but it is a know issue
 * https://issuetracker.google.com/issues/237190748
 * Gmail has the same issue.
 * @param text The text state to use.
 * @param placeholder The placeholder text.
 * @param singleLine Whether the text box should be single line.
 */
@Composable
fun TextBox(text: MutableState<String>, placeholder: String, singleLine: Boolean = false) {

    val bottomPadding = with(receiver = LocalDensity.current) {
        max(a = WindowInsets.ime.getBottom(density = LocalDensity.current).toDp(), b = 80.dp)
    }

    Card(
        modifier = if (singleLine) {
            Modifier.padding(bottom = 16.dp).fillMaxWidth()
        } else {
            Modifier.padding(bottom = bottomPadding).fillMaxSize()
        },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
    ) {
        TextField(
            value = text.value,
            onValueChange = { text.value = it },
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            singleLine = singleLine,
            modifier = if (singleLine) {
                Modifier.fillMaxWidth()
            } else {
                Modifier.fillMaxSize()
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
        )
    }
}