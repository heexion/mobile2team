package com.example.mobile2team.Util

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobile2team.Data.model.FacilityDetail
import com.example.mobile2team.R

@Composable
fun FacilityInfoPanel(
    facility: FacilityDetail,
    onToggleFavorite: () -> Unit,
    onCallPhone: (String) -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(12.dp) // 전체 카드 주변 여백
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)) // ✅ 그림자 효과
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = facility.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Text(
                    text = facility.address,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Text(
                    text = facility.phoneNumber ?: "-",
                    fontSize = 14.sp
                )
                Text("⭐ ${facility.averageRating ?: "0.0"}")
                Text("${facility.reviewCount}개 리뷰  리뷰 보기")
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = "즐겨찾기",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_call),
                        contentDescription = "전화",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_bus),
                        contentDescription = "길찾기",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 이미지가 있으면 AsyncImage 사용 권장
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text("기관 사진", fontSize = 12.sp)
            }
        }
    }
}
