package com.example.storyapp_ade.di

import android.content.Context
import com.example.storyapp_ade.api.retrofit.ApiConfig
import com.example.storyapp_ade.data.database.StoryDatabase
import com.example.storyapp_ade.data.repository.Repository
import com.example.storyapp_ade.data.pref.UserPreference
import com.example.storyapp_ade.data.pref.dataStore

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository? {
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        val token = runBlocking {
            pref.getSession().first()
        }
        val apiService = ApiConfig.getApiService(token.token)
        return Repository.getInstance(apiService, database, pref, true)
    }
}