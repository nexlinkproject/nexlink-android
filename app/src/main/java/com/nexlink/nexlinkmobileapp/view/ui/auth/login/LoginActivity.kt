package com.nexlink.nexlinkmobileapp.view.ui.auth.login

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
import com.nexlink.nexlinkmobileapp.data.local.pref.UserModel
import com.nexlink.nexlinkmobileapp.databinding.ActivityLoginBinding
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.auth.signup.SignUpActivity
import com.nexlink.nexlinkmobileapp.view.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<LoginViewModel> {
        AuthModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.tfEmail.text.toString()
        val password = binding.tfPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            showToast("Email dan password tidak boleh kosong.")
            return
        }

        loginViewModel.login(email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)

                        val user = result.data.data
                        if (user != null) {
                            loginViewModel.saveSession(
                                UserModel(
                                    user.fullName.toString(),
                                    user.userId.toString(), user.accessToken.toString()
                                )
                            )
                        }

                        AlertDialog.Builder(this).apply {
                            setTitle("Login Success")
                            setMessage(getString(R.string.alert_login_success))
                            setPositiveButton("Masuk") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
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
                            setTitle("Login Failed")
                            setMessage(getString(R.string.wrong_email_and_password))
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