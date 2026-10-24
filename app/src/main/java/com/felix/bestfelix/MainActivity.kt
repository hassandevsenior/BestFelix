package com.felix.bestfelix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.felix.bestfelix.ui.BestFelixRoot
import com.felix.bestfelix.ui.theme.BestFelixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            BestFelixTheme {
                BestFelixRoot()
            }
        }
    }
}
