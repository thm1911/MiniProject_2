package com.example.myapplication.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.ui.login.LoginViewModel

import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(
            AuthRepository(
                userDao = AppDatabase
                    .Companion.getInstance(requireContext()).userDao(),
                sessionManager = SessionManager(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                username = binding.edtUsername.text?.toString().orEmpty(),
                password = binding.edtPassword.text?.toString().orEmpty()
            )

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnLogin.isEnabled = !state.isLoading
                    binding.edtUsername.isEnabled = !state.isLoading
                    binding.edtPassword.isEnabled = !state.isLoading

                    if (!state.isLoggedIn) {
                        binding.tvStatus.text = state.errorMessage
                            ?: getString(com.example.myapplication.R.string.login_default_message)
                    } else{
                        findNavController().popBackStack()
                    }
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}