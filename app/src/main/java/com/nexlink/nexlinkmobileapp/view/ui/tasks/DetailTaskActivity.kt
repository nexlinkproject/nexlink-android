package com.nexlink.nexlinkmobileapp.view.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.ListTaskUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityDetailTaskBinding
import com.nexlink.nexlinkmobileapp.view.adapter.TaskUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTaskBinding
    private lateinit var taskUsersAdapter: TaskUsersAdapter

    private val tasksViewModel by viewModels<TasksViewModel> {
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

        // Set up button listeners
        binding.btnEditTask.setOnClickListener {
            val editTaskIntent = Intent(this@DetailTaskActivity, EditTaskActivity::class.java).apply {
                putExtra(EditTaskActivity.EXTRA_TASK_ID, intent.getStringExtra(EXTRA_TASK_ID))
                putExtra(EditTaskActivity.EXTRA_TASK_NAME, intent.getStringExtra(EXTRA_TASK_NAME))
                putExtra(EditTaskActivity.EXTRA_TASK_DESCRIPTION, intent.getStringExtra(EXTRA_TASK_DESCRIPTION))
                putExtra(EditTaskActivity.EXTRA_TASK_STATUS, intent.getStringExtra(EXTRA_TASK_STATUS))
                putExtra(EditTaskActivity.EXTRA_TASK_START_DATE, intent.getStringExtra(EXTRA_TASK_START_DATE))
                putExtra(EditTaskActivity.EXTRA_TASK_END_DATE, intent.getStringExtra(EXTRA_TASK_END_DATE))
                putExtra(EditTaskActivity.EXTRA_TASK_PRIORITY, intent.getStringExtra(EXTRA_TASK_PRIORITY))
                putExtra(EditTaskActivity.EXTRA_PROJECT_ID, intent.getStringExtra(EXTRA_PROJECT_ID))
            }
            startActivityForResult(editTaskIntent, REQUEST_UPDATE_TASK)
        }

        binding.btnDeleteTask.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnCancelled.setOnClickListener {
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    changeTaskStatus("cancelled")
                },
                title = "Change Status Task",
                message = "Are you sure want to change status task to 'Cancelled'?",
                icons = "info"
            )
        }

        binding.btnPending.setOnClickListener {
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    changeTaskStatus("pending")
                },
                title = "Change Status Task",
                message = "Are you sure want to change status task to 'Pending'?",
                icons = "info"
            )
        }

        binding.btnDone.setOnClickListener {
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    changeTaskStatus("done")
                },
                title = "Change Status Task",
                message = "Are you sure want to change status task to 'Done'?",
                icons = "info"
            )
        }

        // Set up recycler view
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        taskUsersAdapter = TaskUsersAdapter(false)
        binding.rvUsers.adapter = taskUsersAdapter

        setupDataDetailTask()
    }

    override fun onResume() {
        super.onResume()
        loadUserTask()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UPDATE_TASK && resultCode == RESULT_OK) {
            getDataTaskById()
        }
    }

    private fun setupDataDetailTask() {
        val taskName = intent.getStringExtra(EXTRA_TASK_NAME)
        val taskStatus = intent.getStringExtra(EXTRA_TASK_STATUS)
        val taskDescription = intent.getStringExtra(EXTRA_TASK_DESCRIPTION)
        val taskDeadline = intent.getStringExtra(EXTRA_TASK_END_DATE)
        val taskPriority = intent.getStringExtra(EXTRA_TASK_PRIORITY)

        changeStatusTask(taskStatus)

        binding.tvTaskName.text = taskName
        binding.tvStatus.text = taskStatus
        binding.tvDescription.text = taskDescription
        binding.tvDeadline.text = formatDate(taskDeadline.toString())
        binding.tvPriority.text = taskPriority
    }

    private fun getDataTaskById() {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        tasksViewModel.getTaskById(taskId.toString()).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val task = result.data.data?.task
                    if (task != null) {
                        binding.tvTaskName.text = task.name
                        binding.tvStatus.text = task.status
                        binding.tvDescription.text = task.description
                        binding.tvDeadline.text = formatDate(task.endDate.toString())
                        binding.tvPriority.text = task.priority

                        changeStatusTask(task.status)

                        // Update intent extra
                        intent.putExtra(EXTRA_TASK_NAME, task.name)
                        intent.putExtra(EXTRA_TASK_STATUS, task.status)
                        intent.putExtra(EXTRA_TASK_DESCRIPTION, task.description)
                        intent.putExtra(EXTRA_TASK_END_DATE, task.endDate)
                        intent.putExtra(EXTRA_TASK_PRIORITY, task.priority)
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("DetailTaskActivity", "getDataTaskById error: ${result.error}")
                }
            }
        }
    }

    private fun changeStatusTask(status: String?){
        when(status) {
            "pending" -> {
                binding.tvStatus.setTextColor(resources.getColor(R.color.warning_main))
                binding.tvStatus.text = "Pending"
            }
            "done" -> {
                binding.tvStatus.setTextColor(resources.getColor(R.color.success_main))
                binding.tvStatus.text = "Done"
            }
            "cancelled" -> {
                binding.tvStatus.setTextColor(resources.getColor(R.color.danger_main))
                binding.tvStatus.text = "Cancelled"
            }
            else -> {
                binding.tvStatus.setTextColor(resources.getColor(R.color.primary_main))
                binding.tvStatus.text = "In Progress"
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                deleteTask()
            },
            title = "Delete Task",
            message = "Are you sure you want to delete this task?",
            icons = "error"
        )
    }

    private fun loadUserTask() {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        tasksViewModel.getTaskUser(taskId.toString()).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val users = result.data.data?.task?.users
                    if (users != null) {
                        showNoUsers(false)
                        taskUsersAdapter.submitList(users)
                        // Set on item click listener for rv teammates
                        taskUsersAdapter.setOnItemClickCallback(object : TaskUsersAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ListTaskUsersItem) {
                                showSelectedUser(
                                    data,
                                    binding.rvUsers.findViewHolderForAdapterPosition(
                                        taskUsersAdapter.currentList.indexOf(data)
                                    )!!
                                )
                            }
                        })
                    } else {
                        showNoUsers(true)
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showNoUsers(true)
                    Log.e("DetailTaskActivity", "loadUserTask error: ${result.error}")
                }
            }
        }
    }

    private fun deleteTask() {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        tasksViewModel.deleteTask(taskId.toString()).observe(this) { result ->
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
                            finish()
                        },
                        title = "Delete Task",
                        message = "Task deleted successfully",
                        icons = "success"
                    )
                    Log.i("DetailTaskActivity", "deleteTask with id: $taskId success")
                }
                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Error",
                        message = "Failed to delete task",
                        icons = "error"
                    )
                    Log.e("DetailTaskActivity", "deleteTask error: ${result.error}")
                }
            }
        }
    }

    private fun changeTaskStatus(status: String) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val name = intent.getStringExtra(EXTRA_TASK_NAME)
        val description = intent.getStringExtra(EXTRA_TASK_DESCRIPTION)
        val startDate = intent.getStringExtra(EXTRA_TASK_START_DATE)
        val endDate = intent.getStringExtra(EXTRA_TASK_END_DATE)
        val priority = intent.getStringExtra(EXTRA_TASK_PRIORITY)
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)

        if(taskId != null && name != null && description != null && startDate != null && endDate != null && priority != null && projectId != null) {
            tasksViewModel.updateTask(
                taskId,
                name,
                description,
                status,
                startDate,
                endDate,
                priority,
                projectId
            ).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.Success -> {
                        showLoading(false)
                        alertInfoDialogWithEvent(
                            context = this,
                            layoutInflater = layoutInflater,
                            onOkClicked = {
                                finish()
                            },
                            title = "Update Status Task",
                            message = "Status Task updated successfully",
                            icons = "success"
                        )
                        Log.i("DetailTaskActivity", "changeTaskStatus success, taskId : $taskId, status : $status")
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        alertInfoDialog(
                            context = this,
                            layoutInflater = layoutInflater,
                            title = "Error",
                            message = "Failed to change status task",
                            icons = "error"
                        )
                        Log.e("DetailTaskActivity", "changeTaskStatus error: ${result.error}")
                    }
                }
            }
        }else{
            alertInfoDialog(
                context = this,
                layoutInflater = layoutInflater,
                title = "Error",
                message = "Failed to change status task",
                icons = "error"
            )
        }
    }

    private fun showSelectedUser(user: ListTaskUsersItem, viewHolder: RecyclerView.ViewHolder) {
        // Todo: Show user detail
    }

    private fun showNoUsers(isEmpty: Boolean) {
        if(isEmpty) {
            binding.rvUsers.visibility = View.GONE
            binding.tvNoUsers.visibility = View.VISIBLE
        } else {
            binding.rvUsers.visibility = View.VISIBLE
            binding.tvNoUsers.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_NAME = "task_name"
        const val EXTRA_TASK_DESCRIPTION = "task_description"
        const val EXTRA_TASK_STATUS = "task_status"
        const val EXTRA_TASK_START_DATE = "task_start_date"
        const val EXTRA_TASK_END_DATE = "null"
        const val EXTRA_TASK_PRIORITY = "task_priority"
        const val EXTRA_PROJECT_ID = "project_id"
        const val REQUEST_UPDATE_TASK = 1
    }
}