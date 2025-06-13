package com.example.mobile2team.Navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mobile2team.Screen.DetailScreen
import com.example.mobile2team.Screen.FavoriteScreen
import com.example.mobile2team.Screen.LoginScreen
import com.example.mobile2team.Screen.MainScreen
import com.example.mobile2team.Screen.ProfileScreen
import com.example.mobile2team.Screen.RegisterScreen
import com.example.mobile2team.Screen.ReviewScreen
import com.example.mobile2team.Screen.RouteScreen
import com.example.mobile2team.Screen.SearchScreen
import com.example.mobile2team.ViewModel.UserViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.Data.assets.toFacilityDetail
import com.google.gson.Gson
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun AppNavHost(navController: NavHostController) {
    val userViewModel: UserViewModel = viewModel()
    val context = LocalContext.current

    // allFacilities를 AppNavHost에서 생성
    val allFacilities by remember {
        mutableStateOf(
            run {
                val jsonString = context.assets.open("facility.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val vworldResponse = gson.fromJson(jsonString, com.example.mobile2team.Data.assets.VWorldResponse::class.java)
                vworldResponse.response.result.featureCollection.features.map { it.toFacilityDetail() }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(navController = navController, userViewModel)
        }
        composable("search") {
            SearchScreen(navController = navController)
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


            //val facilityId = backStackEntry.arguments?.getString("facilityId")

            if (facilityId != null) {
                DetailScreen(navController = navController
                    , facilityId = facilityId
                    ,userViewModel = userViewModel)
            } else {
                // 에러 처리 또는 기본값 처리
            }
        }

        composable("favorites") {
            FavoriteScreen(
                navController = navController,
                userViewModel = userViewModel,
                allFacilities = allFacilities
            )
        }
        composable("review/{facilityId}") { backStackEntry ->
            val facilityId = backStackEntry.arguments?.getString("facilityId")
            if (facilityId != null) {
                ReviewScreen(facilityId = facilityId)
            }
        }
        composable(
            route = "route/{lat}/{lng}/{name}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStack ->
            val lat = backStack.arguments!!.getFloat("lat")
            val lng = backStack.arguments!!.getFloat("lng")
            val name = backStack.arguments!!.getString("name") ?: ""

            RouteScreen(
                destinationName = name,
                destinationLat = lat.toDouble(),
                destinationLng = lng.toDouble()
            )
        }




    }
}