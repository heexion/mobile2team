package com.example.mobile2team.Screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile2team.ViewModel.UserViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("프로필 정보", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("이름: ${userViewModel.name}")
        Text("아이디: ${userViewModel.id}")

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                userViewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ){
                Text("로그아웃", color = Color.White)
        }
    }
}


