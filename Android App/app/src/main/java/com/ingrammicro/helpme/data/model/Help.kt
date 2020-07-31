package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Help(
    @SerializedName("_id")
    @Expose
    val id: String,
    @SerializedName("_rev")
    @Expose
    val rev: String,
    @SerializedName("userid")
    @Expose
    val userId: String,
    @SerializedName("helptype")
    @Expose
    val helpType: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val status: Int,
    val priority: String,
    val latitude: Double,
    val longitude: Double,
    val distance: Double,
    val category: Category,
    val photos: ArrayList<String>,
    @SerializedName("activity")
    @Expose
    var activities: ArrayList<HelpAct>,
    @SerializedName("UserInfo")
    @Expose
    val userInfo: UserInfo
)