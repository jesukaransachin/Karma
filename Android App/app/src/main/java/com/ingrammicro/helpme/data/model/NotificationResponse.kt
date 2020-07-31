package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



data class NotificationResponse(
    @SerializedName("data")
    @Expose
    val notificationLst: ArrayList<Notification>

)