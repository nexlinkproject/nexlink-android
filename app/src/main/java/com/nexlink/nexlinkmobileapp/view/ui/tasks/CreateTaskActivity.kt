package com.nexlink.nexlinkmobileapp.view.ui.tasks

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityCreateTaskBinding
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTaskBinding

    private val tasksViewModel by viewModels<TasksViewModel> {
        TasksModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnSave.setOnClickListener {
            createTask()
        }

        // Set up date pickers
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

        // Set up priority dropdown
        val priorities = resources.getStringArray(R.array.priority_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        binding.selectPriority.setAdapter(adapter)
    }

    private fun createTask() {
        val name = binding.tfTaskName.text.toString()
        val description = binding.tfTaskDescription.text.toString()
        val status = "in-progress"
        val startDate = binding.tfStartDate.text.toString()
        val endDate = binding.tfEndDate.text.toString()
        val priority = binding.selectPriority.text.toString()
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID) ?: ""

        if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || priority.isEmpty()) {
            showToast("Please fill in all fields")
            return
        }

        tasksViewModel.createTask(name, description, status, startDate, endDate, priority, projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle("Save Task")
                        setMessage("Task saved successfully!")
                        setPositiveButton("Next") { _, _ ->
                            val project = result.data.data?.task
                            if (project != null) {
                                setResult(RESULT_OK)
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
                        setMessage("Failed to save task: ${result.error}")
                        setPositiveButton("OK", null)
                        create()
                        show()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_PROJECT_ID = "EXTRA_PROJECT_ID"
    }
}