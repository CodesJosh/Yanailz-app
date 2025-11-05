package com.yanails.uasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yanails.uasapp.ui.theme.UñasAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UñasAppTheme {
                // Aquí llamamos al "cerebro" de nuestra app, que está en App.kt
                App()
            }
        }
    }
}