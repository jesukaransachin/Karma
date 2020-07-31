package com.ingrammicro.helpme.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.databinding.FragmentPhotoGalleryDialogBinding
import com.ingrammicro.helpme.ui.home.adapter.PhotoGalleryAdapter
import com.ingrammicro.imdelivery.SharedViewModel

class PhotoGalleryDialogFragment : DialogFragment() {

    val args: PhotoGalleryDialogFragmentArgs by navArgs()

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var photoGalleryAdapter: PhotoGalleryAdapter
    private lateinit var binding: FragmentPhotoGalleryDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
        sharedViewModel =
            activity.run { ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
    }

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoGalleryDialogBinding.inflate(inflater, container, false)
        binding.viewPager2.apply {
            photoGalleryAdapter = PhotoGalleryAdapter()
            adapter = photoGalleryAdapter
        }

        sharedViewModel.gallery.observe(viewLifecycleOwner, Observer { photos ->
            photoGalleryAdapter.addItems(photos)
            binding.viewPager2.setCurrentItem(args.position)
        })

        // Inflate the layout to use as dialog or embedded fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
    }

}