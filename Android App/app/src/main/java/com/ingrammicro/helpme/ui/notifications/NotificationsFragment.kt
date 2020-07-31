package com.ingrammicro.helpme.ui.notifications

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.data.model.Notification
import com.ingrammicro.helpme.databinding.FragmentNotificationsBinding
import com.ingrammicro.helpme.databinding.UpdateProfileFragmentBinding
import com.ingrammicro.helpme.ui.notifications.viewholders.NotificationChildItem
import com.ingrammicro.helpme.ui.notifications.viewmodels.NotificationsViewModel
import com.ingrammicro.helpme.utils.shortSnackbar
import com.ingrammicro.helpme.viewholders.HelpParentItem
import kotlinx.android.synthetic.main.profile_fragment.view.*
import java.util.*
import kotlin.collections.ArrayList

class NotificationsFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    private lateinit var viewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!





    private lateinit var notiArray : ArrayList<Notification>

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
       _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        binding.viewModel = viewModel

//        val myHelp = NotificationChildItem(
//            "ajdkjfh",
//            "jadfk",
//            "ahfk",
//            "afjdhkdja"
//
//        )
////        myHelps.add(myHelp)
//        notiArray = ArrayList<NotificationChildItem>()
//        notiArray.add(myHelp)
//
//        val adapter = NotificationAdapter(notiArray, context)
//        binding.notificaitionRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            binding.notificaitionRecyclerView.adapter = adapter
//        }
//        binding.myHelpsRecyclerView.visibility = View.VISIBLE
        subscribeToChanges()
    }

    fun subscribeToChanges(){
        viewModel.fetchNotification.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parseResponse(resource)
        })

        viewModel.notifyAdapter.observe(viewLifecycleOwner, Observer {
            notiArray = viewModel.notificationList
            notiArray.reverse();
            val adapter = NotificationAdapter(notiArray, context)
            binding.notificaitionRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                binding.notificaitionRecyclerView.adapter = adapter
            }
        })
        binding.backButton.setOnClickListener { view ->
            findNavController().popBackStack()

        }
        viewModel.snackbar.observe(viewLifecycleOwner, EventObserver {

            shortSnackbar(binding.root, it).show()

        })

    }

}