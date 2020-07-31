package com.ingrammicro.helpme.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.databinding.LoginSelectionFragmentBinding
import com.ingrammicro.helpme.ui.BaseFragment
import com.ingrammicro.helpme.viewmodels.LoginSelectionViewModel

class LoginSelectionFragment : BaseFragment() {

    companion object {
        fun newInstance() =
            LoginSelectionFragment()
    }

    private lateinit var viewModel: LoginSelectionViewModel

    private var _binding: LoginSelectionFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginSelectionFragmentBinding.inflate(inflater, container, false)

        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = 10000L
        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width: Float = binding.imgvwBg.width.toFloat()
            val translationX = width * progress
            binding.imgvwBg.setTranslationX(translationX)
            binding.imgvwBg1.setTranslationX(translationX - width)
        }
        animator.start()

        binding.otpLogin.setOnClickListener {
            navigateToPhoneLogin()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = HelpMeApplication.prefs?.fetchUserId()
        if (!TextUtils.isEmpty(userId)) {
            findNavController().navigate(R.id.action_global_mapsFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginSelectionViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        if (checkLocationSettings()) {
            getDeviceLocation()
        }
    }

    fun navigateToPhoneLogin() {
        val action = LoginSelectionFragmentDirections.actionMainFragmentToPhoneLoginFragment()
        findNavController().navigate(action)
    }
}