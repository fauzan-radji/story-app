package com.fauzan.storytelling.ui.add

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentAddBinding
import com.fauzan.storytelling.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val args: AddFragmentArgs by navArgs()
    private val viewModel by viewModels<AddViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {it?.let { uri ->
        imageUri = uri
        showImage()
    }}
    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[CAMERA_PERMISSION] ?: false -> {
                startCamera()
            }
            LOCATION_PERMISSIONS.any { permissions[it] ?: false } -> {
                getMyLocation()
            }
            else -> {
                Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

        binding.cbLocation.isChecked = LOCATION_PERMISSIONS.any { checkPermission(it) }
    }

    private fun checkPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
        requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermission(permissions: Array<String>): Boolean = permissions.all {
        checkPermission(it)
    }

    private fun showImage() {
        imageUri?.let { uri ->
            binding.ivThumbnail.setImageURI(uri)
        }
    }

    private fun getMyLocation() {
        if (!checkPermission(LOCATION_PERMISSIONS)) {
            Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                viewModel.setMyLocation(it.latitude, it.longitude)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListener()

        args.imageUri?.let { uriString ->
            imageUri = Uri.parse(uriString)
            showImage()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupOnClickListener() {
        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCamera.setOnClickListener {
            if (!checkPermission(CAMERA_PERMISSION)) {
                requestPermissions.launch(arrayOf(CAMERA_PERMISSION))
            } else {
                startCamera()
            }
        }

        binding.cbLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (checkPermission(LOCATION_PERMISSIONS)) {
                    getMyLocation()
                } else {
                    requestPermissions.launch(LOCATION_PERMISSIONS)
                }
            } else {
                viewModel.setMyLocation(null, null)
            }
        }

        binding.btnUpload.setOnClickListener {
            imageUri?.let { uri ->
                val imageFile = uriToFile(uri, requireContext())
                val description = binding.etDescription.text.toString()
                addStory(description, imageFile)
            }
        }

        viewModel.lat.observe(viewLifecycleOwner) { binding.tvLat.text = getString(R.string.latitude, it?.toString() ?: "N/A") }
        viewModel.lon.observe(viewLifecycleOwner) { binding.tvLon.text = getString(R.string.longitude, it?.toString() ?: "N/A") }
        viewModel.isIncludeLocation.observe(viewLifecycleOwner) { binding.cbLocation.isChecked = it }
    }

    private fun addStory(description: String, imageFile: File) {
        viewModel.addStory(description, imageFile).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnUpload.text = ""
                    binding.btnUpload.isEnabled = false
                }

                is Result.Success -> {
                    requireView().findNavController().navigateUp()
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnUpload.text = getString(R.string.upload)
                    binding.btnUpload.isEnabled = true
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startCamera() {
        val toCameraFragment = AddFragmentDirections.actionAddFragmentToCameraFragment()
        requireView().findNavController().navigate(toCameraFragment)
    }

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}