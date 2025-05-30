package com.example.mobile2team.Screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mobile2team.ViewModel.ReviewViewModel
import com.example.mobile2team.Data.model.Review

//@Preview
@Composable
fun ReviewScreen(viewModel: ReviewViewModel = viewModel()) {
    val content by viewModel::content
    val rating by viewModel::rating
    val error by viewModel::errorMessage
    val userReviews by viewModel::userReviews

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TextField(
                value = content,
                onValueChange = { viewModel.content = it },
                label = { Text("리뷰 내용") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            RatingBar(rating = rating, onRatingChanged = { viewModel.rating = it })
        }

        item {
            error?.let { Text(it, color = Color.Red) }
        }

        item {
            Button(
                onClick = { viewModel.submitReview() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("리뷰 제출")
            }
        }

        items(userReviews, key = { it.id }) { review ->
            ReviewItem(review)
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
