package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HelpResponse(
    @SerializedName("result")
    @Expose
    var helps: ArrayList<Help>,
    var categories: ArrayList<Category>,
    val error: String
)