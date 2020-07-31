package com.ingrammicro.helpme.viewholders

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingrammicro.helpme.MainActivity
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.data.model.ActivityItem
import com.ingrammicro.helpme.fragments.ProfileFragmentDirections
import com.ingrammicro.helpme.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.help_item_child.view.*
import java.util.*
import kotlin.collections.ArrayList

class ActivitiesAdapter(var activityItems: ArrayList<HelpChildItem>, var context: Context?): RecyclerView.Adapter<ActivitiesAdapter.ActivityItemHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ActivityItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.help_item_child, parent, false)

        return ActivityItemHolder(view)
    }

    override fun getItemCount(): Int {
        return activityItems.size
    }

    override fun onBindViewHolder(holder: ActivityItemHolder, position: Int) {
        val viewModel = ViewModelProviders.of(context as MainActivity).get(UUID.randomUUID().toString(),ProfileViewModel::class.java)
        holder.bind(activityItems[position], context!!, viewModel)
    }

    class ActivityItemHolder(view: View): RecyclerView.ViewHolder(view){
        val timeout: Int = 6000
        private var view = view

        fun bind(item: HelpChildItem, context: Context, viewModel: ProfileViewModel) {
            view.helpUserFullName.text = item.fullName?.capitalize() ?: "Unverified User"
            view.progressBarAccept.visibility = View.GONE
            val intent = Intent()
            if (item.contactVia == 1) {
                intent.action = Intent.ACTION_DIAL
                intent.data = Uri.parse("tel:${item.phone}")
                view.phoneIcon.setBackgroundResource(R.drawable.ic_call_icon)
            } else {
                intent.action = Intent.ACTION_SENDTO
                intent.type = "text/html"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(item.email))
                intent.putExtra(Intent.EXTRA_SUBJECT, "Karma: ${item.helpTitle}")
                view.phoneIcon.setBackgroundResource(R.drawable.ic_email_icon)
            }

            view.helpUserPhoto.setOnClickListener {
                val action = ProfileFragmentDirections.moveToUserProfile(item.uid, true)
                it.findNavController().navigate(action)
            }

            view.helpUserFullName.setOnClickListener {
                val action = ProfileFragmentDirections.moveToUserProfile(item.uid, true)
                it.findNavController().navigate(action)
            }

            if (item.status == 0 || item.status == 2) {
                view.helpUserPhoneEmailButton.visibility = View.GONE
                view.helpUserAcceptButton.visibility = View.VISIBLE
                view.helpUserAcceptButton.setOnClickListener {
                    view.progressBarAccept.visibility = View.VISIBLE
                    view.helpUserAcceptButton.visibility = View.GONE
                    viewModel.acceptHelp(item.hid, item.uid)
                    viewModel.accept.observe(itemView.context as LifecycleOwner, EventObserver { resource ->
                        viewModel.parseAccept(resource)
                    })
                    viewModel.acceptHelp.observe(itemView.context as LifecycleOwner, EventObserver { res ->
                        if (res.result == "Success") {
                            view.progressBarAccept.visibility = View.GONE
                            view.helpUserAcceptButton.visibility = View.GONE
                            view.helpUserPhoneEmailButton.visibility = View.VISIBLE
                            view.helpUserPhoneEmailButton.setOnClickListener{

                                context.startActivity(
                                    intent
                                )

                            }
                        } else {
                            view.progressBarAccept.visibility = View.GONE
                            view.helpUserAcceptButton.visibility = View.VISIBLE
                        }
                    })
                }
            } else {
                view.helpUserAcceptButton.visibility = View.GONE
                view.helpUserPhoneEmailButton.visibility = View.VISIBLE
                view.helpUserPhoneEmailButton.setOnClickListener{

                    context.startActivity(
                        intent
                    )

                }
            }
            Glide.with(view).load(item.profilePic).placeholder(R.drawable.ic_placeholder_profile)
                .circleCrop().into(view.helpUserPhoto)
        }
    }
}