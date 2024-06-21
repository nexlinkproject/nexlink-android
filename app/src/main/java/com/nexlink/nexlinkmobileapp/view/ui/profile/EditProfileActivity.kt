package com.nexlink.nexlinkmobileapp.view.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityEditProfileBinding
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private val userViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        getUserById()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUserById() {
        val idUser = intent.getStringExtra(EXTRA_USER_ID)
        if (idUser != null) {
            userViewModel.getUserById(idUser).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)

                        val user = result.data.data?.user
                        if (user != null) {
                            binding.tfName.setText(user.fullName)
                            binding.tfEmail.setText(user.email)
                        }
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        Log.e("ProfileFragment", "Error Get User By Id : ${result.error}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}