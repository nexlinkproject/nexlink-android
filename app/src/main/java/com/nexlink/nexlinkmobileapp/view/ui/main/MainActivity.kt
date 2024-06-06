package com.nexlink.nexlinkmobileapp.view.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Update toolbar menu untuk setiap fragment dan menentukan menu dan title
        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = destination.label
            if (destination.id == R.id.navigation_home) {
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