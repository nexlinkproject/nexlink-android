package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

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
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectTasksItem
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityDetailProjectBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectTasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.ui.tasks.DetailTaskActivity
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
import com.nexlink.nexlinkmobileapp.view.utils.formatDate


class DetailProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProjectBinding

    private lateinit var projectUsersAdapter: ProjectUsersAdapter
    private lateinit var projectTasksAdapter: ProjectTasksAdapter

    private var isDataProjectLoading = false
    private var isTeammatesLoading = false
    private var isTasksLoading = false

    private val projectsViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Set up buttons click listeners
        binding.btnEditProject.setOnClickListener {
            val editProjectIntent = Intent(this@DetailProjectActivity, EditProjectActivity::class.java).apply {
                putExtra(EditProjectActivity.EXTRA_PROJECT_ID, intent.getStringExtra(EXTRA_PROJECT_ID))
                putExtra(EditProjectActivity.EXTRA_PROJECT_NAME, intent.getStringExtra(EXTRA_PROJECT_NAME))
                putExtra(EditProjectActivity.EXTRA_PROJECT_DESCRIPTION, intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION))
                putExtra(EditProjectActivity.EXTRA_PROJECT_STATUS, intent.getStringExtra(EXTRA_PROJECT_STATUS))
                putExtra(EditProjectActivity.EXTRA_PROJECT_START_DATE, intent.getStringExtra(EXTRA_PROJECT_START_DATE))
                putExtra(EditProjectActivity.EXTRA_PROJECT_END_DATE, intent.getStringExtra(EXTRA_PROJECT_END_DATE))
            }
            startActivityForResult(editProjectIntent, REQUEST_UPDATE_PROJECT)
        }

        binding.btnDeleteProject.setOnClickListener {
            showDeleteConfirmationDialog()
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
        if (requestCode == REQUEST_UPDATE_PROJECT && resultCode == RESULT_OK) {
            // Refresh data detail project
            val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
            if (projectId != null) {
                isDataProjectLoading = true
                showLoading(true)
                loadDataProjectById(projectId)
            }
        }
    }

    private fun setupRecyclerViews() {
        binding.rvTeammates.layoutManager = LinearLayoutManager(this)
        projectUsersAdapter = ProjectUsersAdapter(showRemoveButton = false)
        binding.rvTeammates.adapter = projectUsersAdapter

        binding.rvTask.layoutManager = LinearLayoutManager(this)
        projectTasksAdapter = ProjectTasksAdapter()
        binding.rvTask.adapter = projectTasksAdapter
    }

    private fun setupDataDetailProject() {
        val projectName = intent.getStringExtra(EXTRA_PROJECT_NAME)
        val projectDescription = intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION)
        var projectStatus = intent.getStringExtra(EXTRA_PROJECT_STATUS)

        val projectStartDate =
            formatDate(intent.getStringExtra(EXTRA_PROJECT_START_DATE).toString())
        val projectEndDate = formatDate(intent.getStringExtra(EXTRA_PROJECT_END_DATE).toString())
        val projectDate = "$projectStartDate - $projectEndDate"

        when(projectStatus) {
            "in-progress" -> {
                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.warning_main))
                projectStatus = "In Progress"
            }
            "completed" -> {
                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.success_main))
                projectStatus = "Completed"
            }
            "cancelled" -> {
                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.danger_main))
                projectStatus = "Cancelled"
            }
            else -> {
                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.primary_main))
                projectStatus = "Active"
            }
        }

        binding.tvProjectName.text = projectName
        binding.tvDescription.text = projectDescription
        binding.tvProjectStatus.text = projectStatus
        binding.tvDeadline.text = projectDate
    }

    private fun getDataTeammatesAndTasks() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            isTeammatesLoading = true
            isTasksLoading = true
            showLoading(true)

            loadTeammates(projectId)
            loadTasks(projectId)
        }
    }

    private fun loadDataProjectById(projectId: String) {
        projectsViewModel.getProjectById(projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Success -> {
                    val project = result.data.data?.project
                    if (project != null) {
                        binding.tvProjectName.text = project.name
                        binding.tvDescription.text = project.description

                        when(project.status) {
                            "in-progress" -> {
                                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.warning_main))
                                binding.tvProjectStatus.text = "In Progress"
                            }
                            "completed" -> {
                                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.success_main))
                                binding.tvProjectStatus.text = "Completed"
                            }
                            "cancelled" -> {
                                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.danger_main))
                                binding.tvProjectStatus.text = "Cancelled"
                            }
                            else -> {
                                binding.tvProjectStatus.setTextColor(resources.getColor(R.color.primary_main))
                                binding.tvProjectStatus.text = "Active"
                            }
                        }

                        val projectStartDate = formatDate(project.startDate.toString())
                        val projectEndDate = formatDate(project.endDate.toString())
                        binding.tvDeadline.text = "$projectStartDate - $projectEndDate"

                        // Update Extra data
                        intent.putExtra(EXTRA_PROJECT_NAME, project.name)
                        intent.putExtra(EXTRA_PROJECT_DESCRIPTION, project.description)
                        intent.putExtra(EXTRA_PROJECT_STATUS, project.status)
                        intent.putExtra(EXTRA_PROJECT_START_DATE, project.startDate)
                        intent.putExtra(EXTRA_PROJECT_END_DATE, project.endDate)
                    } else {
                        Log.e("DetailProjectActivity", "Failed to load project: Project is null")
                    }

                    isDataProjectLoading = false
                    checkIfDataLoaded()
                }

                is ResultState.Error -> {
                    Log.e("DetailProjectActivity", "Failed to load project: ${result.error}")
                    isDataProjectLoading = false
                    checkIfDataLoaded()
                }
            }
        }
    }

    private fun loadTeammates(projectId: String) {
        projectsViewModel.getProjectUsers(projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {}
                is ResultState.Success -> {
                    val users = result.data.data?.project?.users

                    if (users.isNullOrEmpty()) {
                        binding.rvTeammates.visibility = View.GONE
                        binding.tvNoTeammates.visibility = View.VISIBLE
                    } else {
                        binding.rvTeammates.visibility = View.VISIBLE
                        binding.tvNoTeammates.visibility = View.GONE

                        projectUsersAdapter.submitList(users)

                        // Set on item click listener for rv teammates
                        projectUsersAdapter.setOnItemClickCallback(object : ProjectUsersAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: ListProjectUsersItem) {
                                showSelectedUser(
                                    data
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
                    Log.e("DetailProjectActivity", "Failed to load teammates: ${result.error}")
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
                    val tasksResponse = result.data
                    val tasks = tasksResponse.data?.tasks

                    // Cetak response lengkap untuk debugging
                    Log.i("DetailProjectActivity", "Tasks Response: $tasksResponse")

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
                                Log.i("DetailProjectActivity", "Task: $data")
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

                    Log.e("DetailProjectActivity", "Failed to load tasks: ${result.error}")
                    isTasksLoading = false
                    checkIfDataLoaded()
                }
            }
        }
    }

    private fun showSelectedUser(user: ListProjectUsersItem) {
        // TODO: Show user detail
    }

    private fun showSelectedTask(task: ListProjectTasksItem, viewHolder: RecyclerView.ViewHolder) {
        val detailTaskIntent = Intent(this, DetailTaskActivity::class.java).apply {
            putExtra(DetailTaskActivity.EXTRA_TASK_ID, task.id)
            putExtra(DetailTaskActivity.EXTRA_TASK_NAME, task.name)
            putExtra(DetailTaskActivity.EXTRA_TASK_DESCRIPTION, task.description)
            putExtra(DetailTaskActivity.EXTRA_TASK_STATUS, task.status)
            putExtra(DetailTaskActivity.EXTRA_TASK_START_DATE, task.startDate)
            putExtra(DetailTaskActivity.EXTRA_TASK_END_DATE, task.endDate)
            putExtra(DetailTaskActivity.EXTRA_TASK_PRIORITY, task.priority)
            putExtra(DetailTaskActivity.EXTRA_PROJECT_ID, task.projectId)
        }
        startActivity(detailTaskIntent)
    }

    private fun checkIfDataLoaded() {
        if (!isTeammatesLoading && !isTasksLoading && !isDataProjectLoading) {
            showLoading(false)
        }
    }

    private fun showDeleteConfirmationDialog() {
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                deleteProject()
            },
            title = "Delete Project",
            message = "Are you sure you want to delete this project?",
            icons = "error"
        )
    }

    private fun deleteProject() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID).orEmpty()
        projectsViewModel.deleteProject(projectId).observe(this) { result ->
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
                        title = "Delete Project",
                        message = "Project deleted successfully",
                        icons = "success"
                    )
                    Log.i("DetailProjectActivity", "Project deleted successfully, ${result.data.message}")
                }

                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Error",
                        message = "Failed to delete project",
                        icons = "error"
                    )
                    Log.e("DetailProjectActivity", "Failed to delete project: ${result.error}")
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        const val EXTRA_PROJECT_ID = "extra_project_id"
        const val EXTRA_PROJECT_NAME = "extra_project_name"
        const val EXTRA_PROJECT_DESCRIPTION = "extra_project_description"
        const val EXTRA_PROJECT_STATUS = "extra_project_status"
        const val EXTRA_PROJECT_START_DATE = "extra_project_start_date"
        const val EXTRA_PROJECT_END_DATE = "extra_project_end_date"
        const val REQUEST_UPDATE_PROJECT = 1
    }
}