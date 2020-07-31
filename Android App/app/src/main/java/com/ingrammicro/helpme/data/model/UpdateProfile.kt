package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateProfile(

    @SerializedName("lat")
    @Expose
    val lat: String?,

    @SerializedName("lng")
    @Expose
    val lng: String?,


    @SerializedName("id")
@Expose
val id: String
)

