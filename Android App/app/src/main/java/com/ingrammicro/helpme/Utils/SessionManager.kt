package com.ingrammicro.helpme.utils

import android.content.Context
import android.content.SharedPreferences
import com.ingrammicro.helpme.R

class SessionManager(context: Context?) {
    private var prefs: SharedPreferences =
        context!!.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_AUTH_TOKEN = "user_auth_token"
        const val USER_REFRESH_TOKEN = "user_refresh_token"
        const val USER_ID = "user_id"
        const val LATITUDE = "user_lat"
        const val LONGITUDE = "user_lng"
        const val FCM_TOKEN = "fcm_token"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_AUTH_TOKEN, token)
        editor.apply()
    }

    fun saveRefreshToken(refreshToken: String) {
        val editor = prefs.edit()
        editor.putString(USER_REFRESH_TOKEN, refreshToken)
        editor.apply()
    }

    fun saveFCMToken(refreshToken: String) {
        val editor = prefs.edit()
        editor.putString(FCM_TOKEN, refreshToken)
        editor.apply()
    }

    fun saveUserId(id: String) {
        val editor = prefs.edit()
        editor.putString(USER_ID, id)
        editor.apply()
    }

    fun saveUserLat( id: String?) {
        var idLat : String? = id
        if (null == id){
            idLat = "0.0"
        }
        val editor = prefs.edit()
        editor.putString(LATITUDE, idLat)
        editor.apply()
    }

    fun saveUserLng(id: String?) {
        var idLng : String? = id
        if (null == idLng){
            idLng = "0.0"
        }
        val editor = prefs.edit()
        editor.putString(LONGITUDE, id)
        editor.apply()
    }

    fun getLatitude(): String? {
        return prefs.getString(LATITUDE, "0.0")
    }

    fun getLongitude(): String? {
        return prefs.getString(LONGITUDE, "0.0")
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_AUTH_TOKEN, null)
    }

    fun fetchRefreshToken(): String? {
        return prefs.getString(USER_REFRESH_TOKEN, null)
    }

    fun getFCMToken(): String? {
        return prefs.getString(FCM_TOKEN, null)
    }

    fun fetchUserId(): String? {
        return prefs.getString(USER_ID, null)
    }

    fun clearAll() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}