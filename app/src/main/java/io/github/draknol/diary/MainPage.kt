package io.github.draknol.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate


/**
 * Top app bar for the main page of the app.
 * Displays title `"Diary"`
 * Also has a non-functioning menu icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar() {
    TopAppBar(
        title = {
            Text(
                text = "Diary",
                fontSize = 36.sp
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "menu",
                modifier = Modifier.padding(12.dp).size(36.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}


/**
 * Floating action button for adding a new entry.
 * @param onClick The action to be performed when the button is clicked.
 */
@Composable
fun AddButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(size = 24.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.shadow(elevation = 16.dp)
    ) {
        Row (
            modifier = Modifier.width(width = 72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "add",
                modifier = Modifier.size(size = 28.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Add",
                fontSize = 20.sp
            )
        }
    }
}


/**
 * Bottom app bar for the main page of the app.
 * Displays two tabs: "Home" and "Routine".
 */
@Composable
fun TabBar() {
    // Will be replaced by navigation
    var homeSelected by remember { mutableStateOf(value = true) }
    var routineSelected by remember { mutableStateOf(value = false) }

    // Get height of navigation bar
    val navHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            .fillMaxWidth()
            .height(height = navHeight + 48.dp)
    )

    BottomAppBar(
        modifier = Modifier
            .navigationBarsPadding()
            .height(height = 48.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabIconToggleButton(
                    id = R.drawable.home,
                    contentDescription = "home",
                    checked = homeSelected,
                    onClick = { homeSelected = true; routineSelected = false }
                )

                TabIconToggleButton(
                    id = R.drawable.routine,
                    contentDescription = "routine",
                    checked = routineSelected,
                    onClick = { routineSelected = true; homeSelected = false }
                )
            }
        }
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
    val color = if (checked) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh

    Surface(
        modifier = Modifier.size(width = 64.dp, height = 32.dp),
        shape = RoundedCornerShape(24.dp),
        color = color,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = contentDescription,
            modifier = Modifier.padding(4.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}


/**
 * Uses a `LazyVerticalGrid` to show a grid of `Entry`s.
 * @param modifier Modifier to be used in the `LazyVerticalGrid`.
 */
@Composable
fun EntryList(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer),
        columns = GridCells.Fixed(count = 1)
    ) {
        val date = LocalDate.of(2025, 1, 1)
        items(count = 365) { index ->
            Entry(date.plusDays(index.toLong() * 20), "Journal Entry $index")
        }
    }
}


/**
 * Displays a single diary entry
 * @param date When the entry is for.
 * @param title The title of the entry.
 */
@Composable
fun Entry(date: LocalDate, title: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            text = date.toString(),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 10.sp,
        )
    }
}