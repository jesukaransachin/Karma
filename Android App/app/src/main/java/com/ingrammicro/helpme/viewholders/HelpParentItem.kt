package com.ingrammicro.helpme.viewholders


import com.ingrammicro.helpme.data.model.ActivityItem
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

data class HelpParentItem(val _id: String, val helpType: String, val helpName: String, val description: String, val activities: List<HelpChildItem>, val category: String, val categoryUrl: String, val status: Int)