package io.github.draknol.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import io.github.draknol.diary.ui.theme.DiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiaryTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TitleBar() },
                    bottomBar = { TabBar() },
                    floatingActionButton = { AddButton(onClick = {}) }
                ) { innerPadding ->
                    EntryList(modifier = Modifier.padding(paddingValues = innerPadding))
                }
            }
        }
    }
}