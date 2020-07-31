package com.ingrammicro.helpme.utils

import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.model.Help
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat


@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("categoryUrl")
fun CircleImageView.setCategoryImage(imageUrl: String) {
//    "https://i.imgur.com/H981AN7.jpg"
//    Picasso.get()
//        .load(imageUrl)
//        .into(this)

    Glide.with(this)
        .load(imageUrl)
        .into(this)
}

@BindingAdapter("heroUrl")
fun CircleImageView.setHeroImage(imageUrl: String) {
//    "https://i.imgur.com/H981AN7.jpg"
//    Picasso.get()
//        .load(imageUrl)
//        .into(this)

    Glide.with(this)
        .load(imageUrl)
        .into(this)
}

@BindingAdapter("photoUrl")
fun CircleImageView.setPhotoImage(imageUrl: String) {
//    "https://i.imgur.com/H981AN7.jpg"
//    Picasso.get()
//        .load(imageUrl)
//        .into(this)

    Glide.with(this)
        .load(imageUrl)
        .into(this)
}

@BindingAdapter("distance")
fun TextView.setDistance(distance: Double) {
    val dis = distance / 1000
    val df = DecimalFormat("0.00")
//    text = "${df.format(dis)} km"
//    text = "${"%.2f".format(dis)} km"
    text = resources.getString(R.string.distance_format, df.format(dis))
}

@BindingAdapter("helpType")
fun Chip.setHelpType(helpType: String) {
    when (helpType) {
        NEED_HELP -> {
            setChipBackgroundColorResource(R.color.colorNeedHelp)
            setChipStrokeColorResource(R.color.colorNeedHelp)
            closeIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_need_help_24)
        }
        GIVE_HELP -> {
            setChipBackgroundColorResource(R.color.colorGiveHelp)
            setChipStrokeColorResource(R.color.colorGiveHelp)
            closeIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_give_help_24)
        }
    }
}

@BindingAdapter("priority")
fun Chip.setPriority(priority: String) {
    if (TextUtils.equals(priority, HIGH_PRIORITY)) {
        text = priority
        visibility = View.VISIBLE
    } else {
        text = ""
        visibility = View.INVISIBLE
    }
}

@BindingAdapter("gallery")
fun RecyclerView.setGallery(photos: ArrayList<String>) {
    layoutManager = LinearLayoutManager(
        context,
        LinearLayoutManager.HORIZONTAL, false
    )
}

@BindingAdapter("helpAction")
fun Button.setHelpAction(help: Help) {
    when (help.helpType) {
        NEED_HELP -> {
            onNeedHelp(this, help)
        }
        GIVE_HELP -> {
            onGiveHelp(this, help)
        }
    }
}

private fun onNeedHelp(button: Button, help: Help) {

    val userId = HelpMeApplication.prefs?.fetchUserId()

    var status = HELP_ACT_STATUS_NONE
    val activities = help.activities
    for (activity in activities) {
        if (userId == activity.uid) {
            status = activity.status
        }
    }

    when (status) {
        HELP_ACT_STATUS_NONE -> button.text = "I want to Help"
        HELP_ACT_STATUS_AWAITING -> button.text = "Awaiting Response"
        HELP_ACT_STATUS_ACCEPTED -> button.text = "Accepted your request"
        HELP_ACT_STATUS_CLOSED -> button.text = "Closed the request"
    }

    button.isEnabled = status == HELP_ACT_STATUS_NONE
}

private fun onGiveHelp(button: Button, help: Help) {

    val userId = HelpMeApplication.prefs?.fetchUserId()

    var status = HELP_ACT_STATUS_NONE
    val activities = help.activities
    for (activity in activities) {
        if (userId == activity.uid) {
            status = activity.status
            break
        }
    }

    when (status) {
        HELP_ACT_STATUS_NONE -> button.text = "I need Help"
        HELP_ACT_STATUS_AWAITING -> button.text = "Awaiting Response"
        HELP_ACT_STATUS_ACCEPTED -> button.text = "Ready to help you"
        HELP_ACT_STATUS_CLOSED -> button.text = "Closed the request"
    }

    button.isEnabled = status == HELP_ACT_STATUS_NONE
}