package com.example.mobile2team.Screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile2team.ViewModel.UserViewModel

//preview 작동하기 위해 view model 인수로 안넘겨줄려고 분리
@Composable
fun MainScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    MainScreenBody(
        navController = navController,
        isLoggedIn = userViewModel.isLoggedIn
    )
}

@Composable
fun MainScreenBody(
    navController: NavController,
    isLoggedIn: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(70.dp))

        Text(
            text = "WellFit",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // 가운데로 밀어주는 핵심
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 주변 복지시설 찾기
            OutlinedButton(
                onClick = {  navController.navigate("search")
                    //navController.navigate("detail/123") // 시설 id가 123인 곳으로 이동
                    //navController.navigate("detail/${facility.id}") (나중에 이거로 바꿔야함)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, Color(0xFF007F00)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("주변 복지시설 찾기", color = Color.Black, fontSize = 16.sp)
            }


            Spacer(modifier = Modifier.height(16.dp))  //버튼 사이 간격

            // 즐겨찾기 목록
            OutlinedButton(
                onClick = { navController.navigate("favorites") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, Color(0xFF007F00)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("즐겨찾기 목록", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 내 정보
            OutlinedButton(
                onClick = {
                    if (isLoggedIn) {
                        navController.navigate("profile")
                    } else {
                        navController.navigate("login")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, Color(0xFF007F00)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "내 정보",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("내 정보", color = Color.Black, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        // 하단 홈 아이콘
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "홈",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .size(28.dp),
            tint = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    val fakeNavController = rememberNavController()

    MainScreenBody(navController = fakeNavController, isLoggedIn=true)

}