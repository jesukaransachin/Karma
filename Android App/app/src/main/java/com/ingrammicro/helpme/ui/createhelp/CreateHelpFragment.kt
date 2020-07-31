package com.ingrammicro.helpme.ui.createhelp

//import com.ingrammicro.helpme.utils.shortSnackbar
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.chip.Chip
import com.ingrammicro.helpme.BuildConfig
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.databinding.CategoryGroupItemBinding
import com.ingrammicro.helpme.databinding.CreateHelpFragmentBinding
import com.ingrammicro.helpme.utils.NEED_HELP
import com.ingrammicro.helpme.utils.shortSnackbar
import com.ingrammicro.imdelivery.SharedViewModel
import kotlinx.android.synthetic.main.create_help_fragment.*
import kotlinx.android.synthetic.main.create_help_fragment.view.*
import me.echodev.resizer.Resizer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "Type"

private const val REQUEST_GET_CAMERAPHOTO1 = 10001
private const val REQUEST_GET_CAMERAPHOTO2 = 10002
private const val REQUEST_GET_CAMERAPHOTO3 = 10003
private const val REQUEST_GET_GALLERYPHOTO1 = 20001
private const val REQUEST_GET_GALLERYPHOTO2 = 20002
private const val REQUEST_GET_GALLERYPHOTO3 = 20003
private const val REQUEST_GET_VIDEO = 10004

