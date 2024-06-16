package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityAddTeammatesAndTaskBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectTasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class AddTeammatesAndTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTeammatesAndTaskBinding

    private lateinit var projectUsersAdapter: ProjectUsersAdapter
    private lateinit var tasksAdapter: ProjectTasksAdapter

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
            println("Add task")
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

    private fun setupRecyclerViews() {
        projectUsersAdapter = ProjectUsersAdapter()
        binding.rvTeammates.adapter = projectUsersAdapter

        tasksAdapter = ProjectTasksAdapter()
        binding.rvTask.adapter = tasksAdapter
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
                    val users = result.data.data?.project?.users

                    if (users.isNullOrEmpty()) {
                        binding.rvTeammates.visibility = View.GONE
                        binding.tvNoTeammates.visibility = View.VISIBLE
                    } else {
                        binding.tvNoTeammates.visibility = View.GONE
                        projectUsersAdapter.submitList(users)
                    }

                    isTeammatesLoading = false
                    checkIfDataLoaded()
                }

                is ResultState.Error -> {
                    binding.tvNoTask.visibility = View.VISIBLE
                    showToast(result.error)
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
                        binding.tvNoTask.visibility = View.GONE
                        tasksAdapter.submitList(tasks)
                    }

                    isTasksLoading = false
                    checkIfDataLoaded()
                }

                is ResultState.Error -> {
                    binding.tvNoTask.visibility = View.VISIBLE
                    showToast(result.error)
                    isTasksLoading = false
                    checkIfDataLoaded()
                }
            }
        }
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
    }
}