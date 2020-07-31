package com.ingrammicro.helpme.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.core.content.FileProvider
import com.ingrammicro.helpme.BuildConfig
import com.ingrammicro.helpme.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object AppUtils {

    fun getErrorMessage(state: String): Int {
        return when (state) {
            STATE_ERROR_NETWORK -> R.string.error_rest_network
            STATE_ERROR_TIMEOUT -> R.string.error_rest_timeout
            STATE_ERROR_UNEXPECTED -> R.string.error_rest_unexpected
            else -> -1
        }
    }

    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"
        try {

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
           if (dateFormat != null && !dateFormat.equals("")) {
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
           } else {
               return ""
           }
        } catch (e: ParseException) {
            e.printStackTrace()
//                Log.e("ConvTimeE", e.getMessage())
        }
        return convTime
    }

    fun getScreenShot(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    @Throws(IOException::class)
    private fun createImageFile(context: Context, bitmap: Bitmap): Uri {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        val photoFile = File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )

        val stream: OutputStream = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.flush()
        stream.close()

        return FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID + ".provider",
            photoFile
        )
    }

    fun shareImage(context: Context, view: View) {

        val bitmap = getScreenShot(view)

        val photoURI: Uri? = try {
            createImageFile(context, bitmap)
        } catch (ex: IOException) {
            null
        }

        photoURI?.also {
            val shareIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                putExtra(Intent.EXTRA_STREAM, photoURI)
                type = "image/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }, null)
            context.startActivity(shareIntent)
        }
    }
}