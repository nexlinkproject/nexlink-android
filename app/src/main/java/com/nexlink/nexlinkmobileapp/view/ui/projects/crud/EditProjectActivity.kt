package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectTasksItem
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityEditProjectBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectTasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.ui.tasks.CreateTaskActivity
import com.nexlink.nexlinkmobileapp.view.ui.tasks.DetailTaskActivity
import com.nexlink.nexlinkmobileapp.view.utils.formatDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProjectBinding

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

        binding = ActivityEditProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
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

        // Set up button listeners
        binding.btnAddTask.setOnClickListener {
            val detailProjectIntent = Intent(this, CreateTaskActivity::class.java).apply {
                putExtra(
                    CreateTaskActivity.EXTRA_PROJECT_ID,
                    intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_ID)
                )
            }
            startActivityForResult(
                detailProjectIntent,
                EditProjectActivity.REQUEST_CREATE_TASK
            )
        }

        binding.btnSaveProject.setOnClickListener {
            updateProject()
        }

        // Set up status dropdown
        val status = resources.getStringArray(R.array.status_array)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, status)
        binding.selectStatus.setAdapter(adapter)

        setupRecyclerViews()
        setupDataDetailProject()
    }

    override fun onResume() {
        super.onResume()
        getDataTeammatesAndTasks()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EditProjectActivity.REQUEST_CREATE_TASK && resultCode == RESULT_OK) {
            // Refresh data tasks setelah kembali dari CreateTaskActivity
            val projectId = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_ID)
            if (projectId != null) {
                isTasksLoading = true
                showLoading(true)
                loadTasks(projectId)
            }
        }
    }

    private fun updateProject() {
        val projectId = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_ID)
        val name = binding.tfProjectName.text.toString()
        val description = binding.tfDescription.text.toString()
        val startDate = binding.tfStartDate.text.toString()
        val endDate = binding.tfEndDate.text.toString()
        var status = binding.selectStatus.text.toString()
        val deadline = binding.tfEndDate.text.toString() // ini masih perlu di ubah

        when(status){
            "Active" -> status = "active"
            "On-Going" -> status = "on-going"
            "Cancelled" -> status = "cancelled"
            "Completed" -> status = "completed"
            else -> status = "null"
        }

        if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || status.isEmpty()) {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("Please fill all fields")
                setPositiveButton("OK", null)
                create()
                show()
            }
            return
        }

        if (projectId != null) {
            projectsViewModel.updateProject(
                name,
                description,
                status,
                startDate,
                endDate,
                deadline,
                projectId
            ).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> showLoading(true)

                    is ResultState.Success -> {
                        val dataProject = result.data.data?.updatedProject
                        if (dataProject != null) {
                            setResult(RESULT_OK)
                            finish()
                        }
                        showLoading(false)
                        showToast("Project ${dataProject?.name} updated successfully")

                        Log.i("EditProjectActivity", "Project ${dataProject?.name} updated successfully")
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        Log.e("EditProjectActivity", "Failed to update project: ${result.error}")
                    }
                }
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
        val projectName = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_NAME)
        val projectDescription = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_DESCRIPTION)
        val projectStatus = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_STATUS)
        val projectStartDate = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_START_DATE)
        val projectEndDate = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_END_DATE)

        binding.tfProjectName.setText(projectName)
        binding.tfDescription.setText(projectDescription)
        binding.tfStartDate.setText(formatDatePicker(projectStartDate.toString()))
        binding.tfEndDate.setText(formatDatePicker(projectEndDate.toString()))

        when(projectStatus){
            "active" -> binding.selectStatus.setText("Active", false)
            "on-going" -> binding.selectStatus.setText("On-Going", false)
            "cancelled" -> binding.selectStatus.setText("Cancelled", false)
            "completed" -> binding.selectStatus.setText("Completed", false)
            else -> binding.selectStatus.setText("Null", false)
        }
    }

    private fun getDataTeammatesAndTasks() {
        val projectId = intent.getStringExtra(EditProjectActivity.EXTRA_PROJECT_ID)
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
                        projectUsersAdapter.setOnItemClickCallback(object :
                            ProjectUsersAdapter.OnItemClickCallback {
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
                    Log.e("EditProjectActivity", "Failed to load teammates: ${result.error}")
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
                        projectTasksAdapter.setOnItemClickCallback(object :
                            ProjectTasksAdapter.OnItemClickCallback {
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
                    Log.e("EditProjectActivity", "Failed to load tasks: ${result.error}")
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
        const val EXTRA_PROJECT_ID = "extra_project_id"
        const val EXTRA_PROJECT_NAME = "extra_project_name"
        const val EXTRA_PROJECT_DESCRIPTION = "extra_project_description"
        const val EXTRA_PROJECT_STATUS = "extra_project_status"
        const val EXTRA_PROJECT_START_DATE = "extra_project_start_date"
        const val EXTRA_PROJECT_END_DATE = "extra_project_end_date"
        const val REQUEST_CREATE_TASK = 1
    }
}