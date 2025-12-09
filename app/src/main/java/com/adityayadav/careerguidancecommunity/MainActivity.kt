package com.adityayadav.careerguidancecommunity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.adityayadav.careerguidancecommunity.ui.navigation.AppNavHost
import com.adityayadav.careerguidancecommunity.ui.theme.CareerGuidanceCommunityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareerGuidanceCommunityTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    // Theme reads DataStore internally — nothing else required
    CareerGuidanceCommunityTheme {
        val navController = rememberNavController()
        Surface {
            AppNavHost(navController = navController)
        }
    }
}
