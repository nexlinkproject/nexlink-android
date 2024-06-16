package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityCreateProjectBinding
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProjectBinding
    private val projectsViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        binding.tfStartDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker { date ->
                    setText(dateFormat.format(date))
                }
            }
        }

        binding.tfEndDate.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker { date ->
                    setText(dateFormat.format(date))
                }
            }
        }

        binding.btnSaveProject.setOnClickListener {
            createProject()
        }
    }

    private fun createProject() {
        val name = binding.tfProjectName.text.toString()
        val description = binding.tfDescription.text.toString()
        val status = "active"
        val startDate = binding.tfStartDate.text.toString()
        val endDate = binding.tfEndDate.text.toString()
        val deadline = binding.tfEndDate.text.toString() // ini masih perlu di ubah

        if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("Please fill all fields")
                setPositiveButton("OK", null)
                create()
                show()
            }
            return
        }

        projectsViewModel.createProject(name, description, status, startDate, endDate, deadline).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)

                is ResultState.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Save Project")
                        setMessage("Project $name has been saved. Let's add teammates and tasks.")
                        setPositiveButton("Next") { _, _ ->
                            val project = result.data.data?.project
                            if (project != null) {
                                val detailProjectIntent = Intent(this@CreateProjectActivity, AddTeammatesAndTaskActivity::class.java).apply {
                                    putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_ID, project.id)
                                    putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_NAME, project.name)
                                    putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_DESCRIPTION, project.description)
                                    putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_START_DATE, project.startDate)
                                    putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_END_DATE, project.endDate)
                                }
                                startActivity(detailProjectIntent)
                                finish()
                            }
                        }
                        create()
                        show()
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Error")
                        setMessage("Failed to save project: ${result.error}")
                        setPositiveButton("OK", null)
                        create()
                        show()
                    }
                }
            }
        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.txt_select_date))
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = Date(it)
            onDateSelected(selectedDate)
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}