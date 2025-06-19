package com.example.mobile2team.Screen

import android.util.Log
import android.widget.RatingBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile2team.ViewModel.ReviewViewModel
import com.example.mobile2team.Data.model.Review
import com.example.mobile2team.ViewModel.UserViewModel

//@Preview
@Composable
fun ReviewScreen(
    facilityId: String,
    userViewModel: UserViewModel,
    viewModel: ReviewViewModel = viewModel()
) {
    // 화면이 로드될 때 리뷰를 가져옴
    LaunchedEffect(facilityId) {
        viewModel.fetchReviews(facilityId)
    }

    // UserViewModel에서 현재 로그인한 사용자의 name 가져오기
    val userId = userViewModel.name.ifEmpty { "me" }

    Log.d("ReviewScreen", "Current User ID: $userId")

    val content by viewModel::content
    val rating by viewModel::rating
    val error by viewModel::errorMessage
    val userReviews by viewModel::userReviews

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            item {
                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.content = it },
                    label = { Text("리뷰 내용을 작성하세요.")},
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                RatingBar(rating = rating, onRatingChanged = { viewModel.rating = it })
            }



            item {
                error?.let { Text(it, color = Color.Red) }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                Button(
                    onClick = { viewModel.submitReview(facilityId, userId) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF005500)),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("리뷰 제출", color = Color.White, fontSize = 16.sp)
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(userReviews, key = { it.id }) { review ->
                ReviewItem(review)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("작성자: ${review.userId}", style = MaterialTheme.typography.labelSmall)
        Text("⭐".repeat(review.rating))
        Text(review.content)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        for (i in 1..5) {
            Text(
                text = if (i <= rating) "⭐" else "☆",
                modifier = Modifier
                    .padding(4.dp)
                    .clickable { onRatingChanged(i) },
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
