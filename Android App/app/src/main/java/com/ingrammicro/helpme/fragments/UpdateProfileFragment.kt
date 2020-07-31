package com.ingrammicro.helpme.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraCharacteristics
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.transition.Transition
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.ingrammicro.helpme.BuildConfig
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.Event
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.databinding.UpdateProfileFragmentBinding
import com.ingrammicro.helpme.ui.BaseFragment
import com.ingrammicro.helpme.utils.SessionManager
import com.ingrammicro.helpme.utils.shortSnackbar
import com.ingrammicro.helpme.viewmodels.UpdateProfileViewModel
import com.ingrammicro.imdelivery.SharedViewModel
import me.echodev.resizer.Resizer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*

private const val REQUEST_TAKE_PHOTO = 10001
private const val REQUEST_TAKE_DOCUMENT_PHOTO = 20001

class UpdateProfileFragment : BaseFragment() {

    companion object {
        fun newInstance() = UpdateProfileFragment()
    }

    private lateinit var sessionManager: SessionManager
    val args: UpdateProfileFragmentArgs by navArgs()
    private var MY_PERMISSIONS_REQUEST_CAMERA = 100
    private var MY_PERMISSIONS_REQUEST_CAMERA_FOR_DOC = 200
    private lateinit var viewModel: UpdateProfileViewModel
    private var _binding: UpdateProfileFragmentBinding? = null
    var currentPhotoPath: String? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    val photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(context)
        sharedViewModel =
            activity.run { ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = UpdateProfileFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UpdateProfileViewModel::class.java)
        binding.viewModel = viewModel
        initialiseViews()
    }

    fun initialiseViews() {

        sharedViewModel.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer { user ->
            Log.i("Users",""+user)
            if (user != null) {

                viewModel.fullName.set(user.name)
                viewModel.email.set(user.email)
                viewModel.address.set(user.shortAddress)
                viewModel.age.set(user.age)

                if (user.contactVia == 1) {
                    viewModel.isPhoneDefaultContact.set(true)
                } else {
                    viewModel.isPhoneDefaultContact.set(false)
                }

                if (!user.profileUrl.isNullOrEmpty()) {
                   viewModel.isProfilePicProgressbarVisible.set(true)
                    Glide.with(this)
                        .asBitmap()
                        .load(user.profileUrl)
                        .into(object : CustomTarget<Bitmap>(){

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                binding.profileImgVw.setImageBitmap(resource)
                                val stream = ByteArrayOutputStream()
                                resource.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                                viewModel.image = encodeToBase64(stream.toByteArray())
                                viewModel.isProfilePicProgressbarVisible.set(false)
                            }
                        })
                }

                if (!user.documentPicUrl.isNullOrEmpty()) {
                    viewModel.isdocuPicProgressbarVisible.set(true)
                    Glide.with(this)
                        .asBitmap()
                        .load(user.documentPicUrl)
                        .into(object : CustomTarget<Bitmap>(){

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                            ) {
                                binding.documentImgVw.setImageBitmap(resource)
                                val stream = ByteArrayOutputStream()
                                resource.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                                viewModel.documentImage = encodeToBase64(stream.toByteArray())
                                viewModel.isdocuPicProgressbarVisible.set(false)
                            }
                        })

                    binding.documentView.visibility = View.VISIBLE
                    binding.documentAddButton.visibility = View.GONE
                }
                binding.skipButton.visibility = View.INVISIBLE

            }
        })

        viewModel.updateProfile.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parse(resource)
        })

        viewModel.updateProfileResponse.observe(viewLifecycleOwner, EventObserver { resource ->
            sessionManager.saveUserLat(resource.lat)
            sessionManager.saveUserLng(resource.lng)
            findNavController().navigate(R.id.action_global_mapsFragment)
        })

        viewModel._profileImage.observe(viewLifecycleOwner, EventObserver {
            val b = BitmapFactory.decodeByteArray(it, 0, it.size)

            binding.profileImgVw.setImageBitmap(b)

            viewModel.image = encodeToBase64(it)
            Log.d("customer image", viewModel.image)
        })
        viewModel._docuImage.observe(viewLifecycleOwner, EventObserver {
            val b = BitmapFactory.decodeByteArray(it, 0, it.size)

            binding.documentImgVw.setImageBitmap(b)
            binding.documentView.visibility = View.VISIBLE
            binding.documentAddButton.visibility = View.GONE

            viewModel.documentImage = encodeToBase64(it)
            Log.d("customer image", viewModel.image)
        })

        binding.profileImgVw.setOnClickListener { view ->
            if ((ActivityCompat.checkSelfPermission(
                    (context as Activity?)!!,
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_DENIED) || (ActivityCompat.checkSelfPermission(
                    (context as Activity?)!!,
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_DENIED)
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
            } else {
                openFrontCamera()
            }
        }
        binding.cancelDocumentButton.setOnClickListener { view ->
            binding.documentAddButton.visibility = View.VISIBLE
            binding.documentView.visibility = View.GONE
            viewModel.documentImage = ""

        }

        binding.documentAddButton.setOnClickListener { view ->
            if ((ActivityCompat.checkSelfPermission(
                    (context as Activity?)!!,
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_DENIED) || (ActivityCompat.checkSelfPermission(
                    (context as Activity?)!!,
                    Manifest.permission.CAMERA
                )
                        == PackageManager.PERMISSION_DENIED)
            ) {

                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_CAMERA_FOR_DOC
                )
            } else {
                openCamera()
            }
        }

        binding.saveProfileButton.setOnClickListener { view ->
            viewModel.updateProfile(args.phone)
        }
        viewModel.snackbar.observe(viewLifecycleOwner, EventObserver {

            shortSnackbar(binding.root, it).show()

        })
        binding.emailDefaultButton.setOnClickListener { view ->
            viewModel.onEmailDefaultClick()
        }

        binding.phoneDefaultButton.setOnClickListener { view ->
            viewModel.onPhoneDefaultClick()
        }

        binding.skipButton.setOnClickListener { view ->
            findNavController().navigate(R.id.action_global_mapsFragment)

        }
    }


    @Throws(IOException::class)
    fun getBase64EncodedImage(imageURL: String?): String? {
        try {
            val imageUrl = URL(imageURL)
            val ucon: URLConnection = imageUrl.openConnection()
            val `is`: InputStream = ucon.getInputStream()
            val baos = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var read = 0
            while (`is`.read(buffer, 0, buffer.size).also { read = it } != -1) {
                baos.write(buffer, 0, read)
            }
            baos.flush()
            return Base64.encodeToString(
                baos.toByteArray(),
                Base64.DEFAULT
            )
        } catch (e: Exception) {
            Log.d("Error", e.toString())
        }
        return null
    }

    private fun encodeToBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
//                REQUEST_TAKE_PHOTO -> mCurrentPhotoPath.let {
//            validatePhoto(it!!)
//
//        }
//
//
//            binding.profileImgVw.setImageURI(mCurrentPhotoPath)
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_TAKE_PHOTO -> currentPhotoPath.let {
                    validatePhoto(it!!, REQUEST_TAKE_PHOTO)

                }

                REQUEST_TAKE_DOCUMENT_PHOTO -> currentPhotoPath.let {
                    validatePhoto(it!!, REQUEST_TAKE_DOCUMENT_PHOTO)

                }
            }

        }

    }


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


    fun openCamera() {


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
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_DOCUMENT_PHOTO)
                }
            }
        }

    }

    fun openFrontCamera() {


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
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> {
                            takePictureIntent.putExtra(
                                "android.intent.extras.CAMERA_FACING",
                                CameraCharacteristics.LENS_FACING_FRONT
                            )  // Tested on API 24 Android version 7.0(Samsung S6)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
//                            takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT) // Tested on API 27 Android version 8.0(Nexus 6P)
                            takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                        }
                        else -> takePictureIntent.putExtra(
                            "android.intent.extras.CAMERA_FACING",
                            1
                        )  // Tested API 21 Android version 5.0.1(Samsung S4)
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                openFrontCamera()
            } else {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_FOR_DOC) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                openCamera()
            } else {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun validatePhoto(path: String, requestedFrom: Int) {

//        val exif = ExifInterface(path) //Since API Level 5
//         val exifOrientation: String = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
        val stream = ByteArrayOutputStream()

//        var newPath = decodeFile(path,250,250)
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

//        Log.i("BeforeCompress",""+bitmap.getByteCount()/1024)
//        newBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        Log.i("AfterCompress", "" + bitmap.getByteCount() / 1024)



        newBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        if (requestedFrom == REQUEST_TAKE_PHOTO) {
            viewModel.profileImage.value = Event(stream.toByteArray())
        } else {
            viewModel.docuImage.value = Event(stream.toByteArray())
        }

    }

//    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
//        val matrix = Matrix()
//        matrix.postRotate(angle)
//        var width : Int = (source.width * 0.5).toInt()
//        var height = (source.height * 0.5).toInt()
//
//        return Bitmap.createBitmap(
//            source, 0, 0, width , height,
//            matrix, true
//        )
//    }


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


//    private fun validatePhoto(path: String, requestedFrom : Int) {
//
//
//        var file = File(path)
//        val bitmap: Bitmap = Resizer(activity)
//            .setTargetLength(1080)
//            .setOutputFormat(Bitmap.CompressFormat.JPEG)
//            .setSourceImage(file)
//            .setOutputFilename("resized_image")
//            .getResizedBitmap()
//
////        var file = File(path)
////        val resizedImage = Resizer(activity)
////            .setTargetLength(1080)
////            .setQuality(80)
////            .setOutputFormat("JPEG")
////            .setOutputFilename("resized_image")
////            .setSourceImage(file)
////            .resizedFile
//
////        val storageDir: File =
////            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
////        val exif = ExifInterface(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()+"/resized_image.jpeg") //Since API Level 5
////        val exif = ExifInterface
////        val exifOrientation: String = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//
////        val bitmap = BitmapFactory.decodeFile(resizedImage.path)
//        Log.i("BeforeCompress",""+bitmap!!.getByteCount()/1024)
//
////        val orientation: Int = exif.getAttributeInt(
////            ExifInterface.TAG_ORIENTATION,
////            ExifInterface.ORIENTATION_UNDEFINED
////        )
////
////        var rotatedBitmap: Bitmap? = null
////        rotatedBitmap = when (orientation) {
////            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
////            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
////            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
////            ExifInterface.ORIENTATION_NORMAL -> bitmap
////            else -> bitmap
////        }
//
//
////        var newPath = decodeFile(path,250,250)
////        val bitmap = BitmapFactory.decodeFile(path)
//
////        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
//
////        bm = Bitmap.createScaledBitmap(rotatedBitmap!!, 200, 200, true);
////        val stream = ByteArrayOutputStream()
////        rotatedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
////        Log.i("AfterCompress",""+rotatedBitmap!!.getByteCount()/1024)
//
//        Glide.with(this)
//            .load(bitmap)
//            .dontTransform()
//            .into(binding.profileImgVw);
//
//
////        if (requestedFrom == REQUEST_TAKE_PHOTO) {
////            viewModel.profileImage.value = Event(stream.toByteArray())
////        } else {
////            viewModel.docuImage.value = Event(stream.toByteArray())
////        }
//
//
//    }
//
//
//    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
//        val matrix = Matrix()
//        matrix.postRotate(angle)
//        var width : Int = (source.width * 0.5).toInt()
//        var height = (source.height * 0.5).toInt()
//
//        return Bitmap.createBitmap(
//            source, 0, 0, 300 , 300,
//            matrix, true
//        )
//    }
//
//
//    private fun decodeFile(
//        path: String,
//        DESIREDWIDTH: Int,
//        DESIREDHEIGHT: Int
//    ): String? {
//        var strMyImagePath: String? = null
//        var scaledBitmap: Bitmap? = null
//        try {
//            // Part 1: Decode image
//            val unscaledBitmap: Bitmap =
//                ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingLogic.FIT)
//            scaledBitmap =
//                if (!(unscaledBitmap.width <= DESIREDWIDTH && unscaledBitmap.height <= DESIREDHEIGHT)) {
//                    // Part 2: Scale image
//
//                    ScalingUtilities.createScaledBitmap(
//                        unscaledBitmap,
//                        DESIREDWIDTH,
//                        DESIREDHEIGHT,
//                        ScalingLogic.FIT
//                    )
//                } else {
//                    unscaledBitmap.recycle()
//                    return path
//                }
//
//            // Store to tmp file
//            val extr =
//                Environment.getExternalStorageDirectory().toString()
//            val mFolder = File("$extr/ImageFolder")
//            if (!mFolder.exists()) {
//                mFolder.mkdir()
//            }
//            val s = "tmp.png"
//            val f = File(mFolder.absolutePath, s)
//            strMyImagePath = f.absolutePath
//            var fos: FileOutputStream? = null
//            try {
//                fos = FileOutputStream(f)
//                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos)
//                fos.flush()
//                fos.close()
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            scaledBitmap.recycle()
//        } catch (e: Throwable) {
//        }
//        return strMyImagePath ?: path
//    }

}
