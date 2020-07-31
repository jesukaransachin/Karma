package com.ingrammicro.helpme.viewholders

import android.os.Parcelable
import com.ingrammicro.helpme.data.model.User
import kotlinx.android.parcel.Parcelize

data class HelpChildItem (
    val hid: String,
    val uid: String,
    val status: Int,
    val fullName: String?,
    val profilePic: String?,
    val phone: String?,
    val email: String?,
    val helpTitle: String?,
    val contactVia: Int)