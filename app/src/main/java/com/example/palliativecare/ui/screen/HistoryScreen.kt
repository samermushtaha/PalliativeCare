package com.example.palliativecare.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.palliativecare.model.HistoricalEvent

@Composable
fun HistoryScreen() {
    val historicalEvents = listOf(
        HistoricalEvent("January 1, 2000", "Event 1", "Description of Event 1"),
        HistoricalEvent("February 2, 2001", "Event 2", "Description of Event 2"),
        HistoricalEvent("March 3, 2002", "Event 3", "Description of Event 3"),
        HistoricalEvent("April 4, 2003", "Event 4", "Description of Event 4"),
        HistoricalEvent("May 5, 2004", "Event 5", "Description of Event 5")
    )

    HistoricalEventList(historicalEvents = historicalEvents)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalEventList(historicalEvents: List<HistoricalEvent>) {
    Column {
        TopAppBar(
            title = { Text(text = "السجل") },
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
            }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(historicalEvents) { historicalEvent ->
                HistoricalEventItem(historicalEvent)
            }
        }
    }
}

@Composable
fun HistoricalEventItem(historicalEvent: HistoricalEvent) {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = historicalEvent.date,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = historicalEvent.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = historicalEvent.description,
                fontSize = 14.sp
            )
        }
    }
}
