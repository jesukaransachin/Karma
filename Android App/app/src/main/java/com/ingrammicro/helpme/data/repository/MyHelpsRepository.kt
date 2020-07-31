package com.ingrammicro.helpme.data.repository

import android.icu.text.SimpleDateFormat
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.ingrammicro.helpme.data.model.AcceptHelpResponse
import com.ingrammicro.helpme.data.model.HelpItemResponse
import com.ingrammicro.helpme.data.model.User
import com.ingrammicro.helpme.data.remote.MyHelpsService
import com.ingrammicro.helpme.data.remote.NetworkCall
import com.ingrammicro.helpme.data.remote.ProfileService
import com.ingrammicro.helpme.data.remote.Resource
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class MyHelpsRepository {

    private val myHelpsService by lazy {
        MyHelpsService.create()
    }

    fun getUserHelps(id: String) : MutableLiveData<Resource<HelpItemResponse>> {
        val body = JsonObject()
        body.addProperty("userId", id)
        return NetworkCall<HelpItemResponse>().makeCall(myHelpsService.getUserHelps(body))
    }

    fun acceptUserHelp(helpId: String, userId: String, dateField : String) : MutableLiveData<Resource<AcceptHelpResponse>> {
        val body = JsonObject()
        val timeStamp = Timestamp(System.currentTimeMillis())
        body.addProperty("pinId", helpId)
        body.addProperty("status", 1)
        body.addProperty("userId", userId)
        body.addProperty("dateField", dateField)
        return NetworkCall<AcceptHelpResponse>().makeCall(myHelpsService.acceptUserHelp(body))
    }

    fun closeHelp(helpId: String,dateField: String) : MutableLiveData<Resource<AcceptHelpResponse>> {
        val body = JsonObject()
        val timeStamp = Timestamp(System.currentTimeMillis())
        body.addProperty("pinId", helpId)
        body.addProperty("status", 2)
        body.addProperty("userId", "")
        body.addProperty("dateField", dateField)
        return NetworkCall<AcceptHelpResponse>().makeCall(myHelpsService.acceptUserHelp(body))
    }

}