package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Verification(
    @SerializedName("_id")
    @Expose
    val id: String,
    @SerializedName("_rev")
    @Expose
    val rev: String,
    val phone: String,
    @SerializedName("OTP")
    @Expose
    val otp: Int,
    val lat: String?,
    val lng: String?,
    val name: String,
    val profileUrl: String,
    val accessToken: String,
    val refreshToken: String,
    val error: String
)