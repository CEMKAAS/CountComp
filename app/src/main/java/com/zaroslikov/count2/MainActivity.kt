package com.zaroslikov.count2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.zaroslikov.count2.ui.MyCountApp
import com.zaroslikov.count2.ui.theme.Count2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Count2Theme {
                MyCountApp()
            }
        }
    }
}
