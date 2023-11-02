package com.fauzan.storytelling.ui.add

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
import java.io.File

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private val args: AddFragmentArgs by navArgs()
    private val viewModel by viewModels<AddViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {it?.let { uri ->
        imageUri = uri
        showImage()
    }}
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private fun showImage() {
        imageUri?.let { uri ->
            binding.ivThumbnail.setImageURI(uri)
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
        binding.toolbar.setNavigationOnClickListener {
            requireView().findNavController().navigateUp()
        }

        binding.btnGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCamera.setOnClickListener {
            if (!allPermissionGranted()) {
                requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            } else {
                startCamera()
            }
        }

        binding.btnUpload.setOnClickListener {
            imageUri?.let { uri ->
                val imageFile = uriToFile(uri, requireContext())
                val description = binding.etDescription.text.toString()
                addStory(description, imageFile)
            }
        }
    }

    private fun addStory(description: String, imageFile: File) {
        viewModel.addStory(description, imageFile).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    requireView().findNavController().navigateUp()
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
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
        private const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}