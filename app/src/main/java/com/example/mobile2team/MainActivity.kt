package com.example.mobile2team

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.mobile2team.Data.assets.VWorldResponse
import com.example.mobile2team.Data.assets.toFacilityDetail
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.Data.repository.FacilityRepository
import com.example.mobile2team.Navigation.AppNavHost
import com.example.mobile2team.ui.theme.Mobile2teamTheme
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val jsonString = assets.open("facility.json").bufferedReader().use { it.readText() }
        val parsedList: List<FacilityDetail> = parseFacilityJson(jsonString)
        FacilityRepository.getInstance().setFacilityList(parsedList)
        setContent {
            Mobile2teamTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController)

            }
            }
        }
    }
fun parseFacilityJson(jsonString: String): List<FacilityDetail> {
    val gson = Gson()
    val vworld = gson.fromJson(jsonString, VWorldResponse::class.java)
    val facilities = vworld.response.result.featureCollection.features.map { it.toFacilityDetail() }
    return facilities
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Mobile2teamTheme {
        Greeting("Android")
    }
}