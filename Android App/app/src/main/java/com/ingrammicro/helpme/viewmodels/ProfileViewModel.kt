package com.ingrammicro.helpme.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.AcceptHelpResponse
import com.ingrammicro.helpme.data.model.HelpItemResponse
import com.ingrammicro.helpme.data.model.User
import com.ingrammicro.helpme.data.model.Verification
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.MyHelpsRepository
import com.ingrammicro.helpme.data.repository.ProfileRepository
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel : ViewModel() {

    private val profileRepo by lazy {
        ProfileRepository()
    }

    private val myHelpsRepo by lazy {
        MyHelpsRepository()
    }

    private var _helps = MediatorLiveData<Event<Resource<HelpItemResponse>>>()
    var helps: LiveData<Event<Resource<HelpItemResponse>>> = _helps

    private var _myHelps = MediatorLiveData<Event<HelpItemResponse>>()
    var myHelps: LiveData<Event<HelpItemResponse>> = _myHelps

    private var _profile = MediatorLiveData<Event<Resource<User>>>()
    var profile: LiveData<Event<Resource<User>>> = _profile

    private var _userProfile = MediatorLiveData<Event<User>>()
    var userProfile: LiveData<Event<User>> = _userProfile

    private var _accept = MediatorLiveData<Event<Resource<AcceptHelpResponse>>>()
    var accept: LiveData<Event<Resource<AcceptHelpResponse>>> = _accept

    private var _acceptHelp = MediatorLiveData<Event<AcceptHelpResponse>>()
    var acceptHelp: LiveData<Event<AcceptHelpResponse>> = _acceptHelp

    private var _close = MediatorLiveData<Event<Resource<AcceptHelpResponse>>>()
    var close: LiveData<Event<Resource<AcceptHelpResponse>>> = _close

    private var _closeHelp = MediatorLiveData<Event<AcceptHelpResponse>>()
    var closeHelp: LiveData<Event<AcceptHelpResponse>> = _closeHelp


    fun fetchProfile(id: String?) {
        val _id = id!!

        _profile.addSource(
            profileRepo.getUserProfile(_id)
        ) { _profile.value = Event(it) }
    }

    fun fetchMyHelps(id: String?) {
        val _id = id!!
        _helps.addSource(
            myHelpsRepo.getUserHelps(_id)
        ) { _helps.value = Event(it) }
    }

    fun acceptHelp(hid: String?, uid: String?) {
        val _hid = hid!!
        val _uid = uid!!
        _accept.addSource(
            myHelpsRepo.acceptUserHelp(_hid, _uid,getCurrentDate())
        ) { _accept.value = Event(it) }
    }


    fun getCurrentDate() : String {

        val c: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }
    fun closeHelp(hid: String?) {
        val _hid = hid!!
        _close.addSource(
            myHelpsRepo.closeHelp(_hid,getCurrentDate())
        ) { _close.value = Event(it) }
    }

    fun parse(resource: Resource<User>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _userProfile.value = Event(data)
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
            }
        }
    }

    fun parseAccept(resource: Resource<AcceptHelpResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _acceptHelp.value = Event(data)
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
            }
        }
    }

    fun parseClose(resource: Resource<AcceptHelpResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _closeHelp.value = Event(data)
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
            }
        }
    }

    fun parseHelps(resource: Resource<HelpItemResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _myHelps.value = Event(data)
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
            }
        }
    }


}