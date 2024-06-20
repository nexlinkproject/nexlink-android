package com.nexlink.nexlinkmobileapp.view.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityCreateTaskBinding
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
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

        // Set up button listeners
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
            alertInfoDialog(
                context = this,
                layoutInflater = layoutInflater,
                title = "Invalid Input",
                message = "Please fill all fields",
                icons = "info"
            )
            return
        }

        // Konfirmasi sebelum menyimpan proyek
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                saveTask(name, description, status, startDate, endDate, priority, projectId)
            },
            title = "Confirm Save",
            message = "Are you sure the data is correct and you want to save the Task?",
            icons = "info"
        )
    }

    private fun saveTask(name: String, description: String, status: String, startDate: String, endDate: String, priority: String, projectId: String){
        tasksViewModel.createTask(name, description, status, startDate, endDate, priority, projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    alertInfoDialogWithEvent(
                        context = this,
                        layoutInflater = layoutInflater,
                        onOkClicked = {
                            val task = result.data.data?.task
                            if (task != null) {
                                val addTaskUserIntent = Intent(this@CreateTaskActivity, AddUserTaskActivity::class.java).apply {
                                    putExtra(AddUserTaskActivity.EXTRA_TASK_ID, task.id)
                                    putExtra(AddUserTaskActivity.EXTRA_TASK_NAME, task.name)
                                    putExtra(AddUserTaskActivity.EXTRA_TASK_DESCRIPTION, task.description)
                                    putExtra(AddUserTaskActivity.EXTRA_TASK_PRIORITY, task.priority)
                                    putExtra(AddUserTaskActivity.EXTRA_TASK_DEADLINE, task.endDate)
                                    putExtra(AddUserTaskActivity.EXTRA_PROJECT_ID, projectId)
                                }
                                startActivity(addTaskUserIntent)
                                finish()
                            }
                        },
                        title = "Save Task",
                        message = "Task $name has been saved. Let's add user to task.",
                        icons = "success"
                    )
                    Log.i("CreateTaskActivity", "create task success: ${result.data.data?.task}")
                }

                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Error",
                        message = "Failed to save task",
                        icons = "error"
                    )
                    Log.e("CreateTaskActivity", "create task error: ${result.error}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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