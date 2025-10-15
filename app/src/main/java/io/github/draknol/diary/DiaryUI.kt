package io.github.draknol.diary

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter


/**
 * Top app bar with a navigation icon.
 * @param title The title of the page.
 * @param id The resource ID of the navigationIcon.
 * @param contentDescription The description of the navigationIcon.
 * @param onClick The action to be performed when the navigationIcon is clicked.
 */
@OptIn(markerClass = [ExperimentalMaterial3Api::class])
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
                    modifier = Modifier.padding(all = 6.dp).size(size = 36.dp)
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
 * Top app bar without a navigation icon.
 * @param title The title of the page.
 * @param id The resource ID of the Icon.
 * @param contentDescription The description of the Icon.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(title: String, id: Int, contentDescription: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 32.sp
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id),
                contentDescription = contentDescription,
                modifier = Modifier.padding(start = 6.dp).size(size = 48.dp),
                tint = Color(color = 0xFF0072eb)
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
 * @param text The text to display on the button.
 * @param id The resource ID of the icon.
 * @param contentDescription The description of the icon.
 * @param width The width of the button.
 * @param onClick The action to be performed when the button is clicked.
 */
@Composable
fun ActionButton(text: String, id: Int, contentDescription: String, width: Dp, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        shape = RoundedCornerShape(size = 24.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
    ) {
        Row (
            modifier = Modifier.width(width = width),
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
 * Bottom app bar for the app.
 * @param navController The navigation controller.
 * @param currentPage The current page (0 for "home", 1 for "routine").
 * @param id1 The resource ID of the first icon.
 * @param contentDescription1 The description of the first icon.
 * @param destination1 The destination to navigate to when the first icon is clicked.
 * @param id2 The resource ID of the second icon.
 * @param contentDescription2 The description of the second icon.
 * @param destination2 The destination to navigate to when the second icon is clicked.
 */
@Composable
fun TabBar(
    navController: NavController, currentPage: Int,
    id1: Int, contentDescription1: String, destination1: String,
    id2: Int, contentDescription2: String, destination2: String
) {
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabIconToggleButton(
                    id = id1,
                    contentDescription = contentDescription1,
                    checked = currentPage == 0,
                    onClick = {
                        if (currentPage != 0) {
                            navController.navigate(route = destination1) {
                                popUpTo(route = destination1) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    }
                )
                TabIconToggleButton(
                    id = id2,
                    contentDescription = contentDescription2,
                    checked = currentPage == 1,
                    onClick = {
                        if (currentPage != 1) {
                            navController.navigate(route = destination2) {
                                popUpTo(route = destination1) { inclusive = false }
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
 * @param onClick The action to be performed when an entry is clicked.
 */
@Composable
fun EntryList(state: LazyGridState, entries: List<Entry>, onClick: (id: Long) -> Unit) {
    LazyVerticalGrid(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surfaceContainer),
        columns = GridCells.Fixed(count = 1),
        state = state
    ) {
        items(count = entries.size) { index ->
            Entry(entry = entries[index], onClick = onClick)
        }
    }
}


/**
 * Displays a single diary entry
 * @param entry The entry to display.
 * @param onClick The action to be performed when the entry is clicked.
 */
@Composable
fun Entry(entry: Entry, onClick: (id: Long) -> Unit) {
    Surface (
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(size = 8.dp),
        onClick = { onClick(entry.id) }
    ) {
        Column (
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = entry.title.ifEmpty { "no title" },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                text = entry.date,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 10.sp,
            )
        }
    }
}


/**
 * Text box for writing in.
 * Doesn't scroll correctly when keyboard comes up but it is a know issue
 * https://issuetracker.google.com/issues/237190748
 * Gmail has the same issue.
 * @param text The text to use.
 * @param onValueChange The action to be performed when the text changes.
 * @param placeholder The placeholder text.
 * @param singleLine Whether the text box should be single line.
 */
@Composable
fun TextBox(
    text: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean
) {
    val bottomPadding = if (singleLine) 16.dp
    else with(receiver = LocalDensity.current) {
        max(a = WindowInsets.ime.getBottom(density = LocalDensity.current).toDp(), b = 80.dp)
    }

    Card(modifier = Modifier.padding(bottom = bottomPadding)) {
        TextField(
            value = text,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.Gray) },
            singleLine = singleLine,
            modifier = if (singleLine) Modifier.fillMaxWidth() else Modifier.fillMaxSize(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}


/**
 * Displays and image (or message if no image is selected) on a card.
 * @param imageUri The URI of the image (`null` for no image).
 */
@Composable
fun ImageBox(
    imageUri: String?
) {
    val bottomPadding = 80.dp

    Card(
        modifier = Modifier
            .padding(all = 8.dp)
            .padding(bottom = bottomPadding),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                val painter = rememberAsyncImagePainter(
                    model = imageUri
                )

                Image(
                    painter = painter,
                    contentDescription = "entry image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = "No image selected", color = Color.Gray)
            }
        }
    }
}