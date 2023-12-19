package com.example.storyapp_ade.view.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp_ade.data.repository.Repository

class SignUpViewModel(private val repository: Repository) : ViewModel() {
    fun uploadData(name: String, email: String, password: String) =
        repository.uploadData(name, email, password)
}