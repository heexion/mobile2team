package com.example.mobile2team.Data.assets

import com.example.mobile2team.Data.model.FacilityDetail

//외부 정보를 우리껄로 변환
fun FacilityFeature.toFacilityDetail(): FacilityDetail {
    return FacilityDetail(
        id = id.toString(),
        name = properties.fac_nam,
        address = properties.fac_n_add ?: properties.fac_o_add.orEmpty(),
        phoneNumber = properties.fac_tel,
        latitude = geometry.coordinates.getOrNull(1),
        longitude = geometry.coordinates.getOrNull(0)
        // 나머지 필드는 기본값
    )
}