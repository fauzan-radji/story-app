package com.fauzan.storytelling.ui.maps

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentMapsBinding
import com.fauzan.storytelling.data.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MapsViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private var mMap: GoogleMap? = null
    private val boundsBuilder = LatLngBounds.Builder()

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        viewModel.getStories().observe(viewLifecycleOwner) { stories ->
            when(stories) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    stories.data.filter { it.lon != null && it.lat != null }.forEach { story ->
                        val latLng = LatLng(story.lat!!, story.lon!!)
                        mMap?.addMarker(MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                        )
                        boundsBuilder.include(latLng)
                    }

                    val bounds = boundsBuilder.build()
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        300
                    ))
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), stories.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}