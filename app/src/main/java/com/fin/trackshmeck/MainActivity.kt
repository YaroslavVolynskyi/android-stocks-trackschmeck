package com.fin.trackshmeck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fin.trackshmeck.ui.stockquote.WatchlistRoute
import com.fin.trackshmeck.ui.theme.StocktrackschmeckTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StocktrackschmeckTheme {
                WatchlistRoute()
            }
        }
    }
}
