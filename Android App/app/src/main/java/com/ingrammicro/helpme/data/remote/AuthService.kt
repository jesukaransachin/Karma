package com.ingrammicro.helpme.data.remote

import com.google.gson.JsonObject
import com.ingrammicro.helpme.data.model.Login
import com.ingrammicro.helpme.data.model.Verification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("auth/login")
    fun login(@Body credentials: JsonObject): Call<Login>

    @POST("auth/verified")
    fun verify(@Body credentials: JsonObject): Call<Verification>

    companion object Factory {
        fun create(): AuthService {
            val retrofit = ServiceGenerator.getAuthRetrofit()
            return retrofit.create(AuthService::class.java);
        }
    }
}