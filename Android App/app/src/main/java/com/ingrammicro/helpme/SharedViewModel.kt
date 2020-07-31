package com.ingrammicro.imdelivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ingrammicro.helpme.data.model.Category
import com.ingrammicro.helpme.data.model.Hero
import com.ingrammicro.helpme.data.model.SuperHero
import com.ingrammicro.helpme.data.model.User

class SharedViewModel : ViewModel() {

    var bottomBar = MutableLiveData<Boolean>()
    var gallery = MutableLiveData<ArrayList<String>>()
    var categories = MutableLiveData<ArrayList<Category>>()
    var user = MutableLiveData<User>()
//    var hero = MutableLiveData<Hero>()
    var superHero = MutableLiveData<SuperHero>()
    var closeDialog = MutableLiveData<Boolean>()
}