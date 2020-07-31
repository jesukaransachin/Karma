package com.ingrammicro.helpme.ui.home

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.Category
import com.ingrammicro.helpme.data.model.Help
import com.ingrammicro.helpme.data.model.HelpAct
import com.ingrammicro.helpme.data.model.HelpResponse
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.MainRepository
import com.ingrammicro.helpme.utils.HELP_STATUS_CLOSED
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HelpViewModel : ViewModel() {

    private var query = ""

    var mExpanded = false
        private set
    var lastposition = -1
        private set
    var childPosition = 0
        private set

    var helpList = arrayListOf<Help>()
        private set
    var categoryList = arrayListOf<Category>()
        private set
    var categoryIds = arrayListOf<String>()
        private set

    private val mainRepo by lazy {
        MainRepository()
    }

    private var _fetchHelp = MediatorLiveData<Event<Resource<HelpResponse>>>()
    var fetchHelp: LiveData<Event<Resource<HelpResponse>>> = _fetchHelp

    private var _notifyAdapter = MediatorLiveData<Unit>()
    var notifyAdapter: LiveData<Unit> = _notifyAdapter

    private var _notifyCategories = MediatorLiveData<Unit>()
    var notifyCategories: LiveData<Unit> = _notifyCategories

    private val _notifyPosition = MutableLiveData<Event<Int>>()
    val notifyPosition: LiveData<Event<Int>> = _notifyPosition

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _myLocationButtonEnabled = MutableLiveData<Boolean>()
    val myLocationButtonEnabled: LiveData<Boolean> = _myLocationButtonEnabled

    private var _error = MediatorLiveData<String>()
    var error: LiveData<String> = _error

    private val _detailsEnabled = MutableLiveData<Event<Unit>>()
    val detailsEnabled: LiveData<Event<Unit>> = _detailsEnabled

    private val _gallery = MutableLiveData<Event<ArrayList<String>>>()
    val gallery: LiveData<Event<ArrayList<String>>> = _gallery

    init {
        getHelps()
    }

    fun getHelps() {
        _fetchHelp.addSource(
            mainRepo.getHelps(query, categoryIds)
        ) { _fetchHelp.value = Event(it) }
    }

    fun getHelps(queryText: String?) {
        queryText?.let { text ->
            query = text
            getHelps()
        }
    }

    fun getHelps(id: String, isChecked: Boolean) {
        if (isChecked) {
            categoryIds.add(id)
        } else {
            categoryIds.remove(id)
        }
        getHelps()
    }

    fun closeSearch() {
        if (!TextUtils.isEmpty(query)) {
            query = ""
            getHelps()
        }
    }

    fun onHelp(position: Int) {
        val userId = HelpMeApplication.prefs?.fetchUserId()!!
        val activities = arrayListOf<HelpAct>()
        activities.add(HelpAct(userId, 0))
        val help = helpList[position]
        help.activities = activities
        helpList.set(position, help)

        val helpId = helpList[position].id
        mainRepo.help(helpId, userId,getCurrentDate())

        _notifyPosition.value = Event(position)
    }


    fun getCurrentDate() : String {
        val c: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formattedDate: String = df.format(c)
        return formattedDate
    }

    fun storeLastPosition(position: Int) {
        lastposition = position
    }

    fun parseResponse(resource: Resource<HelpResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
                _error.value = ""
                _loading.value = true
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                _loading.value = false
                resource.data?.let { data ->
                    if (TextUtils.isEmpty(data.error)) {
                        handleHelps(data.helps)
                        if (!data.categories.isNullOrEmpty()) {
                            categoryList = data.categories
                            _notifyCategories.value = Unit
                        }
                    } else {
                        _error.value = data.error
                    }
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
                _loading.value = false
                resource.errorType?.let { type ->
                    _error.value = type
                }
            }
        }
    }

    private fun handleHelps(helps: ArrayList<Help>) {

        val filteredHelps = filterHelps(helps)
        if (!filteredHelps.isNullOrEmpty()) {
            lastposition = 0
            helpList.clear()
            helpList = filteredHelps
            _notifyAdapter.value = Unit
            _myLocationButtonEnabled.value = true
        } else {
            lastposition = -1
            helpList.clear()
            _notifyAdapter.value = Unit
            _error.value = "No help found"
        }
    }

    private fun filterHelps(helps: ArrayList<Help>): ArrayList<Help> {

        var filteredHelps = arrayListOf<Help>()
        if (helps.isNullOrEmpty()) {
            return filteredHelps
        }

        val tempList = ArrayList(helps)
        val userId = HelpMeApplication.prefs?.fetchUserId()
        for (help in helps) {
            when {
                help.userId == userId -> tempList.remove(help)
                help.status == HELP_STATUS_CLOSED -> tempList.remove(help)
            }
        }

        filteredHelps = tempList
        return filteredHelps
    }

    fun toggleDetails() {
        mExpanded = !mExpanded
        _detailsEnabled.value = Event(Unit)
    }

    fun navigateToGallery(childPosition: Int) {
        this.childPosition = childPosition
        val help = helpList[lastposition]
        _gallery.value = Event(help.photos)
    }
}