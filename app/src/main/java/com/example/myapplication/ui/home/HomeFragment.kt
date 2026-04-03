package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.data.session.SessionManager
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        binding.btnGoLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        binding.btnGoProducts.setOnClickListener {
            // Navigate by destination id to avoid action-resolution issues at runtime.
            findNavController().navigate(R.id.productsFragment)
        }
        binding.btnGoCategories.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }
        binding.btnGoOrders.setOnClickListener {
            if (sessionManager.isLoggedIn()) {
                findNavController().navigate(R.id.ordersFragment)
            } else {
                Toast.makeText(requireContext(), R.string.require_login_message, Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        binding.btnLogout.setOnClickListener {
            if (!sessionManager.isLoggedIn()) {
                Toast.makeText(requireContext(), R.string.require_login_message, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showLogoutConfirmDialog()
        }

        setFullName()
    }

    private fun setFullName(){
        val fullName = sessionManager.getCurrentFullName()
        binding.tvFullName.text = if (fullName != "") "Xin chào, $fullName" else getString(R.string.no_name)
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                sessionManager.clearSession()
                setFullName()
                Toast.makeText(requireContext(), R.string.logout_success_message, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}