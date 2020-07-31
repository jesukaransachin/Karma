package com.ingrammicro.helpme.data.remote

import androidx.lifecycle.MutableLiveData
import com.ingrammicro.helpme.utils.STATE_ERROR_NETWORK
import com.ingrammicro.helpme.utils.STATE_ERROR_TIMEOUT
import com.ingrammicro.helpme.utils.STATE_ERROR_UNEXPECTED
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

open class NetworkCall<T> {

    lateinit var call: Call<T>

    fun makeCall(call: Call<T>): MutableLiveData<Resource<T>> {
        this.call = call
        val callBackKt = CallBackKt<T>()
        callBackKt.result.value = Resource.Loading()
        this.call.clone().enqueue(callBackKt)
        return callBackKt.result
    }

    class CallBackKt<T> : Callback<T> {

        var result: MutableLiveData<Resource<T>> = MutableLiveData()

        override fun onFailure(call: Call<T>, t: Throwable) {
            val errorType = when (t) {
                is SocketTimeoutException -> STATE_ERROR_TIMEOUT
                is ConnectException -> STATE_ERROR_NETWORK
                is UnknownHostException -> STATE_ERROR_NETWORK
                else -> STATE_ERROR_UNEXPECTED
            }
            result.value = Resource.Error(errorType)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful)
                result.value = response.body()?.let { Resource.Success(it) }
            else {
                val error = STATE_ERROR_UNEXPECTED
                result.value = Resource.Error(error)
            }
        }
    }

    fun cancel() {
        if (::call.isInitialized) {
            call.cancel()
        }
    }
}