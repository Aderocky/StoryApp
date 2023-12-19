package com.example.storyapp_ade.view.upload

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import android.Manifest
import android.content.ContentValues.TAG
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import com.example.storyapp_ade.R
import com.example.storyapp_ade.advanced_ui.MyButton
import com.example.storyapp_ade.advanced_ui.MyEditText
import com.example.storyapp_ade.data.repository.ResultState
import com.example.storyapp_ade.databinding.ActivityUploadBinding
import com.example.storyapp_ade.di.getImageUri
import com.example.storyapp_ade.di.reduceFileImage
import com.example.storyapp_ade.di.uriToFile
import com.example.storyapp_ade.view.ViewModelFactory
import com.example.storyapp_ade.view.main.MainActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding

    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var latLng: LatLng? = null

    private lateinit var myButton: MyButton
    private lateinit var deskEditText: MyEditText


    private val viewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myButton = findViewById(R.id.btn_upload)
        deskEditText = findViewById(R.id.et_desk)

        setMyButtonEnable(false)
        setupAction()

        deskEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(true)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun setMyButtonEnable(isEnable: Boolean) {
        val result = deskEditText.text
        if (isEnable) {
            if (result != null) {
                myButton.isEnabled = true && result.isNotEmpty()
            }
        } else myButton.isEnabled = false
    }

    private fun setupAction() {
        binding.btnUpload.setOnClickListener {
            uploadStory()
        }
        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.cameraButton.setOnClickListener {
            startCamera()
        }
        binding.checkBox.setOnCheckedChangeListener { _, b ->
            if (b) {
                createLocationRequest()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivFoto.setImageURI(it)
        }
    }

    private fun uploadStory() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.etDesk.text.toString()

            viewModel.uploadImage(imageFile, description, latLng).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                            setMyButtonEnable(false)
                        }

                        is ResultState.Success -> {
                            showToast(result.data.message)

                            showLoading(false)
                            setMyButtonEnable(true)
                            val intent = Intent(this@UploadActivity, MainActivity::class.java)
                            startActivity(intent)
                        }

                        is ResultState.Error -> {
                            showToast(result.error)

                            showLoading(false)
                            setMyButtonEnable(true)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getCurrentLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getCurrentLocation()
                }

                else -> {
                    // No location access granted.
                }
            }
        }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK ->
                    Log.i(TAG, "onActivityResult: All location settings are satisfied.")

                RESULT_CANCELED ->
                    Toast.makeText(
                        this@UploadActivity,
                        "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getCurrentLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this@UploadActivity, sendEx.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latLng = LatLng(location.latitude, location.longitude)
                } else {
                    binding.checkBox.isEnabled = false
                    Toast.makeText(
                        this@UploadActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.pbHero.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvMohon.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}