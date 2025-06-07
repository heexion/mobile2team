package com.example.mobile2team.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile2team.Data.model.FacilityDetail
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.jvm.java

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
}