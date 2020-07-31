package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("msg")
    @Expose
    val msg: String,
    @SerializedName("helpid")
    @Expose
    val helpid: String,
    @SerializedName("category")
    @Expose
    val categories: Category,

    @SerializedName("helpType")
    @Expose
    val helpType: String,

    @SerializedName("dateField")
    @Expose
    val datetime: String


)
