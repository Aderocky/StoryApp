package com.example.storyapp_ade.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp_ade.api.response.ListStoryItem
import com.example.storyapp_ade.data.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun story(): LiveData<PagingData<ListStoryItem>> {
        return repository.story().cachedIn(viewModelScope)
    }

}