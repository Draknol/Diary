package io.github.draknol.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate



/**
 * Top app bar for the main page of the app.
 * Displays title `"Diary"` using Material 3 color scheme.
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    )
}

/**
 * Uses a `LazyVerticalGrid` to show a grid of `Entry`s.
 * @param modifier Modifier to be used in the `LazyVerticalGrid`.
 */
@Composable
fun EntryList(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(count = 1)
    ) {
        val date = LocalDate.of(2025, 1, 1)
        items(count = 365) { index ->
            Entry(date.plusDays(index.toLong()), "Journal Entry $index")
        }
    }
}

/**
 * Displays a single diary entry using Material 3 color scheme.
 * @param date When the entry is for.
 * @param title The title of the entry.
 */
@Composable
fun Entry(date: LocalDate, title: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(8.dp)
            )
            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 24.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            text = date.toString(),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 10.sp,
        )
    }
}