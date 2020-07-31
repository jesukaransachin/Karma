package com.ingrammicro.helpme.data.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Category  (
    @SerializedName("_id")
    @Expose
    val id: String,
    @SerializedName("_rev")
    @Expose
    val rev: String,
    val name: String,
    val url: String,
    val categoryId: String
)
