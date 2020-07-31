package com.ingrammicro.helpme.ui.createhelp

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.Category
import com.ingrammicro.helpme.data.model.CreateHelp
import com.ingrammicro.helpme.data.model.NotificationResponse
import com.ingrammicro.helpme.data.model.UploadVideoResponse
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.MainRepository
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*


class CreateHelpViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val apiRepo by lazy {
        MainRepository()
    }

    private var _createHelp = MediatorLiveData<Event<Resource<CreateHelp>>>()
    var createHelp: LiveData<Event<Resource<CreateHelp>>> = _createHelp
    var _uploadVideo = MediatorLiveData<Event<Resource<UploadVideoResponse>>>()
    var uploadVideo: LiveData<Event<Resource<UploadVideoResponse>>> = _uploadVideo

    var _uploadedVideo = MediatorLiveData<Event<UploadVideoResponse>>()
    var uploadedVideo: LiveData<Event<UploadVideoResponse>> = _uploadedVideo



    private var _createProfileResponse = MediatorLiveData<Event<CreateHelp>>()
    var createProfileResponse: LiveData<Event<CreateHelp>> = _createProfileResponse

    var imageSelected1 = MutableLiveData<Event<ByteArray>>()
    var _imageSelected1 : LiveData<Event<ByteArray>> = imageSelected1

    var imageSelected2 = MutableLiveData<Event<ByteArray>>()
    var _imageSelected2 : LiveData<Event<ByteArray>> = imageSelected2
    var imageSelected3 = MutableLiveData<Event<ByteArray>>()
    var _imageSelected3 : LiveData<Event<ByteArray>> = imageSelected3

    var videoSelected = MutableLiveData<Event<ByteArray>>()
    var _videoSelected: LiveData<Event<ByteArray>> = videoSelected

    private var _fetchCategories = MediatorLiveData<Event<Resource<ArrayList<Category>>>>()
    var fetchCategories: LiveData<Event<Resource<ArrayList<Category>>>> = _fetchCategories

    private var _categoriesResponse = MediatorLiveData<Event<ArrayList<Category>>>()
    var categoriesResponse: LiveData<Event<ArrayList<Category>>> = _categoriesResponse

    var title = ObservableField("")
    var videoName = ObservableField("")
    var description = ObservableField("")
    var categorySelected = ObservableField("")
    var type = ObservableField("")
    var priority = ObservableField("High")
    var isPriority = ObservableBoolean(true)
    var isProgressbarVisible  = ObservableBoolean(false)
    var isCategoryVisible  = ObservableBoolean(false)
    private val _snackbar = MutableLiveData<Event<Int>>()
    val snackbar: LiveData<Event<Int>> = _snackbar
    var image1: String = ""
    var image2: String = ""
    var image3: String = ""

    init {

        fetchCategories()
    }

    fun fetchCategories() {
        val _id = HelpMeApplication.prefs!!.fetchUserId()
        _fetchCategories.addSource(
            apiRepo.fetchCategories()
        ) { _fetchCategories.value = Event(it) }
    }

    fun createHelp(typeSelected : String ) {
        if (title.get() == ""){
            _snackbar.value = Event(R.string.title_error)
        } else if (description.get() == ""){
                _snackbar.value = Event(R.string.desc_error)
        } else if ( image1 == "" && image2 == "" && image3 == "") {
            _snackbar.value = Event(R.string.image_error)
        } else if (categorySelected.get() == ""){
            _snackbar.value = Event(R.string.category_error)
        }else
            {
            var imgArray = JsonArray()
            var profileImage = JsonObject()
            if (image1 != "" ) {
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val paramObject = JsonObject()
                paramObject.addProperty("fileName", "CreateHelpImg1"+timeStamp+".jpeg")
                paramObject.addProperty("type", "Maps")
                paramObject.addProperty("content", image1)
                profileImage = paramObject
                imgArray.add(profileImage)
            }


            if (image2 != "" ) {
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val paramObject = JsonObject()
                paramObject.addProperty("fileName", "CreateHelpImg2"+timeStamp+".jpeg")
                paramObject.addProperty("type", "Maps")
                paramObject.addProperty("content", image2)
                profileImage = paramObject
                imgArray.add(profileImage)
            }

            if (image3 != "" ) {
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val paramObject = JsonObject()
                paramObject.addProperty("fileName", "CreateHelpImg3"+timeStamp+".jpeg")
                paramObject.addProperty("type", "Maps")
                paramObject.addProperty("content", image3)
                profileImage = paramObject
                imgArray.add(profileImage)
            }

            _createHelp.addSource(
                    apiRepo.createHelp(typeSelected, title.get(),description.get(),imgArray,categorySelected.get(),priority.get(), videoName.get())
                )
                {
                    _createHelp.value = Event(it)

                }
//            }
        }
    }

    fun uploadVideo(id: String, type: String, videoSelected: ByteArray) {

        val fileName = videoName.get()
        _uploadVideo.addSource(
            apiRepo.uploadVideo(id, type, fileName!!, videoSelected)
        ) {
            _uploadVideo.value = Event(it)
        }
    }


    fun parseCategory(resource: Resource<ArrayList<Category>>) {
        when (resource) {
            is Resource.Loading -> {
                isCategoryVisible.set(true)
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _categoriesResponse.value = Event(data)
                    isCategoryVisible.set(false)
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
                isCategoryVisible.set(false)
//                _snackbar.value = Event(R.string.error_msg)
            }
        }
    }

    fun parse(resource: Resource<CreateHelp>) {
        when (resource) {
            is Resource.Loading -> {
                isProgressbarVisible.set(true)
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    _createProfileResponse.value = Event(data)
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

    fun parseVideo(resource: Resource<UploadVideoResponse>) {
        when (resource) {
            is Resource.Loading -> {
                isProgressbarVisible.set(true)
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let {
                    data ->
                    _uploadedVideo.value = Event(data)
                }
            }
            is Resource.Error -> {
                isProgressbarVisible.set(false)
                Log.d("Resource", "Error")
                _snackbar.value = Event(R.string.error_msg)
            }
        }
    }

}