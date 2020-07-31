package com.ingrammicro.helpme.ui.notifications

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.ingrammicro.helpme.MainActivity
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.model.Notification
import com.ingrammicro.helpme.databinding.HelpPagerGallaryItemBinding
import com.ingrammicro.helpme.databinding.NotificationRecyclerItemBinding
import com.ingrammicro.helpme.ui.home.adapter.HelpPagerGalleryAdapter
import com.ingrammicro.helpme.ui.notifications.viewholders.NotificationChildItem
import com.ingrammicro.helpme.ui.notifications.viewmodels.NotificationsViewModel
import com.ingrammicro.helpme.utils.AppUtils.covertTimeToText
import kotlinx.android.synthetic.main.help_item_child.view.*
import kotlinx.android.synthetic.main.notification_recycler_item.view.*

class NotificationAdapter(var notificationItems: ArrayList<Notification>, var context: Context?): RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHolder>() {
    private lateinit var layoutInflater: LayoutInflater
    private lateinit var linearLayout: LinearLayout
    private var photos = arrayListOf<String>()
    private lateinit var viewModel: NotificationsViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : NotificationAdapterViewHolder {
//        layoutInflater = LayoutInflater.from(parent.context)
//        val view = layoutInflater.inflate(R.layout.notification_recycler_item, parent, false)
//        linearLayout = view.findViewById(R.id.helpActivitiesListLayout)
        viewModel = ViewModelProviders.of(parent.context as MainActivity).get(NotificationsViewModel::class.java)
//        return NotificationAdapterViewHolder(view, viewModel)

        return NotificationAdapterViewHolder(
            NotificationRecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context)), viewModel)
    }

    override fun getItemCount(): Int {
        return notificationItems.size
    }



    fun getItem(position: Int): Notification = notificationItems[position]


    fun addItems(items: ArrayList<String>) {
        if (!items.isNullOrEmpty()) {
//        photos.addAll(items)
            photos = items
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: NotificationAdapterViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(notificationItems[position])
    }
//    override fun onBindViewHolder(holder: NotificationAdapterViewHolder, position: Int) {
////        holder.fullFilledView.visibility = View.GONE
//        for(item in notificationItems) {
//
//               addLayout(notificationItems[position].msg,"")
//
//        }
//        holder.bind(notificationItems[position])
//    }

    private fun addLayout(title: String, description: String) {
        val view = layoutInflater.inflate(R.layout.notification_recycler_item, null, false)

        view.notifiTitle.text = title
//        view.notifiDescription.text = description
//        if (!isAccepted) {
//            view.helpUserPhoneEmailButton.visibility = View.GONE
//            view.helpUserAcceptButton.visibility = View.VISIBLE
//            view.helpUserAcceptButton.setOnClickListener {
//                view.progressBarAccept.visibility = View.VISIBLE
//                view.helpUserAcceptButton.visibility = View.GONE
//                viewModel.acceptHelp(hid, uid)
//                viewModel.accept.observe(view.context as LifecycleOwner, EventObserver { resource ->
//                    viewModel.parseAccept(resource)
//                })
//                viewModel.acceptHelp.observe(view.context as LifecycleOwner, EventObserver { res ->
//                    if (res.result == "Success") {
//                        view.progressBarAccept.visibility = View.GONE
//                        view.helpUserAcceptButton.visibility = View.GONE
//                        view.helpUserPhoneEmailButton.visibility = View.VISIBLE
//                        view.helpUserPhoneEmailButton.setOnClickListener{
//                            val intent = Intent(Intent.ACTION_DIAL)
//                            intent.data = Uri.parse("tel:$phone")
//                            context!!.startActivity(
//                                intent
//                            )
//
//                        }
//                    } else {
//                        view.progressBarAccept.visibility = View.GONE
//                        view.helpUserAcceptButton.visibility = View.VISIBLE
//                    }
//                })
//            }
//        } else {
//            view.helpUserAcceptButton.visibility = View.GONE
//            view.helpUserPhoneEmailButton.visibility = View.VISIBLE
//            view.helpUserPhoneEmailButton.setOnClickListener{
//                val intent = Intent(Intent.ACTION_DIAL)
//                intent.data = Uri.parse("tel:$phone")
//                context!!.startActivity(
//                    intent
//                )
//
//            }
//        }
//        Glide.with(view).load(profileUrl)
//            .circleCrop().into(view.helpUserPhoto)
//        linearLayout.addView(view)
    }


    class NotificationAdapterViewHolder( val binding: NotificationRecyclerItemBinding, viewModel: NotificationsViewModel) : RecyclerView.ViewHolder(binding.root) {


        init {
            binding.setClickListener {
//                viewModel.navigateToGallery(binding.childPosition)
            }
        }
        val timeout: Int = 60000
        val viewModel = viewModel
//        val view = view
//        val notifiTitle = itemView.findViewById<TextView>(R.id.notifiTitle)
//
//        val notifiDate = itemView.findViewById<TextView>(R.id.notifiDate)
//        val helpType = itemView.findViewById<Chip>(R.id.helpItemType)
//        val categoryImage = itemView.findViewById<ImageView>(R.id.categoryImage)

        fun bind(notifiItem: Notification) {


//
            binding.notifiTitle.text = notifiItem.msg
            binding.notifiDate.text  =  covertTimeToText(notifiItem.datetime)

//            notifiItem.categories.url
            if (notifiItem.helpType != null) {
                when {
                    "need" in notifiItem.helpType.toLowerCase() -> {

                        binding.helpItemType.setChipBackgroundColorResource(R.color.chipRed)
                        binding.helpItemType.setText(notifiItem.helpType)
//                        karmaCardContainer.visibility = View.GONE
                    }
                    "give" in notifiItem.helpType.toLowerCase() -> {
                        binding.helpItemType.setChipBackgroundColorResource(R.color.buttonGreen)
                        binding.helpItemType.setText(notifiItem.helpType)
                        //karmaCardContainer.visibility = View.GONE
                    }
                    else -> {
                        binding.helpItemType.setChipBackgroundColorResource(R.color.primaryColor)
//                        karmaCardContainer.visibility = View.GONE
                    }
                }
            } else {
                binding.helpItemType.setChipBackgroundColorResource(R.color.primaryColor)
                binding.helpItemType.setTextColor(Color.parseColor("#000000"))
                binding.helpItemType.text = "Fullfilled"
//                karmaCardContainer.visibility = View.GONE
//                fullFilledView.visibility = View.GONE
            }

            Glide.with(itemView).load(notifiItem.categories.url)
                .circleCrop().into(binding.categoryImage)

        }

    }

}

