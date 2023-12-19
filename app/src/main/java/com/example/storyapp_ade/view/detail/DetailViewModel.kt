package com.example.storyapp_ade.view.detail

import androidx.lifecycle.ViewModel
import com.example.storyapp_ade.data.repository.Repository

class DetailViewModel(private val repository: Repository) : ViewModel() {

    fun detail(id: String) = repository.detail(id)
}