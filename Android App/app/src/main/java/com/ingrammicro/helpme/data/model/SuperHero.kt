package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SuperHero(
    val name: String,
    val phone: String,
    val profilePic: Int,
    val description: Int,
    val email: String,
    val videoId: String,
    val helpExtended: Int,
    val isSpecial: Boolean = false
)