package com.fauzan.storytelling.ui.register

import android.os.Bundle
import com.fauzan.storytelling.R
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.fauzan.storytelling.data.Result
import com.fauzan.storytelling.data.ViewModelFactory
import com.fauzan.storytelling.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegisterViewModel> { ViewModelFactory.getInstance(requireActivity()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.name_cannot_be_empty)
                binding.etName.requestFocus()
            }

            if (email.isEmpty()) {
                binding.etEmail.error = getString(R.string.email_cannot_be_empty)
                binding.etEmail.requestFocus()
            }

            if (password.isEmpty()) {
                binding.etPassword.error = getString(R.string.password_cannot_be_empty)
                binding.etPassword.requestFocus()
            }

            if (confirmPassword.isEmpty()) {
                binding.etConfirmPassword.error =
                    getString(R.string.confirm_password_cannot_be_empty)
                binding.etConfirmPassword.requestFocus()
            }

            if (password != confirmPassword) {
                binding.etConfirmPassword.error =
                    getString(R.string.password_and_confirm_password_must_be_same)
                binding.etConfirmPassword.requestFocus()
            }

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || password != confirmPassword) {
                return@setOnClickListener
            }

            register(name, email, password)
        }

        binding.btnLogin.setOnClickListener {
            val toLoginFragment = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            requireView().findNavController().navigate(toLoginFragment)
        }
    }

    private fun register(name: String, email: String, password: String) {
        viewModel.register(name, email, password).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.text = ""
                    binding.btnRegister.isEnabled = false
                }

                is Result.Success -> {
                    val toLoginFragment = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                    requireView().findNavController().navigate(toLoginFragment)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.text = getString(R.string.register)
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}