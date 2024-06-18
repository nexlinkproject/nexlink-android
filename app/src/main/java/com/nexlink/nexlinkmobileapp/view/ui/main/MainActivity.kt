package com.nexlink.nexlinkmobileapp.view.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityMainBinding
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.auth.AuthViewModel
import com.nexlink.nexlinkmobileapp.view.ui.auth.LoginActivity
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel

class MainActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel> {
        AuthModelFactory.getInstance(this)
    }

    private val userViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                userViewModel.getUserById(user.userId).observe(this) { result ->
                    when (result) {
                        is ResultState.Loading -> {}
                        is ResultState.Success -> {
                            Log.i("MainActivity", "User ${result.data.data?.user?.fullName} Was Login")
                        }
                        is ResultState.Error -> {
                            AlertDialog.Builder(this).apply {
                                setTitle("Session Expired")
                                setMessage("Your session has expired. Please login again.")
                                setPositiveButton("Ok") { _, _ ->
                                    authViewModel.logout()
                                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                                    finish()
                                }
                                create()
                                show()
                            }

                            Log.i("MainActivity", "User not found, Error : ${result.error}")
                        }
                    }
                }
            }
        }

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Update toolbar menu untuk setiap fragment dan menentukan menu dan title
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = destination.label
            if (destination.id == R.id.navigation_home || destination.id == R.id.navigation_profile) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }

            when (destination.id) {
                R.id.navigation_chats -> {
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.chats_toolbar_menu)
                }

                R.id.navigation_projects -> {
                    toolbar.menu.clear()
                    toolbar.inflateMenu(R.menu.projects_toolbar_menu)
                }

                R.id.navigation_profile -> {
                    toolbar.menu.clear()
                }

                else -> {
                    toolbar.menu.clear()
                }
            }
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_search_chats_toolbar -> {
                    Toast.makeText(this, "Search Chats", Toast.LENGTH_SHORT).show()
                    true // Return true to indicate the click was handled
                }

                R.id.item_setting_chats_toolbar -> {
                    Toast.makeText(this, "Setting Chats", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.item_notification_projects_toolbar -> {
                    Toast.makeText(this, "Notification Projects", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }


//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}