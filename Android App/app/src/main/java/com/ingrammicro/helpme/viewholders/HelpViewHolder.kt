package com.ingrammicro.helpme.viewholders

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ingrammicro.helpme.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class HelpItemViewHolder(itemView: View) : GroupViewHolder(itemView) {
    val helpTitle = itemView.findViewById<TextView>(R.id.helpItemTitle)
    val helpType = itemView.findViewById<Chip>(R.id.helpItemType)
    val categoryTypeImage = itemView.findViewById<ImageView>(R.id.helpCategoryImage)
    val notificationIcon = itemView.findViewById<LinearLayout>(R.id.notificationIcon)

    fun bind(helpItem: HelpParentItem) {
        helpTitle.text = helpItem.helpName
        helpType.text = helpItem.helpType
        notificationIcon.visibility = View.INVISIBLE
        Glide.with(itemView).load(helpItem.categoryUrl)
            .circleCrop().into(categoryTypeImage)
        if ("get" in helpItem.helpType.toLowerCase()) {
            helpType.setChipBackgroundColorResource(R.color.chipRed)
        } else if ("give" in helpItem.helpType.toLowerCase()){
            helpType.setChipBackgroundColorResource(R.color.buttonGreen)
        } else {
            helpType.setChipBackgroundColorResource(R.color.primaryColor)
        }
    }
}

class HelpUserItemViewHolder(itemView: View) : ChildViewHolder(itemView) {
    val userFullName = itemView.findViewById<TextView>(R.id.helpUserFullName)
    val userProfilePhoto = itemView.findViewById<ImageView>(R.id.helpUserPhoto)
    val userHelpEmail = itemView.findViewById<Button>(R.id.helpUserPhoneEmailButton)
    val acceptHelp = itemView.findViewById<Button>(R.id.helpUserAcceptButton)

    fun bind(helpChildItem: HelpChildItem) {
        userFullName.text = helpChildItem.fullName
        acceptHelp.visibility = View.GONE
    }
}