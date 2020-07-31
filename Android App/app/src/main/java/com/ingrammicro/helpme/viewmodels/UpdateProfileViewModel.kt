package com.ingrammicro.helpme.viewmodels

import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.UpdateProfile
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.MainRepository
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class UpdateProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val apiRepo by lazy {
        MainRepository()
    }

    private var _updateProfile = MediatorLiveData<Event<Resource<UpdateProfile>>>()
    var updateProfile: LiveData<Event<Resource<UpdateProfile>>> = _updateProfile

    private var _updateProfileResponse = MediatorLiveData<Event<UpdateProfile>>()
    var updateProfileResponse : LiveData<Event<UpdateProfile>> = _updateProfileResponse

    var profileImage = MutableLiveData<Event<ByteArray>>()
    var _profileImage : LiveData<Event<ByteArray>> = profileImage

    var docuImage = MutableLiveData<Event<ByteArray>>()
    var _docuImage : LiveData<Event<ByteArray>> = docuImage

    private val _snackbar = MutableLiveData<Event<Int>>()
    val snackbar: LiveData<Event<Int>> = _snackbar
    var isProgressbarVisible  = ObservableBoolean(false)
    var isProfilePicProgressbarVisible  = ObservableBoolean(false)
    var isdocuPicProgressbarVisible  = ObservableBoolean(false)

    var image: String = ""
    var documentImage: String = ""
    var fullName = ObservableField("")
    var email = ObservableField("")
    var address = ObservableField("")
    var age = ObservableField("")
//    var phone = ObservableField("")
//    var isPhoneDefaultContact = ObservableField(true)
    var isPhoneDefaultContact = ObservableBoolean(true)

    fun updateProfile(phone: String) {
        if (fullName.get() == ""){
            _snackbar.value = Event(R.string.full_name_error)
        } else {
            if (email.get() != "" && !isValidEmail(email.get().toString())) {
                _snackbar.value = Event(R.string.email_error)
            } else if (!isPhoneDefaultContact.get() && email.get() == "") {
                _snackbar.value = Event(R.string.email_error)
            } else {

                var profileImage = JsonObject()
                if (image != "" ) {
                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val paramObject = JsonObject()
                    paramObject.addProperty("fileName", "ProfImg"+timeStamp+".jpeg")
                    paramObject.addProperty("type", "Profile")
                    paramObject.addProperty("content", image)
                    profileImage = paramObject
                }


                var docuImg = JsonObject()
                if (documentImage != "" ) {
                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val paramObject = JsonObject()
                    paramObject.addProperty("fileName", "DocImg"+timeStamp+".jpeg")
                    paramObject.addProperty("type", "Document")
                    paramObject.addProperty("content", documentImage)
                    docuImg = paramObject
                }


                _updateProfile.addSource(
                    apiRepo.updateProfile(
                        fullName.get(),
                        email.get(),
                        address.get(),
                        phone,
                        age.get(),
                        profileImage,
                        docuImg,
                        if(isPhoneDefaultContact.get()) 1 else 0


                    )
                )
                {
                    _updateProfile.value = Event(it)

                }
            }
        }
    }

    fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    fun onEmailDefaultClick() {
        isPhoneDefaultContact.set(false)
    }
    fun onPhoneDefaultClick() {
       isPhoneDefaultContact.set(true)

    }
    fun parse(resource: Resource<UpdateProfile>) {
        when (resource) {
            is Resource.Loading -> {
                isProgressbarVisible.set(true)
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _updateProfileResponse.value = Event(data)
//                    isProgressbarVisible.set(false)
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
                isProgressbarVisible.set(false)
                _snackbar.value = Event(R.string.error_msg)
            }
        }
    }
}