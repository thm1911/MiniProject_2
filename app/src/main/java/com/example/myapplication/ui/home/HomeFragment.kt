package com.example.myapplication.ui.home


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProductsFragment())
        }
        binding.btnGoCategories.setOnClickListener {
        }
        binding.btnGoProductDetail.setOnClickListener {
        }

        setFullName()
    }

    private fun setFullName(){
        val fullName = sessionManager.getCurrentFullName()
        if(fullName != "") binding.tvFullName.text = "Xin chao, $fullName"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}