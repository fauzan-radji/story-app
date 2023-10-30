package com.fauzan.storytelling.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter(mutableListOf()) { story ->
            val toDetailFragment = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
            requireView().findNavController().navigate(toDetailFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPost.adapter = storyAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())
        setupOnClickListeners()
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickListeners() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_language -> {
                    Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.action_logout -> {
                    viewModel.logout()
                    true
                }

                else -> false
            }
        }

        binding.fabAdd.setOnClickListener {
            val toAddPostFragment = HomeFragmentDirections.actionHomeFragmentToAddFragment()
            requireView().findNavController().navigate(toAddPostFragment)
        }
    }

    private fun observe() {
        viewModel.userModel.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    //
                }

                is Result.Success -> {
                    if (result.data != null) {
                        viewModel.getPosts()
                    } else {
                        val toLoginFragment = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                        requireView().findNavController().navigate(toLoginFragment)
                    }
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.posts.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    storyAdapter.updateData(result.data)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}