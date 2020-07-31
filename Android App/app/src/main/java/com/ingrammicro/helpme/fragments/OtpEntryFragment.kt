package com.ingrammicro.helpme.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.databinding.OtpEntryFragmentBinding
import com.ingrammicro.helpme.ui.BaseFragment
import com.ingrammicro.helpme.utils.AppUtils.getErrorMessage
import com.ingrammicro.helpme.utils.SessionManager
import com.ingrammicro.helpme.utils.shortSnackbar
import com.ingrammicro.helpme.viewmodels.OtpEntryViewModel
import com.ingrammicro.helpme.viewmodels.PhoneLoginViewModel

class OtpEntryFragment : BaseFragment() {

    companion object {
        fun newInstance() = OtpEntryFragment()
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: OtpEntryViewModel
    private lateinit var viewModelPhone: PhoneLoginViewModel
    private var resent: Boolean = false
    val args: OtpEntryFragmentArgs by navArgs()
    private var serverOTP = 0
    private var _binding: OtpEntryFragmentBinding? = null
    private val binding get() = _binding!!
    private val timer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.otpTimerText.text =
                "Resend PIN in 0:" + (millisUntilFinished / 1000).toString().padStart(2, '0')
        }

        override fun onFinish() {
            binding.otpResendText.visibility = View.VISIBLE
            serverOTP = -1
            resent = true
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OtpEntryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpResendText.visibility = View.INVISIBLE
        binding.progressBarOTP.visibility = View.INVISIBLE
        timer.start()
        binding.otpResendText.setOnClickListener { _ ->
            resent = true
            binding.otpResendText.visibility = View.INVISIBLE
            binding.otpVerifyButton.isEnabled = false
            binding.progressBarOTP.visibility = View.VISIBLE
            timer.start()
            binding.pinView.setText("")
            viewModelPhone.doLogin(args.phone)
        }
        binding.otpVerifyButton.setOnClickListener { _ ->
            binding.otpVerifyButton.isEnabled = false
            binding.progressBarOTP.visibility = View.VISIBLE
            val otp: Int = binding.pinView.text.toString().toInt()
            if (!resent) {
                serverOTP = args.otp
            } else {
                resent = false
            }

            if (otp.equals(serverOTP)) {
                Snackbar.make(binding.otpContextView, "Verified", Snackbar.LENGTH_LONG).show()
                viewModel.doVerification(args.id)
            } else {
                Snackbar.make(binding.otpContextView, "Incorrect OTP", Snackbar.LENGTH_LONG).show()
                binding.otpVerifyButton.isEnabled = true
                binding.progressBarOTP.visibility = View.INVISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OtpEntryViewModel::class.java)
        viewModelPhone = ViewModelProviders.of(this).get(PhoneLoginViewModel::class.java)

        viewModel.verify.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parse(resource)
        })

        viewModelPhone.login.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModelPhone.parse(resource)
        })

        viewModel.user.observe(viewLifecycleOwner, EventObserver { user ->
            sessionManager.saveAuthToken(user.accessToken)
            sessionManager.saveRefreshToken(user.refreshToken)
            sessionManager.saveUserLat(user.lat)
            sessionManager.saveUserLng(user.lng)
            sessionManager.saveUserId(user.id)

            binding.otpVerifyButton.isEnabled = true
            binding.progressBarOTP.visibility = View.INVISIBLE

            if (TextUtils.isEmpty(user.name)) {
                val action =
                    OtpEntryFragmentDirections.actionOtpEntryFragmentToProfileFragment(args.phone)
                findNavController().navigate(action)
            } else {
                findNavController().navigate(R.id.action_global_mapsFragment)
            }
        })

        viewModelPhone.user.observe(viewLifecycleOwner, EventObserver { user ->
            serverOTP = user.otp

            binding.otpVerifyButton.isEnabled = true
            binding.progressBarOTP.visibility = View.INVISIBLE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                val resId = getErrorMessage(error)
                val msg = if (resId == -1) error else getString(resId)
                shortSnackbar(binding.otpVerifyButton, msg).show()

                binding.otpVerifyButton.isEnabled = true
                binding.progressBarOTP.visibility = View.INVISIBLE
            }
        })
    }
}