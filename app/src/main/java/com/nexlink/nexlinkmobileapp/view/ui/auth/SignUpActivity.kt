package com.nexlink.nexlinkmobileapp.view.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivitySignUpBinding
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent

class SignUpActivity : AppCompatActivity() {

    private val signUpViewModel by viewModels<AuthViewModel> {
        AuthModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSignUp.setOnClickListener {
            register()
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun register() {
        val name = binding.tfName.text.toString()
        val username = name.toLowerCase().replace(" ", "")
        val email = binding.tfEmail.text.toString()
        val password = binding.tfPassword.text.toString()
        val confirmPassword = binding.tfConfirmPassword.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            alertInfoDialog(
                context = this,
                layoutInflater = layoutInflater,
                title = "Invalid Input",
                message = "Name, email, password, and confirm password cannot be empty.",
                icons = "warning"
            )
            return
        }

        if (password != confirmPassword) {
            alertInfoDialog(
                context = this,
                layoutInflater = layoutInflater,
                title = "Password Mismatch",
                message = "Password and confirm password do not match.",
                icons = "warning"
            )
            return
        }

        signUpViewModel.register(name, username, email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        alertInfoDialogWithEvent(
                            context = this,
                            layoutInflater = layoutInflater,
                            onOkClicked = {
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
//                                intent.flags =
//                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            },
                            title = "Sign Up Success",
                            message = getString(R.string.alert_signup_success),
                            icons = "success"
                        )

                        Log.d("SignUpActivity", "User ${result.data.data?.user?.fullName} registered successfully.")
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        alertInfoDialog(
                            context = this,
                            layoutInflater = layoutInflater,
                            title = "Sign Up Failed",
                            message = "An error occurred while signing up.",
                            icons = "error"
                        )

                        Log.e("SignUpActivity", "sign up error: ${result.error}")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
