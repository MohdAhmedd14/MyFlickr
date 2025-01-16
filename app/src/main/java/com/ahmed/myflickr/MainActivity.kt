package com.ahmed.myflickr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ahmed.myflickr.screens.FlickrSearchApp
import com.ahmed.myflickr.screens.ImageDetailScreen
import com.ahmed.myflickr.viewmodels.FlickrImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "search") {
                composable("search") {
                    FlickrSearchApp(navController)
                }
                composable("details") {
                    val image =
                        navController.previousBackStackEntry?.savedStateHandle?.get<FlickrImage>("selectedImage")
                    image?.let {
                        ImageDetailScreen(image = it, onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}