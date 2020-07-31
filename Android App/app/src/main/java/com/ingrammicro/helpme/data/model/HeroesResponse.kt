package com.ingrammicro.helpme.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class HeroesResponse(
    val helpRequested: Int,
    val helpGiven: Int,
    val usersBenefitted: Int,
    var heroes: ArrayList<Hero>,
    val error: String
)