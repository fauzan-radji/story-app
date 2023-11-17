package com.fauzan.storytelling.ui.detail

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DetailViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private var mMap: GoogleMap? = null
    private val args: DetailFragmentArgs by navArgs()

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap

        setMapStyle(googleMap)
        viewModel.latLng.observe(viewLifecycleOwner) { latLng ->
            if (latLng != null) {
                binding.map.visibility = View.VISIBLE
                addMarker(
                    position = latLng,
                    title = args.name,
                    snippet = args.description
                )
            } else {
                binding.map.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        observe()
        setTransition()
        autofillContent(args.photoUrl, args.name, args.description)

        viewModel.getStory(args.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setTransition() {
        binding.ivThumbnail.transitionName = "thumbnail_${args.id}"
        binding.tvTitle.transitionName = "title_${args.id}"
        binding.tvDescription.transitionName = "description_${args.id}"
    }

    private fun observe() {
        viewModel.story.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    val story = result.data
                    binding.progressBar.visibility = View.GONE
                    autofillContent(story.photoUrl, story.name, story.description, story.lat, story.lon)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun autofillContent(photoUrl: String, name: String, description: String, lat: Double? = null, lon: Double? = null) {
        Glide.with(requireContext()).load(photoUrl).into(binding.ivThumbnail)
        binding.tvTitle.text = name
        binding.tvDescription.text = description

        viewModel.setLatLng(lat, lon)
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )

            if (!success) {
                Toast.makeText(requireContext(), "Error loading map style", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(requireContext(), "Error loading map style", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addMarker(position: LatLng, title: String, snippet: String) {
        mMap?.addMarker(
            MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
        )

        mMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                position,
                15f
            )
        )
    }
}