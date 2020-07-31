package com.ingrammicro.helpme.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.Login
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.AuthRepository

class PhoneLoginViewModel : ViewModel() {

    private val authRepo by lazy {
        AuthRepository()
    }

    private var _login = MediatorLiveData<Event<Resource<Login>>>()
    var login: LiveData<Event<Resource<Login>>> = _login

    private var _user = MediatorLiveData<Event<Login>>()
    var user: LiveData<Event<Login>> = _user

    private var _error = MediatorLiveData<String>()
    var error: LiveData<String> = _error

    private val _snackbarWithRetry = MutableLiveData<Int>()
    val snackbarWithRetry: LiveData<Int> = _snackbarWithRetry

    private val _snackbar = MutableLiveData<Event<Int>>()
    val snackbar: LiveData<Event<Int>> = _snackbar

    fun doLogin(phone: String) {
        _login.addSource(
            authRepo.login(phone)
        ) { _login.value = Event(it) }
    }

    fun parse(resource: Resource<Login>) {
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

    fun onLoginClick() {
        _login.addSource(
            authRepo.login("+919833278717")
        ) { _login.value = Event(it) }
    }
}