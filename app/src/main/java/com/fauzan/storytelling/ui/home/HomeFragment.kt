package com.fauzan.storytelling.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter(mutableListOf()) { story, binding ->
            val toDetailFragment = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                id = story.id,
                photoUrl = story.photoUrl,
                name = story.name,
                description = story.description
            )
            val extras = FragmentNavigatorExtras(
                Pair(binding.ivThumbnail, "thumbnail_${story.id}"),
                Pair(binding.tvTitle, "title_${story.id}"),
                Pair(binding.tvDescription, "description_${story.id}")
            )
            requireView().findNavController().navigate(toDetailFragment, extras)
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

        binding.rvPosts.adapter = storyAdapter
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observe() {
        viewModel.checkSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel.token.isEmpty()) {
                val toLoginFragment = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                requireView().findNavController().navigate(toLoginFragment)
            } else {
                viewModel.getStories()
            }
        }

        viewModel.stories.observe(viewLifecycleOwner) { result ->
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