package com.example.mobile2team.Screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.Util.FacilityInfoPanel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



@Composable
fun SearchScreen(navController: NavController,modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val selectedFacility = remember { mutableStateOf<FacilityDetail?>(null) }
    val context = LocalContext.current


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "주변 복지시설",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 검색창
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            placeholder = { Text("검색") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색 아이콘"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF005500),
                unfocusedBorderColor = Color(0xFF007F00),
                cursorColor = Color(0xFF005500)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 지도 화면 Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp)
                .background(Color(0xFFE0E0E0)),
            contentAlignment = Alignment.Center
        ) {
            MapScreen(
                searchQuery = searchQuery.text,
                selectedFacility = selectedFacility,
                onFacilitySelected = { selectedFacility.value = it }
            )


            //Text("지도 화면", color = Color.DarkGray)
        }


        // 상세 정보 박스
        selectedFacility.value?.let { facility ->
            FacilityInfoPanel(
                facility = facility,
                onToggleFavorite = { /* 즐겨찾기 기능 */ },
                onCallPhone = { phoneNumber -> makePhoneCall(context, phoneNumber) },
                onClick = { navController.navigate("detail/${facility.id}") }

            )
        }

    }
}

//@Preview
//@Composable
//private fun DetailScreenPreview() {

//    SearchScreen()
//}

fun loadFavorites(userId: String) {
    val favoriteRef = FirebaseDatabase.getInstance()
        .getReference("users")
        .child(userId)
        .child("favorites")
    favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val loadedFavorites = mutableMapOf<String, Boolean>()
            for (child in snapshot.children) {
                val facilityId = child.key ?: continue
                val isFavorite = child.getValue(Boolean::class.java) ?: false
                loadedFavorites[facilityId] = isFavorite
            }
            //favorites.clear()
            //favorites.putAll(loadedFavorites)
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}

//    val fakeNavController = rememberNavController()
//
//    SearchScreen(fakeNavController)
//}

