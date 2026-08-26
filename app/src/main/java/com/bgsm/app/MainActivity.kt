package com.bgsm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bgsm.app.ui.navigation.AppNavigation
import com.bgsm.app.ui.theme.BgsmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BgsmTheme {
                AppNavigation()
            }
        }
    }
}
