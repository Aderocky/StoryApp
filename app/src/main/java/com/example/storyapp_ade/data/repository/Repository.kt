package com.example.storyapp_ade.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp_ade.api.response.DetailResponse
import com.example.storyapp_ade.api.response.ListStoryItem
import com.example.storyapp_ade.api.retrofit.ApiService
import com.example.storyapp_ade.api.response.RegisterResponse
import com.example.storyapp_ade.api.response.StoryResponse
import com.example.storyapp_ade.api.response.UploadResponse
import com.example.storyapp_ade.data.database.StoryDatabase
import com.example.storyapp_ade.data.paging.StoryRemoteMediator
import com.example.storyapp_ade.data.pref.UserModel
import com.example.storyapp_ade.data.pref.UserPreference
import com.example.storyapp_ade.di.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import com.google.android.gms.maps.model.LatLng


class Repository private constructor(
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun uploadData(name: String, email: String, password: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        } catch (exception: Exception) {
            emit(ResultState.Error("Unexpected Error"))
        }

    }

    fun loginData(email: String, password: String) = liveData {
        emit(ResultState.Loading)
        wrapEspressoIdlingResource {
            try {
                val successResponse = apiService.login(email, password)
                val userModel =
                    UserModel(
                        successResponse.loginResult.name,
                        successResponse.loginResult.token,
                        true
                    )
                saveSession(userModel)
                emit(ResultState.Success(successResponse))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(ResultState.Error(errorResponse.message))
            } catch (exception: Exception) {
                emit(ResultState.Error("Unexpected Error"))
            }
        }
    }

    fun story(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getLocationWithStory() = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.getStoriesWithLocation()
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, StoryResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        } catch (exception: Exception) {
            emit(ResultState.Error("Unexpected Error"))
        }
    }

    fun detail(id: String) = liveData {
        emit(ResultState.Loading)
        try {
            val successResponse = apiService.getDetail(id)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DetailResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        } catch (exception: Exception) {
            emit(ResultState.Error("Unexpected Error"))
        }
    }

    fun uploadImage(imageFile: File, description: String, latLng: LatLng?) = liveData {
        emit(ResultState.Loading)
        val latRequestBody = latLng?.latitude
        val lonRequestBody = latLng?.longitude
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse =
                apiService.uploadImage(multipartBody, requestBody, latRequestBody, lonRequestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(ResultState.Error(errorResponse.message))
        } catch (exception: Exception) {
            emit(ResultState.Error("Unexpected Error"))
        }

    }


    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase,
            userPreference: UserPreference,
            isLogin: Boolean
        ): Repository? {
            if (isLogin) {
                synchronized(this) {
                    instance = Repository(userPreference, storyDatabase, apiService)
                }
            }
            return instance
        }
    }
}