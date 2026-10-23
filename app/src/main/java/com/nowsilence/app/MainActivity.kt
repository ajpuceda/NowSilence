package com.nowsilence.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nowsilence.app.presentation.navigation.NowSilenceNavGraph
import com.nowsilence.app.presentation.theme.NowSilenceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NowSilenceTheme {
                NowSilenceNavGraph()
            }
        }
    }
}
