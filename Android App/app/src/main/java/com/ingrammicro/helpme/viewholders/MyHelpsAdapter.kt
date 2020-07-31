package com.ingrammicro.helpme.viewholders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ingrammicro.helpme.MainActivity
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.help_item_parent_get.view.*
import net.cachapa.expandablelayout.ExpandableLayout

class MyHelpsAdapter(
    var context: Context?,
    var helpItems: ArrayList<HelpParentItem>,
    var readOnly: Boolean
) : RecyclerView.Adapter<MyHelpsAdapter.MyHelpItemsViewHolder>() {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var viewModel: ProfileViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHelpItemsViewHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.help_item_parent_get, parent, false)

        viewModel =
            ViewModelProviders.of(parent.context as MainActivity).get(ProfileViewModel::class.java)
        return MyHelpItemsViewHolder(view, viewModel, parent.context)
    }

    override fun getItemCount(): Int {
        return helpItems.size
    }

    override fun onBindViewHolder(holder: MyHelpItemsViewHolder, position: Int) {

        val adapter = ActivitiesAdapter(ArrayList(helpItems[position].activities), context)

        holder.bind(helpItems[position], adapter)
    }

    inner class MyHelpItemsViewHolder(view: View, viewModel: ProfileViewModel, context: Context) :
        RecyclerView.ViewHolder(view) {

        val timeout: Int = 60000
        val viewModel = viewModel
        val view = view
        val userOneImageView = itemView.findViewById<ImageView>(R.id.userImageOne)
        val userTwoImageView = itemView.findViewById<ImageView>(R.id.userImageTwo)
        val extraCount = itemView.findViewById<TextView>(R.id.extraCount)
        val karmaCardContainer = itemView.findViewById<LinearLayout>(R.id.karmaCountContainer)
        val cardKarmaCount = itemView.findViewById<TextView>(R.id.cardKarmaCount)
        val helpTitle = itemView.findViewById<TextView>(R.id.helpItemTitle)
        val helpType = itemView.findViewById<Chip>(R.id.helpItemType)
        val fullFilledView = itemView.findViewById<LinearLayout>(R.id.helpFullFilledPrompt)
        val categoryTypeImage = itemView.findViewById<ImageView>(R.id.helpCategoryImage)
        val notificationIcon = itemView.findViewById<LinearLayout>(R.id.notificationIcon)
        val helpDoneButton = itemView.findViewById<LinearLayout>(R.id.helpDoneButton)
        val helpDoneProgress = itemView.findViewById<ProgressBar>(R.id.helpDoneProgress)
        val expansionView = itemView.findViewById<ExpandableLayout>(R.id.expandableList)
        fun bind(helpItem: HelpParentItem, adapter: ActivitiesAdapter) {
            itemView.helpActivitiesListLayout.apply {
                layoutManager = LinearLayoutManager(context)
                helpActivitiesListLayout.adapter = adapter
            }

            if (!readOnly) {
                itemView.setOnClickListener {
                    if (helpItem.activities.isNotEmpty()) {
                        if ("full" !in itemView.helpItemType.text.toString().toLowerCase()) {
                            if (expansionView.isExpanded) {
                                expansionView.collapse()
                                if ("give" in helpItem.helpType.toLowerCase()) {
                                    karmaCardContainer.visibility = View.GONE
                                }
                            } else {
                                expansionView.expand()
                                if ("give" in helpItem.helpType.toLowerCase()) {
                                    karmaCardContainer.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            karmaCardContainer.visibility = View.GONE
                            expansionView.collapse()
                        }
                    }
                }

                helpDoneButton.setOnClickListener {
                    helpDoneProgress.visibility = View.VISIBLE
                    helpDoneButton.visibility = View.GONE
                    viewModel.closeHelp(helpItem._id)
                    viewModel.close.observe(view.context as LifecycleOwner, EventObserver { resource ->
                        viewModel.parseClose(resource)
                    })
                    viewModel.closeHelp.observe(view.context as LifecycleOwner, EventObserver { res ->
                        if (res.result == "Success") {
                            helpType.setChipBackgroundColorResource(R.color.primaryColor)
                            helpType.text = "Fullfilled"
                            helpType.setCloseIconTintResource(R.color.black)
                            helpType.setCloseIconResource(R.drawable.ic_fullfilled)
                            helpType.setTextColor(Color.parseColor("#000000"))
                            fullFilledView.visibility = View.GONE
                            expansionView.collapse()
                        } else {

                        }
                    })
                }
            }

            when {
                helpItem.activities.size > 2 -> {
                    userOneImageView.visibility = View.VISIBLE
                    userTwoImageView.visibility = View.VISIBLE
                    extraCount.visibility = View.VISIBLE
                    extraCount.text = "+" + (helpItem.activities.size - 2).toString()
                    Glide.with(itemView)
                        .load(helpItem.activities[0].profilePic)
                        .placeholder(R.drawable.ic_placeholder_profile).circleCrop()
                        .timeout(timeout)
                        .into(userOneImageView)
                    Glide.with(itemView)
                        .load(helpItem.activities[1].profilePic)
                        .placeholder(R.drawable.ic_placeholder_profile).circleCrop()
                        .timeout(timeout)
                        .into(userTwoImageView)
                }
                helpItem.activities.size == 2 -> {
                    userOneImageView.visibility = View.VISIBLE
                    userTwoImageView.visibility = View.VISIBLE
                    extraCount.visibility = View.INVISIBLE
                    Glide.with(itemView)
                        .load(helpItem.activities[0].profilePic)
                        .placeholder(R.drawable.ic_placeholder_profile).circleCrop()
                        .timeout(timeout)
                        .into(userOneImageView)
                    Glide.with(itemView)
                        .load(helpItem.activities[1].profilePic)
                        .placeholder(R.drawable.ic_placeholder_profile).circleCrop()
                        .timeout(timeout)
                        .into(userTwoImageView)
                }
                helpItem.activities.size == 1 -> {
                    userOneImageView.visibility = View.VISIBLE
                    userTwoImageView.visibility = View.INVISIBLE
                    extraCount.visibility = View.INVISIBLE
                    Glide.with(itemView)

                        .load(helpItem.activities[0].profilePic)
                        .placeholder(R.drawable.ic_placeholder_profile).circleCrop()
                        .timeout(timeout)
                        .into(userOneImageView)
                }
                else -> {
                    userOneImageView.visibility = View.INVISIBLE
                    userTwoImageView.visibility = View.INVISIBLE
                    extraCount.visibility = View.INVISIBLE
                }
            }

            helpTitle.text = helpItem.helpName
            helpType.text = helpItem.helpType.capitalize()
            notificationIcon.visibility = View.INVISIBLE
            Glide.with(itemView).load(helpItem.categoryUrl)
                .placeholder(R.drawable.ic_category_others)
                .circleCrop().into(categoryTypeImage)
            if (helpItem.status != 2) {
                when {
                    "need" in helpItem.helpType.toLowerCase() -> {

                        helpType.setChipBackgroundColorResource(R.color.chipRed)
                        helpType.setCloseIconResource(R.drawable.ic_arrow_need_help_24)
                        karmaCardContainer.visibility = View.GONE
                        fullFilledView.visibility = View.VISIBLE
                    }
                    "give" in helpItem.helpType.toLowerCase() -> {
                        helpType.setChipBackgroundColorResource(R.color.buttonGreen)
                        helpType.setCloseIconResource(R.drawable.ic_arrow_give_help_24)
                        cardKarmaCount.text = "Help & Earn 30"
                        karmaCardContainer.visibility = View.GONE
                        fullFilledView.visibility = View.VISIBLE
                        //karmaCardContainer.visibility = View.GONE
                    }
                    else -> {
                        helpType.setChipBackgroundColorResource(R.color.primaryColor)
                        karmaCardContainer.visibility = View.GONE
                        fullFilledView.visibility = View.GONE
                    }
                }
            } else {
                helpType.setChipBackgroundColorResource(R.color.primaryColor)
                helpType.setTextColor(Color.parseColor("#000000"))
                helpType.text = "Fullfilled"
                helpType.setCloseIconTintResource(R.color.black)
                helpType.setCloseIconResource(R.drawable.ic_fullfilled)
                karmaCardContainer.visibility = View.GONE
                fullFilledView.visibility = View.GONE
            }
        }

    }

}

