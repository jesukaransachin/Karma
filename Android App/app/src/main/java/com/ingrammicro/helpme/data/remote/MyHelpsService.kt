package com.ingrammicro.helpme.data.remote

import com.google.gson.JsonObject
import com.ingrammicro.helpme.data.model.AcceptHelpResponse
import com.ingrammicro.helpme.data.model.HelpItem
import com.ingrammicro.helpme.data.model.HelpItemResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MyHelpsService {

    @POST("/maps/myHelps")
    fun getUserHelps(@Body credentials: JsonObject): Call<HelpItemResponse>

    @POST("/maps/updatePinStatus")
    fun acceptUserHelp(@Body credentials: JsonObject): Call<AcceptHelpResponse>

    /**
     * Companion object to create the ApiService
     */
    companion object Factory {
        fun create(): MyHelpsService {
            val retrofit = ServiceGenerator.getRetrofit()
            return retrofit.create(MyHelpsService::class.java);
        }
    }
}