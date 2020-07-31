package com.ingrammicro.helpme.data.remote

import com.google.gson.JsonObject
import com.ingrammicro.helpme.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ProfileService {


    @POST("profile/getProfile")
    fun getUserProfile(@Body credentials: JsonObject): Call<User>

    /**
     * Companion object to create the ApiService
     */
    companion object Factory {
        fun create(): ProfileService {
            val retrofit = ServiceGenerator.getRetrofit()
            return retrofit.create(ProfileService::class.java);
        }
    }
}