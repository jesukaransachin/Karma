package com.ingrammicro.helpme.data.remote

import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceGenerator {

    private val BASE_URL = "https://nodejs-express-app-pdest.eu-gb.mybluemix.net/"

    private val CONTENT_TYPE = "application/json"
    private val HEADER_CONTENT_TYPE = "Content-Type"
    private val HEADER_AUTHORIZATION = "Authorization"


    private val client = OkHttpClient().newBuilder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(headersInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    private val authClient = OkHttpClient().newBuilder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(headersAuthInterceptor())
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(authClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private fun headersInterceptor() = Interceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .addHeader(HEADER_AUTHORIZATION, "Bearer " +HelpMeApplication.prefs!!.fetchAuthToken())
                .build()
        )
    }

    private fun headersAuthInterceptor() = Interceptor { chain ->
        chain.proceed(
            chain.request().newBuilder()
                .addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE)
                .build()
        )
    }

    fun getRetrofit(): Retrofit {
        return retrofit
    }

    fun getAuthRetrofit(): Retrofit {
        return authRetrofit
    }
}