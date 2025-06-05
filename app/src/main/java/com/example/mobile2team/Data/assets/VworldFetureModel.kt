package com.example.mobile2team.Data.assets


//json 데이터 계층구조

data class VWorldResponse(
    val response: VWorldResult
)
data class VWorldResult(
    val result: VWorldFeatureCollection
)
data class VWorldFeatureCollection(
    val featureCollection: FeatureCollection
)

data class FeatureCollection(
    val features: List<FacilityFeature>
)

data class FacilityFeature(
    val id: String,
    val geometry: Geometry,
    val properties: FacilityProperties
)

data class Geometry(
    val coordinates : List<Double>
)

data class FacilityProperties (
    val fac_nam: String,
    val fac_tel : String?,
    val fac_n_add: String?,
    val fac_o_add: String?
)