/**
 *
 * A simple [Fragment] subclass.
 * Use the [TabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateHelpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: CreateHelpFragmentBinding
//    private var binding: CreateHelpFragmentBinding? = null
//    private val binding get() = _binding!!
    var currentPhotoPath: String? = null
    var typeSelected : String = NEED_HELP
    var MY_PERMISSIONS_REQUEST_CAMERA = 200

    private var mMaterialDialog: MaterialDialog? = null
    private lateinit var btnDialogClose : Button
    private lateinit var lottieAnimationViewSuccess : LottieAnimationView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModel: CreateHelpViewModel
    private lateinit var categoryIcons: TypedArray
    private lateinit var categoryIds: Array<String>
    private lateinit var videoPath: Uri
    private var videoBytes: ByteArray? = null
    private var dialogSelect : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           if ( it.getString(ARG_PARAM1) != null) {
             typeSelected = it.getString(ARG_PARAM1)!!
           }

        }
        sharedViewModel =
            activity.run { ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        _binding = CreateHelpFragmentBinding.inflate()
        binding = CreateHelpFragmentBinding.inflate(inflater, container, false)

        return binding.root

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TabFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            TabFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreateHelpViewModel::class.java)
        binding.viewModel = viewModel
        categoryIds = resources.getStringArray(R.array.category_ids)
        categoryIcons = resources.obtainTypedArray(R.array.category_icons)
        initialiseView()
    }


    fun initialiseView() {


        if (typeSelected.equals(NEED_HELP)){
            binding.highPriorityRelativeLayout.visibility = View.VISIBLE
        } else {
            binding.highPriorityRelativeLayout.visibility = View.GONE
        }

        viewModel.fetchCategories.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parseCategory(resource)
        })



        viewModel.categoriesResponse.observe(viewLifecycleOwner, EventObserver { categories ->
            Log.i("Categories",""+categories)
            if (!categories.isNullOrEmpty()) {
                chipGroup.removeAllViews()
                for (item in categories) {
                    val itemBinding = CategoryGroupItemBinding.inflate(layoutInflater)
                    val chip = itemBinding.root
                    val indexOfId = categoryIds.indexOf(item.categoryId)
                    chip.tag = item.categoryId
                    chip.text = item.name
                    chip.chipIcon = categoryIcons.getDrawable(indexOfId)
                    chip.iconStartPadding = 12f
                    chip.isChipIconVisible = true


                        val backgroundColor =
                            if (chip.isChecked) R.color.primaryColor else R.color.white
                        chip.setChipBackgroundColorResource(backgroundColor)

                        chip.setOnCheckedChangeListener { buttonView, isChecked ->
                            val myChip = buttonView as Chip
                            val myBackgroundColor =
                                if (isChecked) R.color.primaryColor else R.color.white
                            myChip.setChipBackgroundColorResource(myBackgroundColor)
                            val id = myChip.tag as String
                            viewModel.categorySelected.set(id)
                        }


//                    chip.isChecked = viewModel.categoryIds.contains(item.categoryId)
//                    chip.setOnCheckedChangeListener { buttonView, isChecked ->
//                        val id = buttonView.tag as String
//                        viewModel.getHelps(id, isChecked)
//                        Log.d(TAG, "checkedId ${id}")
//                    }
                    binding.chipGroup.addView(chip)
                }

            }
        })


        binding.uploadImgButton1.setOnClickListener { view ->
            startDialog(1)
       }

        viewModel._imageSelected1.observe(viewLifecycleOwner, EventObserver {
            val b = BitmapFactory.decodeByteArray(it, 0, it.size)

            binding.picture1Imageview1.setImageBitmap(b)
            binding.documentView1.visibility = View.VISIBLE
            binding.uploadImgButton1.visibility = View.GONE
            viewModel.image1 = encodeToBase64(it)
        })


        binding.cancelDocumentButton1.setOnClickListener { view ->
            binding.uploadImgButton1.visibility= View.VISIBLE
            binding.documentView1.visibility= View.GONE
            viewModel.image1 = ""

        }

        binding.uploadImgButton2.setOnClickListener { view ->
            startDialog(2)
        }

        binding.addVideoButton.setOnClickListener { view ->
            openGallery(REQUEST_GET_VIDEO)
        }

        viewModel._imageSelected2.observe(viewLifecycleOwner, EventObserver {
            val b = BitmapFactory.decodeByteArray(it, 0, it.size)

            binding.picture1Imageview2.setImageBitmap(b)
            binding.documentView2.visibility = View.VISIBLE
            binding.uploadImgButton2.visibility = View.GONE
            viewModel.image2 = encodeToBase64(it)
        })


        binding.cancelDocumentButton2.setOnClickListener { view ->
            binding.uploadImgButton2.visibility= View.VISIBLE
            binding.documentView2.visibility= View.GONE
            viewModel.image2 = ""

        }

        binding.cancelVideoFile.setOnClickListener {
            view ->
            binding.addVideoButton.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            viewModel.videoSelected.value = Event(ByteArray(0))
        }
        binding.uploadImgButton3.setOnClickListener { view ->
            startDialog(3)
        }

        viewModel._imageSelected3.observe(viewLifecycleOwner, EventObserver {
            val b = BitmapFactory.decodeByteArray(it, 0, it.size)

            binding.picture1Imageview3.setImageBitmap(b)
            binding.documentView3.visibility = View.VISIBLE
            binding.uploadImgButton3.visibility = View.GONE
            viewModel.image3 = encodeToBase64(it)
        })


        binding.cancelDocumentButton3.setOnClickListener { view ->
            binding.uploadImgButton3.visibility= View.VISIBLE
            binding.documentView3.visibility= View.GONE
            viewModel.image3 = ""
        }



        binding.priorityChecked.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
               viewModel.priority.set("High")
            } else {
                viewModel.priority.set("Low")
            }
        }

        binding.confirmButton.setOnClickListener{ view ->
//            sharedViewModel.closeDialog.value = true
            viewModel.createHelp(typeSelected)
        }




        viewModel.createHelp.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parse(resource)
        })
        viewModel.uploadVideo.observe(viewLifecycleOwner, EventObserver {resource->
            viewModel.parseVideo(resource)
            viewModel.videoName.set("")
        })
        viewModel._uploadedVideo.observe(viewLifecycleOwner, EventObserver {resource->
            createAnimationDialog()
        })

        viewModel._videoSelected.observe(viewLifecycleOwner, EventObserver {
            if (it.isNotEmpty()) {
                binding.videoView.videoThumbnail.setVideoURI(videoPath)
                binding.videoView.videoThumbnail.seekTo(1)
                binding.addVideoButton.visibility = View.GONE
                binding.videoView.visibility = View.VISIBLE
                videoBytes = it
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                viewModel.videoName.set("CreateHelpVideo$timeStamp.mp4")
            }

        })
        viewModel.createProfileResponse.observe(viewLifecycleOwner, EventObserver { resource ->
           //finish()
//            dialog.dismiss()


            if (videoBytes != null) {
                if (videoBytes!!.isNotEmpty()) {
                    viewModel.uploadVideo(resource.id, "Maps", videoBytes!!)
                } else {
                    createAnimationDialog()
                }

            } else {
                createAnimationDialog()
            }
//

//            shortSnackbar(binding.parentFrameLayout, getString(R.string.help_created_success)).show()

        })


        viewModel.snackbar.observe(viewLifecycleOwner, EventObserver {

            shortSnackbar(binding.parentFrameLayout, it).show()

        })


    }

    fun createAnimationDialog(){
//        return MaterialDialog.Builder(requireContext())
//            .customView(R.layout.lottie_anim_dialog, false)
//            .build()



        mMaterialDialog = MaterialDialog.Builder(requireContext())
            .customView(R.layout.lottie_anim_dialog, true)
            .build()


        btnDialogClose = mMaterialDialog!!.customView!!.findViewById<Button>(R.id.btnDialogClose)
        lottieAnimationViewSuccess = mMaterialDialog!!.customView!!.findViewById<LottieAnimationView>(R.id.animation_view2)
        lottieAnimationViewSuccess.playAnimation()
        btnDialogClose.setOnClickListener { view ->
            mMaterialDialog!!.dismiss()
            sharedViewModel.closeDialog.value = true
//             viewModel.createHelp(typeSelected)
        }
        mMaterialDialog!!.show()
    }

//    fun createAnimationSuccessDialog(): MaterialDialog? {
//        return MaterialDialog.Builder(requireContext())
//            .customView(R.layout.lottie_anim_dialog_success, false)
//            .build()
//
////        view.gone
//    }

        @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }




    fun openCamera(request : Int){


        if ((ActivityCompat.checkSelfPermission((context as Activity?)!!, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) ||  (ActivityCompat.checkSelfPermission((context as Activity?)!!, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED)) {

            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                request)
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {

                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, request)
                    }
                }
            }
        }
    }

    fun openGallery(request : Int){
        if ((ActivityCompat.checkSelfPermission((context as Activity?)!!, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) ||  (ActivityCompat.checkSelfPermission((context as Activity?)!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED)) {

            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                request)
        } else {


            val intent = Intent()
            var title = ""
            if (request == REQUEST_GET_VIDEO) {
                intent.type = "video/*"
                title = "Select a video"
            } else {
                intent.type = "image/*"
                title = "Select an image"
            }

            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), request)
        }

    }

    private fun encodeToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_GET_PHOTO1) {
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
            when (requestCode) {
                in 1000..20000 -> openCamera(requestCode)
                in 2000..3000 -> openGallery(requestCode)

            }

        } else {
//            openGallery(requestCode)
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
        }
//        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if ( requestCode == REQUEST_GET_GALLERYPHOTO1){

                val stream = ByteArrayOutputStream()
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data!!.data)

                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                viewModel.imageSelected1.value = Event(stream.toByteArray())

            } else if ( requestCode == REQUEST_GET_GALLERYPHOTO2){


                val stream = ByteArrayOutputStream()
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data!!.data)


                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                viewModel.imageSelected2.value = Event(stream.toByteArray())

            } else if ( requestCode == REQUEST_GET_GALLERYPHOTO3){


                val stream = ByteArrayOutputStream()
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data!!.data)


                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                viewModel.imageSelected3.value = Event(stream.toByteArray())

            } else if (requestCode == REQUEST_GET_VIDEO) {
                val stream = ByteArrayOutputStream()

                var os = ByteArrayOutputStream()
                var inputStream = requireActivity().contentResolver.openInputStream(data!!.data!!)
                var byteArray = inputStream!!.readBytes()
                videoPath = data!!.data!!
                viewModel.videoSelected.value = Event(byteArray)
            } else {
                validatePhoto(currentPhotoPath!!, requestCode)
            }

        }

    }

    private fun validatePhoto(path: String, requestedFrom: Int) {

        val stream = ByteArrayOutputStream()
        val bitmap1 = BitmapFactory.decodeFile(path)
        Log.i("BeforeCompress", "" + bitmap1.getByteCount() / 1024)
        val file = File(path)
        val bitmap: Bitmap = Resizer(activity)
            .setTargetLength(1080)
            .setOutputFormat(Bitmap.CompressFormat.JPEG)
            .setSourceImage(file)
            .setOutputFilename("resized_image")
            .getResizedBitmap()

        var newBitmap = imageOreintationValidator(bitmap, path)
       Log.i("AfterCompress", "" + bitmap.getByteCount() / 1024)



        newBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        if (requestedFrom == REQUEST_GET_CAMERAPHOTO1) {
            viewModel.imageSelected1.value = Event(stream.toByteArray())
        } else if (requestedFrom == REQUEST_GET_CAMERAPHOTO2){
            viewModel.imageSelected2.value = Event(stream.toByteArray())
        } else if (requestedFrom == REQUEST_GET_CAMERAPHOTO3) {
            viewModel.imageSelected3.value = Event(stream.toByteArray())
        }

    }

    private fun imageOreintationValidator(bitmap: Bitmap, path: String): Bitmap? {
        var bitmap: Bitmap? = bitmap
        val ei: ExifInterface
        try {
            ei = ExifInterface(path)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap =
                    rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap =
                    rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap =
                    rotateImage(bitmap, 270f)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        var bitmap: Bitmap? = null
        val matrix = Matrix()
        matrix.postRotate(angle)
        try {
            bitmap = Bitmap.createBitmap(
                source!!, 0, 0, source.width, source.height,
                matrix, true
            )
        } catch (err: OutOfMemoryError) {
            err.printStackTrace()
        }
        return bitmap
    }


    private fun startDialog(request : Int) {
        val linlayGallery: LinearLayout
        val linlayCamera: LinearLayout
        // create an alert builder

        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(
            activity
        )
//        val builder = AlertDialog.Builder(requireContext())
        myAlertDialog.setMessage("Select Option to get Image:")


        // set the custom layout
        val customLayout: View =
            layoutInflater.inflate(R.layout.alert_select_image_selection, null)
        myAlertDialog.setView(customLayout)
        linlayGallery =
            customLayout.findViewById<LinearLayout>(R.id.linlayGallery)
        linlayCamera =
            customLayout.findViewById<LinearLayout>(R.id.linlayCamera)
        linlayGallery.setOnClickListener(View.OnClickListener {

            when(request){
                1 -> openGallery(REQUEST_GET_GALLERYPHOTO1)
                2 -> openGallery(REQUEST_GET_GALLERYPHOTO2)
                3 -> openGallery(REQUEST_GET_GALLERYPHOTO3)
            }

            dialogSelect!!.dismiss()
        })
        linlayCamera.setOnClickListener(View.OnClickListener {
            when(request){
                1 -> openCamera(REQUEST_GET_CAMERAPHOTO1)
                2 -> openCamera(REQUEST_GET_CAMERAPHOTO2)
                3 -> openCamera(REQUEST_GET_CAMERAPHOTO3)
            }

            dialogSelect!!.dismiss()
        })
        // add a butto
        myAlertDialog.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.dismiss() }


        dialogSelect = myAlertDialog.show()

    }


    private fun startDialog1(request : Int) {
        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(
            activity
        )
        myAlertDialog.setTitle("Upload Pictures Option")
        myAlertDialog.setMessage("How do you want to set your picture?")
        myAlertDialog.setPositiveButton("Gallery",
            object : DialogInterface.OnClickListener  {
                override fun onClick(arg0: DialogInterface?, arg1: Int) {
                    when(request){
                        1 -> openGallery(REQUEST_GET_GALLERYPHOTO1)
                        2 -> openGallery(REQUEST_GET_GALLERYPHOTO2)
                        3 -> openGallery(REQUEST_GET_GALLERYPHOTO3)
                    }

                }
            })
        myAlertDialog.setNegativeButton("Camera",
            object : DialogInterface.OnClickListener {

                override fun onClick(arg0: DialogInterface?, arg1: Int) {
                    when(request){
                        1 -> openCamera(REQUEST_GET_CAMERAPHOTO1)
                        2 -> openCamera(REQUEST_GET_CAMERAPHOTO2)
                        3 -> openCamera(REQUEST_GET_CAMERAPHOTO3)
                    }
//                    val intent = Intent(
//                        MediaStore.ACTION_IMAGE_CAPTURE
//                    )
//                    val f = File(
//                        Environment
//                            .getExternalStorageDirectory(), "temp.jpg"
//                    )
//                    intent.putExtra(
//                        MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(f)
//                    )
//                    startActivityForResult(
//                        intent,
//                        CAMERA_REQUEST
//                    )
                }
            })
        myAlertDialog.show()
    }


}