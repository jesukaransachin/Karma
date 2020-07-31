package com.ingrammicro.helpme.ui.createhelp


import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.databinding.CreateHelpFragmentBinding
import com.ingrammicro.helpme.utils.GIVE_HELP
import com.ingrammicro.helpme.utils.NEED_HELP
import com.ingrammicro.imdelivery.SharedViewModel
import kotlinx.android.synthetic.main.create_help_fragment.*
import kotlinx.android.synthetic.main.create_help_tab_fragment.*


class CreateHelpTabFragment : BottomSheetDialogFragment() {


    companion object {
        fun newInstance() = CreateHelpTabFragment().apply {  }
    }


    private lateinit var dialog: BottomSheetDialog
    private lateinit var behavior: BottomSheetBehavior<View>
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: CreateHelpFragmentBinding
    private var previousState = -1



    fun hideBottomSheetFromOutSide(event: MotionEvent) {
        if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            val outRect = Rect()
//            binding.parentFrameLayout.getGlobalVisibleRect(outRect)
            if (!outRect.contains(
                    event.rawX.toInt(),
                    event.rawY.toInt()
                )
            ) behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val d = it as BottomSheetDialog
//            val sheet = d.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            val bottomSheet: FrameLayout? =
                d.findViewById(com.google.android.material.R.id.design_bottom_sheet)

            behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.isHideable = false
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
//
//            bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
//            bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
            bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_help_tab_fragment, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel =
            activity.run { ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
        sharedViewModel.closeDialog.value = false
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initialiseView()
//        sharedViewModel.closeDialog.observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->
//
////            behavior.setHideable(true);
////            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//////            fragmentManager?.popBackStack()
////            dismiss()
////            dismiss()
//            findNavController().popBackStack()
////            childFragmentManager.popBackStackImmediate()
////            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        })


    }

    fun initialiseView(){
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Need Help"))
        tabLayout!!.addTab(tabLayout!!.newTab().setText("Give Help"))
        val fragment: CreateHelpFragment = CreateHelpFragment()
        val args = Bundle()
        args.putString("Type", NEED_HELP)
        fragment.setArguments(args)
        getChildFragmentManager()
            .beginTransaction().replace(R.id.containerLayout, fragment).commit()
        tabLayout!!.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                //load fragment or do whatever you want

                if (tab.position == 1) {
                    val fragment: CreateHelpFragment = CreateHelpFragment()
                    val args = Bundle()
                    args.putString("Type", GIVE_HELP)
                    fragment.setArguments(args)
                    getChildFragmentManager()
                        .beginTransaction().replace(R.id.containerLayout, fragment).commit()
                } else {
                    val fragment: CreateHelpFragment = CreateHelpFragment()
                    val args = Bundle()
                    args.putString("Type", NEED_HELP)
                    fragment.setArguments(args)
                    getChildFragmentManager()
                        .beginTransaction().replace(R.id.containerLayout, fragment).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        sharedViewModel.closeDialog.observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->

//            behavior.setHideable(true);
//            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
////            fragmentManager?.popBackStack()
//            dismiss()
//            dismiss()
            if( user == true) {
//                tabLayout.removeAllTabs()
                findNavController().popBackStack()
            }
//            childFragmentManager.popBackStackImmediate()
//            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);


//            tab
//            val manager: FragmentManager = (`object` as Fragment).getFragmentManager()
//            val trans: FragmentTransaction = manager.beginTransaction()
//            trans.remove(`object` as Fragment?)
//            trans.commit()
        })
    }





}