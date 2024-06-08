package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.databinding.ActivityAddTeammatesAndTaskBinding

class AddTeammatesAndTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTeammatesAndTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTeammatesAndTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Teammates and Task"

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}