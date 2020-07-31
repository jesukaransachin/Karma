package com.ingrammicro.helpme.data.model;

data class User (
    val name: String,
    val email: String,
    val phone: String,
    val age: String,
    val longAddress: String,
    val shortAddress: String,
    val profileUrl: String,
    val documentPicUrl: String,
    val karmapoints: Int,
    val helpasked: Int,
    val helpextended: Int,
    val contactVia: Int = 1
)
