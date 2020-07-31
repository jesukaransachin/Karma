package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("_id")
    @Expose
    val id: String,
    @SerializedName("_rev")
    @Expose
    val rev: String,
    val longAddress: String,
    val shortAddress: String
)