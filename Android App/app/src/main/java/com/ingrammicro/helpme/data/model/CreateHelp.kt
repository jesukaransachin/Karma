package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateHelp(

    @SerializedName("result")
    @Expose
    val result: String,
    val id: String
)