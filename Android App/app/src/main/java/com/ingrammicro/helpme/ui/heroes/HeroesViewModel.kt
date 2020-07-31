package com.ingrammicro.helpme.ui.heroes

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.model.Hero
import com.ingrammicro.helpme.data.model.HeroesResponse
import com.ingrammicro.helpme.data.model.SuperHero
import com.ingrammicro.helpme.data.remote.Resource
import com.ingrammicro.helpme.data.repository.MainRepository

class HeroesViewModel : ViewModel() {

    var heroes = arrayListOf<Hero>()
        private set

    var superHeroes = arrayListOf<SuperHero>()
        private set

    private val mainRepo by lazy {
        MainRepository()
    }

    private var _fetchHeroes = MediatorLiveData<Event<Resource<HeroesResponse>>>()
    var fetchHeroes: LiveData<Event<Resource<HeroesResponse>>> = _fetchHeroes

    private var _notifyAdapter = MediatorLiveData<Unit>()
    var notifyAdapter: LiveData<Unit> = _notifyAdapter

    private var _helpGiven = MediatorLiveData<Int>().apply {
        value = 584
    }
    var helpGiven: LiveData<Int> = _helpGiven

    private var _helpRequested = MediatorLiveData<Int>().apply {
        value = 541
    }
    var helpRequested: LiveData<Int> = _helpRequested

    private var _userBenefited = MediatorLiveData<Int>().apply {
        value = 382
    }
    var userBenefited: LiveData<Int> = _userBenefited

    private var _moveToProfile = MediatorLiveData<Event<SuperHero>>()
    var moveToProfile: LiveData<Event<SuperHero>> = _moveToProfile

    private var _error = MediatorLiveData<Event<String>>()
    var error: LiveData<Event<String>> = _error

    init {
        getHeroes()
        addSuperHero()
    }

    private fun getHeroes() {
        _fetchHeroes.addSource(
            mainRepo.fetchHeroes()
        ) { _fetchHeroes.value = Event(it) }
    }

    fun parseResponse(resource: Resource<HeroesResponse>) {
        when (resource) {
            is Resource.Loading -> {
                Log.d("Resource", "Loading")
            }
            is Resource.Success -> {
                Log.d("Resource", "Success")
                resource.data?.let { data ->
                    if (TextUtils.isEmpty(data.error)) {
                        _helpGiven.value = data.helpGiven
                        _helpRequested.value = data.helpRequested
                        _userBenefited.value = data.usersBenefitted
                    } else {
                        _error.value = Event(data.error)
                    }
                }
            }
            is Resource.Error -> {
                Log.d("Resource", "Error")
                _error.value = Event("Error")
            }
        }
    }

    fun addSuperHero() {

        val hero1 = SuperHero(
            "Sonu Sood",
            "+917718013458",
            R.drawable.ic_sonu_sood,
            R.string.hero_sonu_sood,
            "",
            "vFenNkRFvSo",
            1893,
            isSpecial = true
        )
        superHeroes.add(hero1)

        val hero2 = SuperHero(
            "Rana Ayub",
            "+917718013458",
            R.drawable.ic_rana_ayyub,
            R.string.hero_rana_ayub,
            "",
            "sXXnNtpRPkY",
            1347,
            isSpecial = true
        )
        superHeroes.add(hero2)

        val hero3 = SuperHero(
            "Shanti Chuahan",
            "+917718013458",
            R.drawable.ic_shanti_chauhan,
            R.string.hero_shanti_chuahan,
            "",
            "kp_me7opTJc",
            1736,
            isSpecial = true
        )
        superHeroes.add(hero3)
        _notifyAdapter.value = Unit
    }

    fun navigateToProfile(hero: SuperHero) {
        _moveToProfile.value = Event(hero)
    }
}