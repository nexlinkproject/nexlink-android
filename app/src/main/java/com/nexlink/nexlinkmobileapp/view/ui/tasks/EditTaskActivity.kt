package com.nexlink.nexlinkmobileapp.view.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.ListTaskUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityEditTaskBinding
import com.nexlink.nexlinkmobileapp.view.adapter.TaskUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.users.SearchUserActivity
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
import com.nexlink.nexlinkmobileapp.view.utils.formatDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding
    private lateinit var taskUsersAdapter: TaskUsersAdapter

    private val tasksViewModel by viewModels<TasksViewModel> {
        TasksModelFactory.getInstance(this)
    }

    private val usersViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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

        // Set up button listeners
        binding.btnAddUser.setOnClickListener {
            val searchUserIntent = Intent(this, SearchUserActivity::class.java).apply {
                putExtra(SearchUserActivity.EXTRA_TASK_ID, intent.getStringExtra(EXTRA_TASK_ID))
                putExtra(SearchUserActivity.EXTRA_PROJECT_ID, intent.getStringExtra(EXTRA_PROJECT_ID))
                putExtra(SearchUserActivity.EXTRA_USER_ADD_TYPE, "tasks")
            }
            startActivityForResult(searchUserIntent, REQUEST_ADD_TEAMMATES)
        }

        binding.btnSave.setOnClickListener {
            updateTask()
        }

        // Set up priority dropdown
        val priority = resources.getStringArray(R.array.priority_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priority)
        binding.selectPriority.setAdapter(adapter)

        // Set up recycler view
        taskUsersAdapter = TaskUsersAdapter(showRemoveButton = true)
        binding.rvUsers.adapter = taskUsersAdapter

        setUpDetailTask()
    }

    override fun onResume() {
        super.onResume()
        getDataUserTask()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_TEAMMATES && resultCode == RESULT_OK) {
            getDataUserTask()
        }
    }

    private fun setUpDetailTask() {
        val taskName = intent.getStringExtra(EXTRA_TASK_NAME)
        val taskDescription = intent.getStringExtra(EXTRA_TASK_DESCRIPTION)
        val taskStatus = intent.getStringExtra(EXTRA_TASK_STATUS)
        val taskStartDate = intent.getStringExtra(EXTRA_TASK_START_DATE)
        val taskEndDate = intent.getStringExtra(EXTRA_TASK_END_DATE)
        val taskPriority = intent.getStringExtra(EXTRA_TASK_PRIORITY)

        when(taskStatus) {
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

        binding.tfTaskName.setText(taskName)
        binding.tvStatus.text = taskStatus
        binding.tfTaskDescription.setText(taskDescription)
        binding.tfStartDate.setText(formatDatePicker(taskStartDate.toString()))
        binding.tfEndDate.setText(formatDatePicker(taskEndDate.toString()))
        binding.selectPriority.setText(taskPriority)
    }

    private fun updateTask() {
        val name = binding.tfTaskName.text.toString()
        val description = binding.tfTaskDescription.text.toString()
        val status = intent.getStringExtra(EXTRA_TASK_STATUS)
        val startDate = binding.tfStartDate.text.toString()
        val endDate = binding.tfEndDate.text.toString()
        val priority = binding.selectPriority.text.toString()

        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)

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

        // Konfirmasi sebelum mengubah task
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                saveUpdateTask(taskId.toString(), name, description, status.toString(), startDate, endDate, priority, projectId.toString())
            },
            title = "Confirm Update",
            message = "Are you sure the data is correct and you want to update the task?",
            icons = "info"
        )
    }

    private fun saveUpdateTask(taskId: String, name: String, description: String, status: String, startDate: String, endDate: String, priority: String, projectId: String){
        tasksViewModel.updateTask(taskId, name, description, status, startDate, endDate, priority, projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    val task = result.data.data?.updatedTask
                    if (task != null) {
                        alertInfoDialogWithEvent(
                            context = this,
                            layoutInflater = layoutInflater,
                            onOkClicked = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            title = "Update Task",
                            message = "Task updated successfully!",
                            icons = "success"
                        )

                        Log.i("EditTaskActivity", "Task updated successfully, task: ${result.data.data?.updatedTask}")
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Error",
                        message = "Failed to update task",
                        icons = "error"
                    )
                    Log.e("EditTaskActivity", "Failed to update task: ${result.error}")
                }
            }
        }
    }

    private fun getDataUserTask() {
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
                        binding.rvUsers.visibility = View.VISIBLE
                        binding.tvNoUsers.visibility = View.GONE

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

                        taskUsersAdapter.setOnRemoveButtonClickCallback(object : TaskUsersAdapter.OnRemoveButtonClickCallback {
                            override fun onRemoveButtonClicked(data: ListTaskUsersItem) {
                                alertConfirmDialog(
                                    context = this@EditTaskActivity,
                                    layoutInflater = layoutInflater,
                                    onYesClicked = {
                                        removeUserFromTask(data)
                                    },
                                    title = "Remove User",
                                    message = "Are you sure you want to remove ${data.fullName} from the task?",
                                    icons = "warning"
                                )
                            }
                        })
                    } else {
                        binding.rvUsers.visibility = View.GONE
                        binding.tvNoUsers.visibility = View.VISIBLE
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    binding.rvUsers.visibility = View.GONE
                    binding.tvNoUsers.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun removeUserFromTask(user: ListTaskUsersItem) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val userId = user.id

        if(taskId != null && userId != null) {
            usersViewModel.removeUserFromTask(userId, taskId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> { showLoading(true) }
                    is ResultState.Success -> {
                        showLoading(false)
                        val message = result.data.message
                        alertInfoDialogWithEvent(
                            context = this,
                            layoutInflater = layoutInflater,
                            onOkClicked = {
                                getDataUserTask()
                            },
                            title = "Remove User",
                            message = "User ${user.fullName} removed from task successfully!",
                            icons = "success"
                        )

                        Log.i("EditTaskActivity", "User removed successfully: $message")
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        val message = result.error
                        alertInfoDialog(
                            context = this,
                            layoutInflater = layoutInflater,
                            title = "Error",
                            message = "Failed to remove user ${user.fullName}",
                            icons = "error"
                        )
                        Log.e("EditTaskActivity", "Failed to remove user: $message")
                    }
                }
            }
        }else{
            alertInfoDialog(
                context = this,
                layoutInflater = layoutInflater,
                title = "Error",
                message = "Failed to remove user ${user.fullName}",
                icons = "error"
            )
        }
    }

    private fun showSelectedUser(user: ListTaskUsersItem, viewHolder: RecyclerView.ViewHolder) {
        // Todo: Show user detail
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
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_NAME = "task_name"
        const val EXTRA_TASK_DESCRIPTION = "task_description"
        const val EXTRA_TASK_STATUS = "task_status"
        const val EXTRA_TASK_START_DATE = "task_start_date"
        const val EXTRA_TASK_END_DATE = "task_end_date"
        const val EXTRA_TASK_PRIORITY = "task_priority"
        const val EXTRA_PROJECT_ID = "project_id"
        const val REQUEST_ADD_TEAMMATES = 1
    }
}