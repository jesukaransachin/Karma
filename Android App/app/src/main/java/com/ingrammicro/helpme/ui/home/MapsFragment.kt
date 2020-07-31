package com.ingrammicro.helpme.ui.home

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.Chip
import com.ingrammicro.helpme.HelpMeApplication
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.data.model.Category
import com.ingrammicro.helpme.data.model.Help
import com.ingrammicro.helpme.databinding.CategoryGroupItemBinding
import com.ingrammicro.helpme.databinding.FragmentMapsBinding
import com.ingrammicro.helpme.ui.BaseFragment
import com.ingrammicro.helpme.ui.createhelp.CreateHelpTabFragment
import com.ingrammicro.helpme.ui.home.adapter.HelpPagerAdapter
import com.ingrammicro.helpme.utils.AppUtils.getErrorMessage
import com.ingrammicro.helpme.utils.NEED_HELP
import com.ingrammicro.helpme.utils.GIVE_HELP
import com.ingrammicro.helpme.utils.shortSnackbar
import com.ingrammicro.imdelivery.SharedViewModel
import com.mancj.materialsearchbar.MaterialSearchBar

private const val ZOOM_LEVEL = 14f
private const val TAG = "MapsFragment"

class MapsFragment : BaseFragment(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback,
    MaterialSearchBar.OnSearchActionListener {

    private var lastSelectedMarker: Marker? = null
    private val markerRainbow = arrayListOf<Marker>()

    private var bottomSheetFragment: CreateHelpTabFragment? = null
    private val viewModel: HelpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var categoryIds: Array<String>
    private lateinit var categoryIcons: TypedArray
    private lateinit var googleMap: GoogleMap
    private lateinit var helpPagerAdapter: HelpPagerAdapter
    private lateinit var binding: FragmentMapsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapsBinding.bind(view)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        categoryIds = resources.getStringArray(R.array.category_ids)
        categoryIcons = resources.obtainTypedArray(R.array.category_icons)

        helpPagerAdapter = HelpPagerAdapter(viewModel)
        binding.viewPager2.adapter = helpPagerAdapter

        binding.viewPager2.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
        }

        binding.viewPager2.setPageTransformer(CompositePageTransformer().also {
            it.addTransformer(MarginPageTransformer(1))
        })

        binding.materialSearchBar.isSuggestionsEnabled = false
        binding.materialSearchBar.setOnSearchActionListener(this)

//        val color = Color.BLUE
//        val porterDuffColorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.DST_OVER)
//        binding.progressBar.progressDrawable.colorFilter = porterDuffColorFilter

        // Change the default color of progress bar programmatically
//        binding.progressBar.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
//        binding.progressBar.progressDrawable.setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_IN)

        setListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onMapReady(map: GoogleMap?) {

        Log.d(TAG, "onMapReady")

        // return early if the map was not initialised properly
        googleMap = map ?: return

        with(googleMap) {
            setMapStyle(this)
            // Hide the zoom controls as the button panel will cover it.
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isMyLocationButtonEnabled = false

            // Set listeners for marker events.  See the bottom of this class for their behavior.
            setOnMarkerClickListener(this@MapsFragment)

            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            setContentDescription("Map with lots of markers.")

            moveToMyLocation(false)
        }

        subscribeToChanges()
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // Retrieve the data from the marker.
        marker?.let {
            val position = it.tag as Int
            binding.viewPager2.setCurrentItem(position, true)
        }
        return false;
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.map_standard_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun setListener() {

        binding.setSearchClickListener {
            binding.materialSearchBar.openSearch()
            binding.materialSearchBar.visibility = View.VISIBLE
        }

        binding.setRefreshClickListener {
            viewModel.getHelps()
        }

        binding.setMyLocationClickListener {
            moveToMyLocation(true)
        }

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                if (viewModel.lastposition == -1) {
                    return
                }

                lastSelectedMarker?.let { marker ->
                    val lastSelectedPosition = marker.tag as Int
                    val lastSelectedItem = helpPagerAdapter.getItem(lastSelectedPosition)
                    setMarkerImage(lastSelectedItem.helpType, marker)
                }

                val item = helpPagerAdapter.getItem(position)
                val latLng = LatLng(
                    item.latitude,
                    item.longitude
                )
                val marker = markerRainbow[position]
                setSelectedMarkerImage(item.helpType, marker)
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

                lastSelectedMarker = marker
                viewModel.storeLastPosition(position)
            }
        })

        binding.fab.setOnClickListener {
            val categories = viewModel.categoryList
            sharedViewModel.categories.value = categories

            findNavController().navigate(R.id.navigate_to_createhelp)

//            if (bottomSheetFragment == null) {
//                bottomSheetFragment = CreateHelpTabFragment()
//
//                bottomSheetFragment?.show(childFragmentManager, bottomSheetFragment?.tag)
//            } else {
////                bottomSheetFragment?.dismiss()
//                bottomSheetFragment?.showAgain()//(//childFragmentManager, bottomSheetFragment?.tag)
////                bottomSheetFragment?.updateContent(playerResponse)
//            }
        }

        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_user_profile)
        }

        binding.heroesButton.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_heroes)
        }

        binding.notificationsButton.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_notifications)
        }

        binding.informationButton.setOnClickListener {
            findNavController().navigate(R.id.navigate_to_information)
        }
    }

    fun subscribeToChanges() {

        viewModel.fetchHelp.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parseResponse(resource)
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            binding.progressLayout.visibility = if (loading) View.VISIBLE else View.GONE
        })

        viewModel.notifyAdapter.observe(viewLifecycleOwner, Observer {
            val helpList = viewModel.helpList
            if (helpList.isNullOrEmpty()) {
                googleMap.clear()
            } else {
                addMarkersToMap(helpList)
            }
            helpPagerAdapter.notifyDataSetChanged()
        })

        viewModel.notifyCategories.observe(viewLifecycleOwner, Observer {
            val categories = viewModel.categoryList
            setupCategories(categories)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                val resId = getErrorMessage(error)
                val msg = if (resId == -1) error else getString(resId)
                shortSnackbar(binding.coordinatorLayout, msg).setAnchorView(binding.fab).show()
            }
        })

        viewModel.notifyPosition.observe(viewLifecycleOwner, EventObserver { position ->
            helpPagerAdapter.notifyItemChanged(position)
        })

        viewModel.detailsEnabled.observe(viewLifecycleOwner, EventObserver {
            helpPagerAdapter.notifyDataSetChanged()
        })

        viewModel.gallery.observe(viewLifecycleOwner, EventObserver { photos ->
            val action = MapsFragmentDirections.navigateToPhotoGallery(viewModel.childPosition)
            findNavController().navigate(action)
            sharedViewModel.gallery.value = photos
        })
    }

    private fun setupCategories(categories: ArrayList<Category>) {

        binding.chipGroup.removeAllViews()

        for (item in categories) {
            val indexOfId = categoryIds.indexOf(item.categoryId)
            val itemBinding = CategoryGroupItemBinding.inflate(layoutInflater)
            val chip = itemBinding.root
            chip.tag = item.categoryId
            chip.text = item.name
            chip.chipIcon = categoryIcons.getDrawable(indexOfId)
            chip.iconStartPadding = 12f
            chip.isChipIconVisible = true
            chip.isChecked = viewModel.categoryIds.contains(item.categoryId)
            val backgroundColor = if (chip.isChecked) R.color.primaryColor else R.color.white
            chip.setChipBackgroundColorResource(backgroundColor)

            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                val myChip = buttonView as Chip
                val myBackgroundColor = if (isChecked) R.color.primaryColor else R.color.white
                myChip.setChipBackgroundColorResource(myBackgroundColor)
                val id = myChip.tag as String
                viewModel.getHelps(id, isChecked)
                Log.d(TAG, "checkedId ${id}")
            }
            binding.chipGroup.addView(chip)
        }
    }

    private fun addMarkersToMap(helps: ArrayList<Help>) {

        googleMap.clear()
        markerRainbow.clear()

        // create bounds that encompass every location we reference
        val boundsBuilder = LatLngBounds.Builder()

        // include all places we have markers for on the map
        for (position in helps.indices) {
            val item = helps[position]
            val latLng = LatLng(
                item.latitude,
                item.longitude
            )

            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(item.title)
            setMarkerImage(item.helpType, markerOptions)
            boundsBuilder.include(latLng)

            val marker = googleMap.addMarker(
                markerOptions
            )
            marker.tag = position
            markerRainbow.add(marker)
        }

        lastSelectedMarker = markerRainbow[viewModel.lastposition]
        val item = helps[viewModel.lastposition]
        setSelectedMarkerImage(item.helpType, lastSelectedMarker)

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastSelectedMarker?.position))
        binding.viewPager2.setCurrentItem(viewModel.lastposition, true)
    }

    private fun setMarkerImage(helpType: String, markerOptions: MarkerOptions) {
        var markerIcon = 0
        when (helpType) {
            NEED_HELP -> {
                markerIcon = R.drawable.ic_marker_need_help
            }
            GIVE_HELP -> {
                markerIcon = R.drawable.ic_marker_give_help
            }
        }
        if (markerIcon != 0) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(markerIcon))
        }
    }

    private fun setMarkerImage(helpType: String, marker: Marker?) {
        var markerIcon = 0
        when (helpType) {
            NEED_HELP -> {
                markerIcon = R.drawable.ic_marker_need_help
            }
            GIVE_HELP -> {
                markerIcon = R.drawable.ic_marker_give_help
            }
        }
        if (markerIcon != 0) {
            marker?.setIcon(BitmapDescriptorFactory.fromResource(markerIcon))
            marker?.zIndex = 0.0f
        }
    }

    private fun setSelectedMarkerImage(helpType: String, marker: Marker?) {
        var markerIcon = 0
        when (helpType) {
            NEED_HELP -> {
                markerIcon = R.drawable.ic_marker_need_help_selected
            }
            GIVE_HELP -> {
                markerIcon = R.drawable.ic_marker_give_help_selected
            }
        }
        if (markerIcon != 0) {
            marker?.setIcon(BitmapDescriptorFactory.fromResource(markerIcon))
            marker?.zIndex = 1.0f
        }
    }

    private fun moveToMyLocation(animate: Boolean) {

        val latitude = HelpMeApplication.prefs?.getLatitude()!!
        val longitude = HelpMeApplication.prefs?.getLongitude()!!

        val latLng = LatLng(
            latitude.toDouble(),
            longitude.toDouble()
        )

        if (animate) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))
        }
    }

    fun hideKeyboard() {
        val currentFocus = requireActivity().currentFocus
        currentFocus?.let { view ->
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onButtonClicked(buttonCode: Int) {
        binding.materialSearchBar.text = ""
        binding.materialSearchBar.visibility = View.GONE
        viewModel.closeSearch()
    }

    override fun onSearchStateChanged(enabled: Boolean) {
        if (!enabled) {
            binding.materialSearchBar.text = ""
            binding.materialSearchBar.visibility = View.GONE
            viewModel.closeSearch()
        }
    }

    override fun onSearchConfirmed(query: CharSequence?) {
        if (TextUtils.isEmpty(query)) {
            return
        }

        viewModel.getHelps(query.toString())
        hideKeyboard()
    }
}