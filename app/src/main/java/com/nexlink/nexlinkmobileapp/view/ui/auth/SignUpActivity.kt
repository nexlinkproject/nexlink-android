package com.nexlink.nexlinkmobileapp.view.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivitySignUpBinding
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory

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
        val username = "test"
        val email = binding.tfEmail.text.toString()
        val password = binding.tfPassword.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Name, email and password cannot be empty.")
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
                        AlertDialog.Builder(this).apply {
                            setTitle("Sign Up Success")
                            setMessage(getString(R.string.alert_signup_success))
                            setPositiveButton("Ok") { _, _ ->
                                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle("Sign Up Failed")
                            setMessage("An error occurred while signing up.")
                            setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                            println(result.error)
                        }
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