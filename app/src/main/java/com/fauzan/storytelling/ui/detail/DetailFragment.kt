package com.fauzan.storytelling.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DetailViewModel> { ViewModelFactory.getInstance(requireActivity()) }
    private val args: DetailFragmentArgs by navArgs()

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

        binding.toolbar.setNavigationOnClickListener {
            requireView().findNavController().navigateUp()
        }

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
                    autofillContent(story.photoUrl, story.name, story.description)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun autofillContent(photoUrl: String, name: String, description: String) {
        Glide.with(requireContext()).load(photoUrl).into(binding.ivThumbnail)
        binding.tvTitle.text = name
        binding.tvDescription.text = description
    }
}