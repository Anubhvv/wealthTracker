package com.example.wealthtracker
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.BottomNavigation
import androidx.compose.ui.graphics.*
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WealthTrackerApp(context: Context) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.Assets
    )

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            BottomNavigation{
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->

                    BottomNavigationItem(
                        icon = {
                            painterResource(id = if(screen.route == Screen.Home.route) R.mipmap.ic_home_foreground else R.mipmap.ic_asset)?.let { icon ->
                                Icon(
                                    painter = icon,
                                    contentDescription = null, // You can provide a content description if needed
                                    modifier = Modifier.size(30.dp,30.dp), // Adjust padding as needed
                                    tint = MaterialTheme.colorScheme.onPrimary//Color.White // Default color

                                )
                            }
                        },
                        label = {
                            Text(
                                color = MaterialTheme.colorScheme.onPrimary,
                                text = screen.route
                            )
                        },
                        modifier = Modifier.background(

                            color = if (currentRoute == screen.route) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                       ,
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )


                }
            }
        }
    ) {
        NavHost(navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) { HomeScreen(context) }
            composable(Screen.Assets.route) { AssetScreen(context) }
        }
    }
}
