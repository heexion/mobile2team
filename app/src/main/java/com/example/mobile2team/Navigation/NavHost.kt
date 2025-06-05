package com.example.mobile2team.Navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobile2team.Screen.DetailScreen
import com.example.mobile2team.Screen.LoginScreen
import com.example.mobile2team.Screen.MainScreen
import com.example.mobile2team.Screen.ProfileScreen
import com.example.mobile2team.Screen.RegisterScreen
import com.example.mobile2team.Screen.SearchScreen
import com.example.mobile2team.ViewModel.UserViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController, userViewModel)
        }
        composable("search") {
            SearchScreen()
        }
        composable("login") {
            LoginScreen(navController = navController, userViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, userViewModel)
        }
        composable("profile") {
            ProfileScreen(navController, userViewModel)
        }
        composable("detail/{facilityId}") { backStackEntry ->
            val facilityId = backStackEntry.arguments?.getString("facilityId")
            if (facilityId != null) {
                DetailScreen(navController = navController, facilityId = facilityId)
            } else {
                // 에러 처리 또는 기본값 처리
            }
        }



    }
}
