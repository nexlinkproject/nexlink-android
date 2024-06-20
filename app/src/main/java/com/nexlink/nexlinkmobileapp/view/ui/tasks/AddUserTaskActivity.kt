package com.nexlink.nexlinkmobileapp.view.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.tasks.ListTaskUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityAddUserTaskBinding
import com.nexlink.nexlinkmobileapp.view.adapter.TaskUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.users.SearchUserActivity
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class AddUserTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUserTaskBinding
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

        binding = ActivityAddUserTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up button listeners
        binding.btnAddUser.setOnClickListener {
            val searchUserIntent = Intent(this, SearchUserActivity::class.java).apply {
                putExtra(SearchUserActivity.EXTRA_TASK_ID, intent.getStringExtra(EXTRA_TASK_ID))
                putExtra(SearchUserActivity.EXTRA_PROJECT_ID, intent.getStringExtra(EXTRA_PROJECT_ID))
                putExtra(SearchUserActivity.EXTRA_USER_ADD_TYPE, "tasks")
            }
            startActivityForResult(
                searchUserIntent,
                REQUEST_ADD_TEAMMATES
            )
        }

        binding.btnSubmit.setOnClickListener {
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    setResult(RESULT_OK)
                    finish()
                },
                title = "Confirm Task",
                message = "Are you sure data user task is correct?",
                icons = "warning"
            )
        }

        // Set up recycler view
        taskUsersAdapter = TaskUsersAdapter(showRemoveButton = true)
        binding.rvUsers.adapter = taskUsersAdapter

        setUpDetailTask()
    }

    override fun onResume() {
        super.onResume()
        loadUserTask()
    }

    override fun onBackPressed() {
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                super.onBackPressed()
            },
            title = "Confirm Exit",
            message = "Are you sure you want to leave this page? Your changes might not be saved.",
            icons = "warning"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_TEAMMATES && resultCode == RESULT_OK) {
            loadUserTask()
        }
    }

    private fun setUpDetailTask() {
        val taskName = intent.getStringExtra(EXTRA_TASK_NAME)
        val taskDescription = intent.getStringExtra(EXTRA_TASK_DESCRIPTION)
        val taskPriority = intent.getStringExtra(EXTRA_TASK_PRIORITY)
        val taskDeadline = intent.getStringExtra(EXTRA_TASK_DEADLINE)

        binding.tvName.text = taskName
        binding.tvDescription.text = taskDescription
        binding.tvPriority.text = taskPriority
        binding.tvDeadline.text = formatDate(taskDeadline.toString())
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
                                    context = this@AddUserTaskActivity,
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

    private fun showSelectedUser(user: ListTaskUsersItem, viewHolder: RecyclerView.ViewHolder) {
        // TODO: Show user detail
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
                                loadUserTask()
                            },
                            title = "Remove User",
                            message = "User ${user.fullName} removed from task successfully!",
                            icons = "success"
                        )

                        Log.i("AddUserTaskActivity", "User removed successfully: $message")
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
                        Log.e("AddUserTaskActivity", "Failed to remove user: $message")
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_NAME = "extra_task_name"
        const val EXTRA_TASK_DESCRIPTION = "extra_task_description"
        const val EXTRA_TASK_PRIORITY = "extra_task_priority"
        const val EXTRA_TASK_DEADLINE = "extra_task_deadline"
        const val EXTRA_PROJECT_ID = "extra_project_id"
        const val REQUEST_ADD_TEAMMATES = 1
    }
}