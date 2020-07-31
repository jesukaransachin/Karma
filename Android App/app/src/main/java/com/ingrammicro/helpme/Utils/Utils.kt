package com.ingrammicro.helpme.utils

import android.content.Context
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit



         fun  covertTimeToText(dataDate: String?): String? {
            var convTime: String? = null
            val prefix = ""
            val suffix = "Ago"
            try {

                val dateFormat = SimpleDateFormat("dd/MM/yyyy@HH:mm:ss")
                val pasTime: Date = dateFormat.parse(dataDate)
                val nowTime = Date()
                val dateDiff: Long = nowTime.getTime() - pasTime.getTime()
                val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
                val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
                val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
                val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
                if (second < 60) {
                    convTime = "now"//"$second sec $suffix"
                } else if (minute < 60) {
                    convTime = "$minute mins $suffix"
                } else if (hour < 24) {
                    convTime = "$hour hr $suffix"
                } else if (day >= 7) {
                    convTime = if (day > 360) {
                        (day / 360).toString() + " Years " + suffix
                    } else if (day > 30) {
                        (day / 30).toString() + " Months " + suffix
                    } else {
                        (day / 7).toString() + " Week " + suffix
                    }
                } else if (day < 7) {
                    convTime = "$day days $suffix"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
//                Log.e("ConvTimeE", e.getMessage())
            }
            return convTime
        }


