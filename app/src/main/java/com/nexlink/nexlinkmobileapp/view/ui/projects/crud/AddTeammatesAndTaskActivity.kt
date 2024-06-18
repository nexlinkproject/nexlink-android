package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectTasksItem
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityAddTeammatesAndTaskBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectTasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.ui.tasks.CreateTaskActivity
import com.nexlink.nexlinkmobileapp.view.ui.tasks.DetailTaskActivity
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class AddTeammatesAndTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTeammatesAndTaskBinding

    private lateinit var projectUsersAdapter: ProjectUsersAdapter
    private lateinit var projectTasksAdapter: ProjectTasksAdapter

    private var isTeammatesLoading = false
    private var isTasksLoading = false

    private val projectsViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTeammatesAndTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Set up buttons click listeners
        binding.btnAddTeammate.setOnClickListener {
            println("Add teammate")
        }

        binding.btnAddTask.setOnClickListener {
            val detailProjectIntent = Intent(this@AddTeammatesAndTaskActivity, CreateTaskActivity::class.java).apply {
                putExtra(CreateTaskActivity.EXTRA_PROJECT_ID, intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_ID))
            }
//            startActivity(detailProjectIntent)
            startActivityForResult(detailProjectIntent, REQUEST_CREATE_TASK)
        }

        binding.btnGenerateMl.setOnClickListener {
            println("Generate ML")
        }

        binding.btnSubmit.setOnClickListener {
            println("Submit")
        }

        setupRecyclerViews()
        setupDataDetailProject()
    }

    override fun onResume() {
        super.onResume()
        getDataTeammatesAndTasks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CREATE_TASK && resultCode == RESULT_OK) {
            // Refresh data tasks setelah kembali dari CreateTaskActivity
            val projectId = intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_ID)
            if (projectId != null) {
                isTasksLoading = true
                showLoading(true)
                loadTasks(projectId)
            }
        }
    }

    private fun setupRecyclerViews() {
        projectUsersAdapter = ProjectUsersAdapter()
        binding.rvTeammates.adapter = projectUsersAdapter

        projectTasksAdapter = ProjectTasksAdapter()
        binding.rvTask.adapter = projectTasksAdapter
    }

    private fun setupDataDetailProject() {
        val projectName = intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_NAME)
        val projectDescription = intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_DESCRIPTION)

        val projectStartDate =
            formatDate(intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_START_DATE).toString())
        val projectEndDate = formatDate(intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_END_DATE).toString())
        val projectDate = "$projectStartDate - $projectEndDate"

        binding.tvProjectName.text = projectName
        binding.tvDescription.text = projectDescription
        binding.tvDeadline.text = projectDate
    }

    private fun getDataTeammatesAndTasks(){
        val projectId = intent.getStringExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_ID)
        if (projectId != null) {
            isTeammatesLoading = true
            isTasksLoading = true
            showLoading(true)

            loadTeammates(projectId)
            loadTasks(projectId)
        }
    }

    private fun loadTeammates(projectId: String) {
        projectsViewModel.getProjecUsers(projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Success -> {
                    val users = result.data.data

                    if (users == null) {
                        binding.rvTeammates.visibility = View.GONE
                        binding.tvNoTeammates.visibility = View.VISIBLE
                    } else {
                        binding.rvTeammates.visibility = View.VISIBLE
                        binding.tvNoTeammates.visibility = View.GONE

                        projectUsersAdapter.submitList(users.project?.users)

                        // Set on item click listener for rv teammates
                        projectUsersAdapter.setOnItemClickCallback(object : ProjectUsersAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ListProjectUsersItem) {
                                showSelectedUser(
                                    data,
                                    binding.rvTask.findViewHolderForAdapterPosition(
                                        projectUsersAdapter.currentList.indexOf(data)
                                    )!!
                                )
                            }
                        })
                    }

                    isTeammatesLoading = false
                    checkIfDataLoaded()
                }

                is ResultState.Error -> {
                    binding.rvTeammates.visibility = View.GONE
                    binding.tvNoTeammates.visibility = View.VISIBLE
//                    showToast(result.error)
                    Log.e("AddTeamAndTaskActivity", "Failed to load teammates: ${result.error}")
                    isTeammatesLoading = false
                    checkIfDataLoaded()
                }
            }
        }
    }

    private fun loadTasks(projectId: String) {
        projectsViewModel.getProjectTasks(projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Success -> {
                    val tasks = result.data.data?.tasks

                    if (tasks.isNullOrEmpty()) {
                        binding.rvTask.visibility = View.GONE
                        binding.tvNoTask.visibility = View.VISIBLE
                    } else {
                        binding.rvTask.visibility = View.VISIBLE
                        binding.tvNoTask.visibility = View.GONE

                        projectTasksAdapter.submitList(tasks)

                        // Set on item click listener for rv tasks
                        projectTasksAdapter.setOnItemClickCallback(object : ProjectTasksAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ListProjectTasksItem) {
                                showSelectedTask(
                                    data,
                                    binding.rvTask.findViewHolderForAdapterPosition(
                                        projectTasksAdapter.currentList.indexOf(data)
                                    )!!
                                )
                            }
                        })
                    }

                    isTasksLoading = false
                    checkIfDataLoaded()
                }

                is ResultState.Error -> {
                    binding.rvTask.visibility = View.GONE
                    binding.tvNoTask.visibility = View.VISIBLE
//                    showToast(result.error)
                    Log.e("AddTeamAndTaskActivity", "Failed to load tasks: ${result.error}")
                    isTasksLoading = false
                    checkIfDataLoaded()
                }
            }
        }
    }

    private fun showSelectedUser(user: ListProjectUsersItem, viewHolder: RecyclerView.ViewHolder) {
        showToast("User ${user.fullName} clicked")
    }

    private fun showSelectedTask(task: ListProjectTasksItem, viewHolder: RecyclerView.ViewHolder) {
        val detailProjectIntent = Intent(this, DetailTaskActivity::class.java).apply {
            putExtra(DetailTaskActivity.EXTRA_TASK_ID, task.id)
            putExtra(DetailTaskActivity.EXTRA_TASK_NAME, task.name)
            putExtra(DetailTaskActivity.EXTRA_TASK_DESCRIPTION, task.description)
            putExtra(DetailTaskActivity.EXTRA_TASK_DEADLINE, task.endDate)
            putExtra(DetailTaskActivity.EXTRA_TASK_PRIORITY, "Null")
            putExtra(DetailTaskActivity.EXTRA_PROJECT_ID, task.projectId)
        }
        startActivity(detailProjectIntent)
    }

    private fun checkIfDataLoaded() {
        if (!isTeammatesLoading && !isTasksLoading) {
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_PROJECT_ID = "extra_project_id"
        const val EXTRA_PROJECT_NAME = "extra_project_name"
        const val EXTRA_PROJECT_DESCRIPTION = "extra_project_description"
        const val EXTRA_PROJECT_START_DATE = "extra_project_start_date"
        const val EXTRA_PROJECT_END_DATE = "extra_project_end_date"
        const val REQUEST_CREATE_TASK = 1
    }
}