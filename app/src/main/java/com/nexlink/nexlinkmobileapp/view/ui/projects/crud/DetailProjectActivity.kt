package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.databinding.ActivityDetailProjectBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectTasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.TasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.UsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.utils.formatDate

class DetailProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProjectBinding

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

        binding = ActivityDetailProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Set up buttons click listeners
        binding.btnEditProject.setOnClickListener {
            val detailProjectIntent = Intent(this@DetailProjectActivity, AddTeammatesAndTaskActivity::class.java).apply {
                putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_ID, intent.getStringExtra(EXTRA_PROJECT_ID))
                putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_NAME, intent.getStringExtra(EXTRA_PROJECT_NAME))
                putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_DESCRIPTION, intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION))
                putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_START_DATE, intent.getStringExtra(EXTRA_PROJECT_START_DATE))
                putExtra(AddTeammatesAndTaskActivity.EXTRA_PROJECT_END_DATE, intent.getStringExtra(EXTRA_PROJECT_END_DATE))
            }
            startActivity(detailProjectIntent)
        }

        binding.btnDeleteProject.setOnClickListener {
            showDeleteConfirmationDialog()
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
        val projectName = intent.getStringExtra(EXTRA_PROJECT_NAME)
        val projectDescription = intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION)

        val projectStartDate =
            formatDate(intent.getStringExtra(EXTRA_PROJECT_START_DATE).toString())
        val projectEndDate = formatDate(intent.getStringExtra(EXTRA_PROJECT_END_DATE).toString())
        val projectDate = "$projectStartDate - $projectEndDate"

        binding.tvProjectName.text = projectName
        binding.tvDescription.text = projectDescription
        binding.tvDeadline.text = projectDate

        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
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

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Delete Project")
            setMessage("Are you sure you want to delete this project?")
            setPositiveButton("Yes") { _, _ ->
                deleteProject()
            }
            setNegativeButton("No", null)
            create()
            show()
        }
    }

    private fun deleteProject() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID).orEmpty()
        projectsViewModel.deleteProject(projectId).observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    showToast(result.data.message.toString())
                    finish()
                }

                is ResultState.Error -> {
                    showLoading(false)
                    showToast("Failed to delete project: ${result.error}")
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