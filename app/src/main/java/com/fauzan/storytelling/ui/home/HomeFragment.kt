package com.fauzan.storytelling.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.fauzan.storytelling.adapter.LoadingStateAdapter
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private val storyAdapter: StoryAdapter by lazy {
        StoryAdapter() { story, binding ->
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

        setupRecyclerView()
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        storyAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
        binding.rvPosts.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { storyAdapter.retry() }
        )
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun observe() {
        viewModel.checkSession().observe(viewLifecycleOwner) { userModel ->
            if (userModel.token.isEmpty()) {
                val toLoginFragment = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                requireView().findNavController().navigate(toLoginFragment)
            } else {
                viewModel.getStories().observe(viewLifecycleOwner) { stories ->
                    storyAdapter.submitData(viewLifecycleOwner.lifecycle, stories)
                }
            }
        }
    }
}