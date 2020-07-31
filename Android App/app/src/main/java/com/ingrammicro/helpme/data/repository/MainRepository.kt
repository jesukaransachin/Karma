package com.ingrammicro.helpme.data.repository

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.data.model.*
import com.ingrammicro.helpme.data.remote.ApiService
import com.ingrammicro.helpme.data.remote.NetworkCall
import com.ingrammicro.helpme.data.remote.Resource
import okhttp3.MediaType
import okhttp3.RequestBody

class MainRepository {

    private val apiService by lazy {
        ApiService.create()
    }
//    fun updateProfile(id: String?,name : String?,email: String?, address: String?, phone: String? ): MutableLiveData<Resource<UpdateProfile>> {

    fun getHelps(
        query: String,
        categoryIds: ArrayList<String>
    ): MutableLiveData<Resource<HelpResponse>> {

        val latitude = HelpMeApplication.prefs?.getLatitude()
        val longitude = HelpMeApplication.prefs?.getLongitude()

        val body = JsonObject()
        body.addProperty("lat", latitude)
        body.addProperty("lng", longitude)

        if (!TextUtils.isEmpty(query)) {
            body.addProperty("search", query)
        }

        if (!categoryIds.isNullOrEmpty()) {
            val ids = JsonArray()
            for (id in categoryIds) {
                ids.add(id)
            }
            body.add("categoryId", ids)
        }

        return NetworkCall<HelpResponse>().makeCall(apiService.fetchHelps(body))
    }

    fun help(
        helpId: String,
        userId: String,
        dataField : String
    ): MutableLiveData<Resource<String>> {
        val body = JsonObject()
        body.addProperty("pinId", helpId)
        body.addProperty("userId", userId)
        body.addProperty("dateField", dataField)
        return NetworkCall<String>().makeCall(apiService.help(body))
    }

    fun fetchHeroes(): MutableLiveData<Resource<HeroesResponse>> {
        return NetworkCall<HeroesResponse>().makeCall(apiService.fetchHeroes())
    }

    fun updateProfile(
        name: String?,
        email: String?,
        address: String?,
        phone: String?,
        age : String?,
        profile: JsonObject?,
        document: JsonObject?,
        contactVia : Int
    ): MutableLiveData<Resource<UpdateProfile>> {
        val body = JsonObject()
        body.addProperty("id", HelpMeApplication.prefs!!.fetchUserId())
        body.addProperty("name", name)
        body.addProperty("email", email)
        body.addProperty("address", address)
        body.addProperty("phone", phone)
        body.addProperty("age", age)
        body.addProperty("contactVia", contactVia)
        body.add("profile", profile)
        body.add("document", document)



        return NetworkCall<UpdateProfile>().makeCall(apiService.updateProfile(body))
    }


    fun createHelp(
        helpType: String?,
        title: String?,
        description: String?,
        files: JsonArray?,
        categoryId: String?,
        priority: String?,
        videoName: String?
    ): MutableLiveData<Resource<CreateHelp>> {
        val body = JsonObject()
        body.addProperty("userid", HelpMeApplication.prefs!!.fetchUserId())
        body.addProperty("helptype", helpType)
        body.addProperty("title", title)
        body.addProperty("description", description)
        body.add("files", files)
        body.addProperty("categoryId", categoryId)
        body.addProperty("priority", priority)
        body.addProperty("videoName", videoName)

        return NetworkCall<CreateHelp>().makeCall(apiService.createHelp(body))
    }

    fun fetchNotification(id: String) : MutableLiveData<Resource<NotificationResponse>> {
        val body = JsonObject()
        body.addProperty("userId", id)
        return NetworkCall<NotificationResponse>().makeCall(apiService.fetchNotification(body))
    }
    fun fetchCategories() : MutableLiveData<Resource<java.util.ArrayList<Category>>> {
        return NetworkCall<java.util.ArrayList<Category>>().makeCall(apiService.fetchCategories())
    }

    fun uploadVideo(
        id: String,
        type: String,
        fileName: String,
        video: ByteArray
    ): MutableLiveData<Resource<UploadVideoResponse>> {
        val requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), video)
        return NetworkCall<UploadVideoResponse>().makeCall(apiService.uploadVideo(
            fileName,
            id,
            type,
            requestBody
        ))
    }
}