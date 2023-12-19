package com.example.storyapp_ade.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp_ade.data.repository.Repository
import com.example.storyapp_ade.di.Injection
import com.example.storyapp_ade.view.detail.DetailViewModel
import com.example.storyapp_ade.view.login.LoginViewModel
import com.example.storyapp_ade.view.main.MainViewModel
import com.example.storyapp_ade.view.maps.MapsViewModel
import com.example.storyapp_ade.view.signup.SignUpViewModel
import com.example.storyapp_ade.view.upload.UploadViewModel
import com.example.storyapp_ade.view.welcome.WelcomeViewModel

class ViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }

            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, isLogin: Boolean): ViewModelFactory {
            synchronized(this) {
                if (INSTANCE == null || isLogin) {
                    INSTANCE = Injection.provideRepository(context)?.let { ViewModelFactory(it) }
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}