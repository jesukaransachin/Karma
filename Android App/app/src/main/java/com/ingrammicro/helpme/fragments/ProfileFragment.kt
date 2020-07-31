package com.ingrammicro.helpme.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.databinding.ProfileFragmentBinding
import com.ingrammicro.helpme.utils.SessionManager
import com.ingrammicro.helpme.viewholders.HelpChildItem
import com.ingrammicro.helpme.viewholders.HelpParentItem
import com.ingrammicro.helpme.viewholders.MyHelpsAdapter
import com.ingrammicro.helpme.viewmodels.ProfileViewModel
import com.ingrammicro.imdelivery.SharedViewModel
import kotlinx.android.synthetic.main.profile_fragment.view.*

class ProfileFragment : Fragment() {

    val args: ProfileFragmentArgs by navArgs()

    companion object {
        fun newInstance() = ProfileFragment()
    }

    private lateinit var sessionManager: SessionManager

    private lateinit var viewModel: ProfileViewModel
    private lateinit var topAppBar: MaterialToolbar
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: ProfileFragmentBinding? = null
    private val binding get() = _binding!!
    val myHelps = ArrayList<HelpParentItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        binding.myHelpsRecyclerView.visibility = View.GONE
        binding.profileHelpsProgressIndicator.visibility = View.VISIBLE
        binding.noItemsTextView.visibility = View.GONE
        binding.profileBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topAppBar = requireActivity().findViewById(R.id.topAppBar)
        topAppBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        topAppBar.visibility = View.VISIBLE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(context)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.profile.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parse(resource)
        })
        viewModel.helps.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parseHelps(resource)
        })
        viewModel.userProfile.observe(viewLifecycleOwner, EventObserver { userProfile ->
            var name = userProfile.phone
            var email = "No email found"
            if (userProfile.name != "") {
                name = userProfile.name
            }
            if (userProfile.email != "") {
                email = userProfile.email
            }
            binding.include.userFullName.text = name
            binding.include.userEmailId.text = email

            if (args.readOnly) {
                binding.include.editProfileButton.visibility = View.GONE
            } else {
                binding.include.editProfileButton.visibility = View.VISIBLE
                binding.include.editProfileButton.setOnClickListener {

                    sharedViewModel.user.value = userProfile
                    val action =
                        ProfileFragmentDirections.actionProfileFragmentToUpdateProfileFragment(
                            userProfile.phone
                        )
                    findNavController().navigate(action)
                }
            }

            binding.include.karmaPoints.text = userProfile.karmapoints.toString()
            binding.include.helpedPoints.text = userProfile.helpextended.toString()
            Glide.with(this)
                .load(userProfile.profileUrl)
                .placeholder(R.drawable.ic_placeholder_profile)
                .timeout(60000)
                .centerCrop()
                .into(binding.include.profileImage)
            binding.include.verifiedIcon.visibility = View.GONE
            binding.include.profileProgressIndicator.visibility = View.GONE
            binding.include.profileContainer.visibility = View.VISIBLE
        })
        viewModel.myHelps.observe(viewLifecycleOwner, EventObserver { helps ->
            binding.profileHelpsProgressIndicator.visibility = View.GONE
            myHelps.clear()
            for (help in helps.data.listIterator()) {
                val activities = ArrayList<HelpChildItem>()
                for (helpActivity in help.activity) {
                    if (helpActivity.uid != "") {
                        val parcelableActivity = HelpChildItem(
                            help._id,
                            helpActivity.uid,
                            helpActivity.status,
                            helpActivity.userData.name,
                            helpActivity.userData.profileUrl,
                            helpActivity.userData.phone,
                            helpActivity.userData.email,
                            help.title,
                            helpActivity.userData.contactVia
                        )
                        activities.add(parcelableActivity)
                    }


                }
                val myHelp = HelpParentItem(
                    help._id,
                    help.helptype,
                    help.title,
                    help.description,
                    activities,
                    help.category.name,
                    help.category.url,
                    help.status
                )
                myHelps.add(myHelp)
            }
            if (myHelps.size > 0) {
                val adapter = MyHelpsAdapter(context, myHelps, args.readOnly)
                binding.myHelpsRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    myHelpsRecyclerView.adapter = adapter
                }
                binding.myHelpsRecyclerView.visibility = View.VISIBLE
            } else {
                binding.noItemsTextView.visibility = View.VISIBLE
            }
        })

        var userId = sessionManager.fetchUserId()
        if (!TextUtils.isEmpty(args.userId)) {
            userId = args.userId
        }

        viewModel.fetchProfile(userId)
        viewModel.fetchMyHelps(userId)
        //viewModel.fetchProfile("65e722614aafa9dffbcfe4f2aea43dd8")
        //viewModel.fetchMyHelps("65e722614aafa9dffbcfe4f2aea43dd8")
        binding.include.profileContainer.visibility = View.INVISIBLE
        binding.include.profileProgressIndicator.visibility = View.VISIBLE
    }

}