package com.ingrammicro.helpme.ui.notifications.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.*
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.MainRepository
import com.ingrammicro.helpme.utils.HELP_STATUS_CLOSED

class NotificationsViewModel : ViewModel() {
    // TODO: Implement the ViewModel


    private val mainRepo by lazy {
        MainRepository()
    }

    var notificationList = arrayListOf<Notification>()
        private set

    private var _fetchNotification = MediatorLiveData<Event<Resource<NotificationResponse>>>()
    var fetchNotification: LiveData<Event<Resource<NotificationResponse>>> = _fetchNotification

    private var _notifyAdapter = MediatorLiveData<Unit>()
    var notifyAdapter: LiveData<Unit> = _notifyAdapter

    private var _error = MediatorLiveData<Any>()
    var error: LiveData<Any> = _error

    private val _snackbar = MutableLiveData<Event<Int>>()
    val snackbar: LiveData<Event<Int>> = _snackbar

    var isProgressbarVisible  = ObservableBoolean(false)

    init {

        fetchNotification()
    }

    fun fetchNotification() {
        val _id = HelpMeApplication.prefs!!.fetchUserId()
        _fetchNotification.addSource(
            mainRepo.fetchNotification(_id!!)
        ) { _fetchNotification.value = Event(it) }
    }


    fun parseResponse(resource: Resource<NotificationResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
                isProgressbarVisible.set(true)
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->

                        if (!data.notificationLst.isNullOrEmpty()) {
                            notificationList = data.notificationLst
                            if (!notificationList.isNullOrEmpty()) {
                               _notifyAdapter.value = Unit
                           } else {
                                _snackbar.value = Event(R.string.notification_empty)
                            }

                        } else {
                            //no data
                            _snackbar.value = Event(R.string.notification_empty)
                        }
                }
                isProgressbarVisible.set(false)
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
//                resource.state?.let { state ->
//                    _error.value = getErrorMessage(state)
//                }
                _snackbar.value = Event(R.string.error_msg)
                isProgressbarVisible.set(false)
            }
        }
    }

}