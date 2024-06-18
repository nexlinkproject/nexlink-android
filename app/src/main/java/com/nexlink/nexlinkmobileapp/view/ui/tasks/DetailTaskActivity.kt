package com.nexlink.nexlinkmobileapp.view.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityDetailTaskBinding
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTaskBinding
    private var isTaskLoading = false

    private val taskViewModel by viewModels<TasksViewModel> {
        TasksModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        setupDataDetailTask()
    }

//    override fun onResume() {
//        super.onResume()
////        getData()
//    }

    private fun setupDataDetailTask() {
        val taskName = intent.getStringExtra(EXTRA_TASK_NAME)
        val taskDescription = intent.getStringExtra(EXTRA_TASK_DESCRIPTION)
        val taskDeadline = intent.getStringExtra(EXTRA_TASK_DEADLINE)
        val taskPriority = intent.getStringExtra(EXTRA_TASK_PRIORITY)

        binding.tvTaskName.text = taskName
        binding.tvDescription.text = taskDescription
        binding.tvDeadline.text = formatDate(taskDeadline.toString())
        binding.tvPriority.text = taskPriority
    }

//    private fun getData(){
//        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
//        if (taskId != null) {
//            isTaskLoading = true
//            showLoading(true)
//
//            loadTaskById(taskId)
//        }
//    }

//    private fun loadTaskById(projectId: String) {
//        taskViewModel.getTaskById(projectId).observe(this) { result ->
//            when (result) {
//                is ResultState.Loading -> {}
//                is ResultState.Success -> {
//                    val task = result.data.data?.task
//                    if (task != null) {
//                        binding.tvTaskName.text = task.name
//                        binding.tvDescription.text = task.description
//                        binding.tvDeadline.text = formatDate(task.endDate.toString())
//                        binding.tvPriority.text = "Null"
//                    }else{
//                        Log.e("DetailTaskActivity", "Failed to load task: Task is null")
//                    }
//
//                    isTaskLoading = false
//                    checkIfDataLoaded()
//                }
//
//                is ResultState.Error -> {
//                    Log.e("AddTeamAndTask", "Failed to load tasks: ${result.error}")
//                    isTaskLoading = false
//                    checkIfDataLoaded()
//                }
//            }
//        }
//    }

//    private fun checkIfDataLoaded() {
//        if (!isTaskLoading) {
//            showLoading(false)
//        }
//    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_NAME = "task_name"
        const val EXTRA_TASK_DESCRIPTION = "task_description"
        const val EXTRA_TASK_DEADLINE = "task_deadline"
        const val EXTRA_TASK_PRIORITY = "task_priority"
        const val EXTRA_PROJECT_ID = "project_id"

    }
}