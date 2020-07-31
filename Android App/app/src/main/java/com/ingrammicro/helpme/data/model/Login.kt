package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Login(
    val ok: Boolean,
    val id: String,
    val rev: String,
    @SerializedName("OTP")
    @Expose
    val otp: Int,
    val error: String
)