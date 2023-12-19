package com.example.storyapp_ade.view.maps

import androidx.lifecycle.ViewModel
import com.example.storyapp_ade.data.repository.Repository

class MapsViewModel(private val repository: Repository) : ViewModel() {

    fun getLocationWithStory() = repository.getLocationWithStory()

}