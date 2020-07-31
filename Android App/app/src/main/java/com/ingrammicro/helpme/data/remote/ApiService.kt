package com.ingrammicro.helpme.data.remote

import com.google.gson.JsonObject
import com.ingrammicro.helpme.data.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {


    @POST("maps/home")
   fun fetchHelps(@Body credentials: JsonObject): Call<HelpResponse>

    @POST("maps/readyToHelp")
    fun help(@Body credentials: JsonObject): Call<String>

    @POST("maps/getHeroes")
    fun fetchHeroes(): Call<HeroesResponse>

    @POST("profile/updateProfile")
    fun updateProfile(@Body credentials: JsonObject): Call<UpdateProfile>

    @POST("maps/createHelp")
    fun createHelp(@Body credentials: JsonObject): Call<CreateHelp>

    @POST("profile/getNotifications")
    fun fetchNotification(@Body credentials: JsonObject): Call<NotificationResponse>

    @POST("maps/getCategories")
    fun fetchCategories(): Call<java.util.ArrayList<Category>>

    @POST("upload/uploadVideo")
    fun uploadVideo(
    @Header("x-file-name") fileName: String,
    @Header("x-id") helpId: String,
    @Header("x-type") type: String,
    @Body credentials: RequestBody): Call<UploadVideoResponse>

    companion object Factory {
        fun create(): ApiService {
            val retrofit = ServiceGenerator.getRetrofit()
            return retrofit.create(ApiService::class.java);
        }
    }
}