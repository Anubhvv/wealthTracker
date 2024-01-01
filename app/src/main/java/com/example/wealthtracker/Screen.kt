package com.example.wealthtracker

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object Assets : Screen("Assets")
}
