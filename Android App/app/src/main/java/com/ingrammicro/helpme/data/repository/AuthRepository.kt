package com.ingrammicro.helpme.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.data.model.Login
import com.ingrammicro.helpme.data.model.UpdateProfile
import com.ingrammicro.helpme.data.model.Verification
import com.ingrammicro.helpme.data.remote.AuthService
import com.ingrammicro.helpme.data.remote.NetworkCall
import com.ingrammicro.helpme.data.remote.Resource

class AuthRepository {

    private val authService by lazy {
        AuthService.create()
    }

    fun login(phone: String): MutableLiveData<Resource<Login>> {
        val body = JsonObject()
        body.addProperty("phone", phone)
        return NetworkCall<Login>().makeCall(authService.login(body))
    }

    fun verify(id: String): MutableLiveData<Resource<Verification>> {

        val latitude = HelpMeApplication.prefs?.getLatitude()
        val longitude = HelpMeApplication.prefs?.getLongitude()
        val fcmToken = HelpMeApplication.prefs?.getFCMToken()

        val body = JsonObject()
        body.addProperty("id", id)
        body.addProperty("lat", latitude)
        body.addProperty("lng", longitude)
        body.addProperty("regToken", fcmToken)
        return NetworkCall<Verification>().makeCall(authService.verify(body))
    }

//    fun updateProfile(id: String,email: String, address: String, phone: String ): MutableLiveData<Resource<UpdateProfile>> {
//        val body = JsonObject()
//        body.addProperty("id", id)
//        return NetworkCall<UpdateProfile>().makeCall(authService.updateProfile(body))
//    }
}