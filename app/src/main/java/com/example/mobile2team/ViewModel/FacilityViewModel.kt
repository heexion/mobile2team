package com.example.mobile2team.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile2team.Data.assets.VWorldResponse
import com.example.mobile2team.Data.assets.toFacilityDetail
import com.example.mobile2team.Data.model.FacilityDetail
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.jvm.java
import com.google.gson.Gson
import java.io.InputStreamReader

class FacilityViewModel : ViewModel() {
    private val _facilities = MutableStateFlow<List<FacilityDetail>>(emptyList())
    val facilities: StateFlow<List<FacilityDetail>> = _facilities

    init {
        fetchFacilities()
    }

    private fun fetchFacilities() {
        val database = FirebaseDatabase.getInstance()
        val facilitiesRef = database.getReference("facilities")
        facilitiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<FacilityDetail>()
                for (facilitySnapshot in snapshot.children) {
                    val facility = facilitySnapshot.getValue(FacilityDetail::class.java)
                    facility?.let { list.add(it) }
                }
                _facilities.value = list
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addFacility(context: Context) {
        val database = FirebaseDatabase.getInstance()
        val facilitiesRef = database.getReference("facilities")

        // facilities 노드가 비어있는지 확인
        facilitiesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) { // 데이터가 존재하지 않을 때만 추가
                    // JSON 파일 읽기
                    val inputStream = context.assets.open("facility.json")
                    val reader = InputStreamReader(inputStream)
                    
                    // JSON 파싱
                    val vWorldResponse: VWorldResponse = Gson().fromJson(reader, VWorldResponse::class.java)
                    val facilities: List<FacilityDetail> = vWorldResponse.response.result.featureCollection.features.map { it.toFacilityDetail() }
                    
                    // Firebase에 저장
                    facilities.forEach { facility ->
                        val safeFacilityId = facility.id
                            .replace(".", ",")
                            .replace("#", ",")
                            .replace("$", ",")
                            .replace("[", ",")
                            .replace("]", ",")
                        
                        facilitiesRef.child(safeFacilityId).setValue(facility)
                            .addOnSuccessListener {
                                // 성공적으로 추가됨
                            }
                            .addOnFailureListener { exception ->
                                // 추가 실패
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
            }
        })
    }
}