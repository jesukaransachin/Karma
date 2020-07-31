package com.ingrammicro.helpme.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.Verification
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.AuthRepository

class OtpEntryViewModel : ViewModel() {

    private val authRepo by lazy {
        AuthRepository()
    }

    private var _verify = MediatorLiveData<Event<Resource<Verification>>>()
    var verify: LiveData<Event<Resource<Verification>>> = _verify

    private var _user = MediatorLiveData<Event<Verification>>()
    var user: LiveData<Event<Verification>> = _user

    private var _error = MediatorLiveData<String>()
    var error: LiveData<String> = _error

    fun doVerification(id: String) {
        _verify.addSource(
            authRepo.verify(id)
        ) { _verify.value = Event(it) }
    }

    fun parse(resource: Resource<Verification>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    if (TextUtils.isEmpty(data.error)) {
                        _user.value = Event(data)
                    } else {
                        _error.value = data.error
                    }
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
                resource.errorType?.let { type ->
                    _error.value = type
                }
            }
        }
    }
}