package com.nexlink.nexlinkmobileapp.view.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nexlink.nexlinkmobileapp.databinding.FragmentProfileBinding
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.auth.AuthViewModel
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog

class ProfileFragment : Fragment() {

    private val authViewModel by viewModels<AuthViewModel> {
        AuthModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        authViewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.tvProfileName.text = user.fullName
        }

        binding.btnProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        return root
    }

    private fun showDeleteConfirmationDialog() {
        alertConfirmDialog(
            context = requireContext(),
            layoutInflater = layoutInflater,
            onYesClicked = {
                authViewModel.logout()
            },
            title = "Logout",
            message = "Are you sure you want to logout?",
            icons = "info"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}