package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Hero(
    @SerializedName("_id")
    @Expose
    val id: String,
    @SerializedName("_rev")
    @Expose
    val rev: String,
    val name: String,
    val phone: String,
    val profileUrl: String,
    val documentPicUrl: String,
    val email: String,
    @SerializedName("karmapoints")
    @Expose
    val karmaPoints: Int,
    @SerializedName("helpasked")
    @Expose
    val askedForHelp: Int,
    @SerializedName("helpextended")
    @Expose
    val helpExtended: Int,
    val isSpecial: Boolean = false
)