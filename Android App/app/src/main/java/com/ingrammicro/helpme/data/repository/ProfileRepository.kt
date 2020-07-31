package com.ingrammicro.helpme.data.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.ingrammicro.helpme.data.model.Login
import com.ingrammicro.helpme.data.model.User
import com.ingrammicro.helpme.data.model.Verification
import com.ingrammicro.helpme.data.remote.AuthService
import com.ingrammicro.helpme.data.remote.NetworkCall
import com.ingrammicro.helpme.data.remote.ProfileService
import com.ingrammicro.helpme.data.remote.Resource

class ProfileRepository {
    
    private val profileService by lazy {
        ProfileService.create()
    }

    fun getUserProfile(id: String) : MutableLiveData<Resource<User>> {
        val body = JsonObject()
        body.addProperty("id", id)
        return NetworkCall<User>().makeCall(profileService.getUserProfile(body))
    }

}