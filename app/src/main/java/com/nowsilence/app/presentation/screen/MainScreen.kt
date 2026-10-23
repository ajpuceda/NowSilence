package com.nowsilence.app.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nowsilence.app.presentation.theme.NowSilenceTheme

@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("NowSilence") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Filtro Inteligente de Notificaciones con IA Local")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NowSilenceTheme {
        MainScreen()
    }
}
