package com.ingrammicro.helpme.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.databinding.PhoneLoginFragmentBinding
import com.ingrammicro.helpme.ui.BaseFragment
import com.ingrammicro.helpme.utils.AppUtils.getErrorMessage
import com.ingrammicro.helpme.utils.shortSnackbar
import com.ingrammicro.helpme.viewmodels.PhoneLoginViewModel
import com.jwang123.flagkit.FlagKit
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.android.synthetic.main.phone_login_fragment.*

class PhoneLoginFragment : BaseFragment() {

    companion object {
        fun newInstance() = PhoneLoginFragment()
    }

    private lateinit var viewModel: PhoneLoginViewModel
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var binding: PhoneLoginFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PhoneLoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        val tmanager =
            requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val country = tmanager.networkCountryIso
        val pNumber = phoneNumberUtil.getCountryCodeForRegion(country.toUpperCase()).toString()
        val countryCode = "+$pNumber"
        binding.phoneEditText.setText(countryCode)
        Log.d("country info", country)
        binding.phoneEditText.setCompoundDrawables(
            resizeDrawable(
                FlagKit.drawableWithFlag(
                    context,
                    country.toLowerCase()
                ), 12
            ), null, null, null
        )

        binding.phoneEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                val number = editable.toString()
                if (number.length >= 3) {
                    val countryIsoCode = getCountryIsoCode(number)
                    //binding.flagImageView.setImageDrawable(FlagKit.drawableWithFlag(context, countryIsoCode!!.toLowerCase()))
                }

            }
        })
        binding.confirmPhoneButton.setOnClickListener { view ->
            //val action = PhoneLoginFragmentDirections.actionPhoneLoginFragmentToOtpEntryFragment();
            //view.findNavController().navigate(action)
            binding.confirmPhoneButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.doLogin(binding.phoneEditText.text.toString())
        }
    }

    private fun resizeDrawable(drawable: Drawable, size: Int): Drawable? {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight

        drawable.setBounds(0, 0, (width * 0.5).toInt(), (height * 0.5).toInt())
        return drawable
    }

    private fun getCountryIsoCode(number: String): String? {
        val validatedNumber = if (number.startsWith("+")) number else "+$number"

        val phoneNumber = try {
            phoneNumberUtil.parse(validatedNumber, null)
        } catch (e: NumberParseException) {
            Log.e(TAG, "error during parsing a number")
            null
        }
            ?: return null

        return phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.countryCode)
    }

    fun String.toFlagEmoji(): String {
        if (this.length != 2) {
            return this
        }

        val countryCodeCaps =
            this.toUpperCase() // upper case is important because we are calculating offset
        val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

        // 2. It then checks if both characters are alphabet
        if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
            return this
        }

        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PhoneLoginViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.login.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parse(resource)
        })

        viewModel.user.observe(viewLifecycleOwner, EventObserver { user ->
            // save user data and navigate to next scree
            binding.confirmPhoneButton.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
            Log.d("USER OTP", user.otp.toString())
            val action = PhoneLoginFragmentDirections.actionPhoneLoginFragmentToOtpEntryFragment(
                user.id,
                user.otp,
                phoneEditText.text.toString()
            )
            findNavController().navigate(action)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                val resId = getErrorMessage(error)
                val msg = if (resId == -1) error else getString(resId)
                shortSnackbar(binding.confirmPhoneButton, msg).show()

                binding.confirmPhoneButton.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
            }
        })
    }
}