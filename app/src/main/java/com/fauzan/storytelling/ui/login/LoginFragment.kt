package com.fauzan.storytelling.ui.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fauzan.storytelling.R
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentLoginBinding
import com.fauzan.storytelling.data.Result

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.etEmail.error = getString(R.string.email_cannot_be_empty)
                binding.etEmail.requestFocus()
            }

            if (password.isEmpty()) {
                binding.etPassword.error = getString(R.string.password_cannot_be_empty)
                binding.etPassword.requestFocus()
            }

            if (email.isEmpty() || password.isEmpty()) {
                return@setOnClickListener
            }

            login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            val toRegisterFragment = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            requireView().findNavController().navigate(toRegisterFragment)
        }
    }

    private fun login(email: String, password: String) {
        viewModel.login(email, password).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.text = ""
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    val toHomeFragment = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                    requireView().findNavController().navigate(toHomeFragment)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.text = getString(R.string.login)
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}