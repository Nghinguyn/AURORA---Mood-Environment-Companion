package com.example.aurora.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun JournalWriteScreen(
    mood: String,
    onSubmit: (String, String) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Tell me more about feeling $mood",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Writing even a few sentences helps me understand you better 💛\n" +
                    "• What happened today?\n" +
                    "• What made you feel this way?\n" +
                    "• What do you wish was different?",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                if (it.length > 10) error = ""
            },
            minLines = 5,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Describe your feelings…") }
        )

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (description.length < 10) {
                    error = "Could you tell me just a little more? 💛"
                } else {
                    onSubmit("Feeling $mood", description)
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Continue")
        }
    }
}
