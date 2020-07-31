package com.ingrammicro.helpme.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TrackLocationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val sessionManager = SessionManager(applicationContext)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d(TAG, "doWork")
        try {
            // Do something
            getLastLocation()
            Result.success()
        } catch (error: Throwable) {
            Result.failure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        LocationServices.getFusedLocationProviderClient(applicationContext)
            .lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                location?.let {
                    Log.d(TAG, "Location: ${it.latitude}, ${it.longitude}")
                    sessionManager.saveUserLat(it.latitude.toString())
                    sessionManager.saveUserLng(it.longitude.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Location: ${exception}")
            }
    }

    companion object {

        private const val TAG = "TrackLocationWorker"

        private const val TAG_TRACK_LOCATION_WORK = "TRACK_LOCATION_WORK_TAG"

        @JvmStatic
        fun schedule(appContext: Context) {
            val trackLocationRequest = OneTimeWorkRequestBuilder<TrackLocationWorker>()
                // Additional configuration
                .addTag(TAG_TRACK_LOCATION_WORK).build()
            WorkManager.getInstance(appContext).enqueueUniqueWork(
                TRACK_LOCATION_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                trackLocationRequest
            )
        }
    }
}