package com.fauzan.storytelling.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()
        observe()
    }

    private fun setupOnClickListeners() {
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observe() {
        viewModel.userModel.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.textView.text = "Loading..."
                }

                is Result.Success -> {
                    if(result.data != null) {
                        binding.textView.text = result.data.name
                    } else {
                        val toLoginFragment = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                        requireView().findNavController().navigate(toLoginFragment)
                    }
                }

                is Result.Error -> {
                    binding.textView.text = result.error
                }
            }
        }
    }
}