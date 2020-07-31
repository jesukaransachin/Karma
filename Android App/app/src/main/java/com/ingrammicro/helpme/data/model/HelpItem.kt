package com.ingrammicro.helpme.data.model

data class HelpItem(val _id: String, val helptype: String, val title: String, val description: String, val activity: List<ActivityItem>, val category: Category, val status: Int)