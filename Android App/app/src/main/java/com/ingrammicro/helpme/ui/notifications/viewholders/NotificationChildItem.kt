package com.ingrammicro.helpme.ui.notifications.viewholders

import android.os.Parcelable
import com.ingrammicro.helpme.data.model.User
import kotlinx.android.parcel.Parcelize

data class NotificationChildItem (
    val msg: String,
    val helpid: String,
    val catId: String,
    val helpType: String
   )


