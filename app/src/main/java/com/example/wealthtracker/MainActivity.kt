package com.example.wealthtracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wealthtracker.ui.theme.WealthTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("asset", "hey")
        var wealthData = loadWealthData(context = this)
        if(wealthData.categories.isEmpty()){
            Log.e("asset","empty")
        }
        else{
            Log.e("asset","not empty")
        }
        if (wealthData.categories.isEmpty()) {
            wealthData = WealthData(
                categories = listOf(
                    CategoryData("Bank Balance",0.0),
                    CategoryData("Equity/ Stocks",0.0),
                    CategoryData("Real Estate",0.0)
                )
            )
            saveWealthData(context = this, wealthData = wealthData)
            wealthData = loadWealthData(context = this)
            if(wealthData.categories.isEmpty()){
                Log.e("asset","2.empty")
            }
            else{
                Log.e("asset","2.not empty")
            }
            for (category in wealthData.categories) {
                Log.e("asset","iterating")
                Log.e("asset saved loaded","Category: ${category.name} Value: ${category.value}")
            }
        }
        setContent {
            WealthTrackerTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) { WealthTrackerApp(this@MainActivity) }

                }
            }
        }
    }
}


