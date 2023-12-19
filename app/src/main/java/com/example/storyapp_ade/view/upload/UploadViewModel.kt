package com.example.storyapp_ade.view.upload

import androidx.lifecycle.ViewModel
import com.example.storyapp_ade.data.repository.Repository
import com.google.android.gms.maps.model.LatLng
import java.io.File

class UploadViewModel(private val repository: Repository) : ViewModel() {
    fun uploadImage(file: File, description: String, latLng: LatLng?) =
        repository.uploadImage(file, description, latLng)
}