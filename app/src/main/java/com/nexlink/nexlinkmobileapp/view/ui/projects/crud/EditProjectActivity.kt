package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.ui.tasks.CreateTaskActivity
import com.nexlink.nexlinkmobileapp.view.ui.tasks.DetailTaskActivity
import com.nexlink.nexlinkmobileapp.view.ui.users.SearchUserActivity
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
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

    private val usersViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(this)
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
        binding.btnAddTeammate.setOnClickListener{
            val searchUserIntent = Intent(this, SearchUserActivity::class.java).apply {
                putExtra(SearchUserActivity.EXTRA_PROJECT_ID, intent.getStringExtra(EXTRA_PROJECT_ID))
                putExtra(SearchUserActivity.EXTRA_USER_ADD_TYPE, "project")
            }
            startActivityForResult(searchUserIntent, REQUEST_ADD_TEAMMATES)
        }

        binding.btnAddTask.setOnClickListener {
            val createTaskIntent = Intent(this, CreateTaskActivity::class.java).apply {
                putExtra(
                    CreateTaskActivity.EXTRA_PROJECT_ID,
                    intent.getStringExtra(EXTRA_PROJECT_ID)
                )
            }
            startActivityForResult(createTaskIntent, REQUEST_CREATE_TASK)
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
        when(requestCode) {
            REQUEST_CREATE_TASK -> {
                if (resultCode == RESULT_OK) {
                    // Refresh data tasks setelah kembali dari CreateTaskActivity
                    val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
                    if (projectId != null) {
                        isTasksLoading = true
                        showLoading(true)
                        loadTasks(projectId)
                    }
                }
            }

            REQUEST_ADD_TEAMMATES -> {
                if (resultCode == RESULT_OK) {
                    // Refresh data teammates setelah kembali dari SearchUserActivity
                    val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
                    if (projectId != null) {
                        isTeammatesLoading = true
                        showLoading(true)
                        loadTeammates(projectId)
                    }
                }
            }
        }
    }

    private fun updateProject() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        val name = binding.tfProjectName.text.toString()
        val description = binding.tfDescription.text.toString()
        val startDate = binding.tfStartDate.text.toString()
        val endDate = binding.tfEndDate.text.toString()
        var status = binding.selectStatus.text.toString()
        val deadline = binding.tfEndDate.text.toString() // ini masih perlu di ubah

        when(status){
            "Active" -> status = "active"
            "In Progress" -> status = "in-progress"
            "Cancelled" -> status = "cancelled"
            "Completed" -> status = "completed"
            else -> status = "null"
        }

        if (name.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || status.isEmpty()) {
            alertInfoDialog(
                context = this,
                layoutInflater = layoutInflater,
                title = "Invalid Input",
                message = "Please fill all fields",
                icons = "info"
            )
            return
        }

        // Konfirmasi sebelum mengubah proyek
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                saveUpdateProject(name, description, status, startDate, endDate, deadline, projectId.toString())
            },
            title = "Confirm Update",
            message = "Are you sure the data is correct and you want to update the project?",
            icons = "info"
        )

    }

    private fun setupRecyclerViews() {
        projectUsersAdapter = ProjectUsersAdapter(showRemoveButton = true)
        binding.rvTeammates.adapter = projectUsersAdapter
        binding.rvTeammates.layoutManager = LinearLayoutManager(this)

        projectTasksAdapter = ProjectTasksAdapter()
        binding.rvTask.adapter = projectTasksAdapter
        binding.rvTask.layoutManager = LinearLayoutManager(this)
    }

    private fun setupDataDetailProject() {
        val projectName = intent.getStringExtra(EXTRA_PROJECT_NAME)
        val projectDescription = intent.getStringExtra(EXTRA_PROJECT_DESCRIPTION)
        val projectStatus = intent.getStringExtra(EXTRA_PROJECT_STATUS)
        val projectStartDate = intent.getStringExtra(EXTRA_PROJECT_START_DATE)
        val projectEndDate = intent.getStringExtra(EXTRA_PROJECT_END_DATE)

        binding.tfProjectName.setText(projectName)
        binding.tfDescription.setText(projectDescription)
        binding.tfStartDate.setText(formatDatePicker(projectStartDate.toString()))
        binding.tfEndDate.setText(formatDatePicker(projectEndDate.toString()))

        when(projectStatus){
            "active" -> binding.selectStatus.setText("Active", false)
            "in-progress" -> binding.selectStatus.setText("In Progress", false)
            "cancelled" -> binding.selectStatus.setText("Cancelled", false)
            "completed" -> binding.selectStatus.setText("Completed", false)
            else -> binding.selectStatus.setText("Null", false)
        }
    }

    private fun saveUpdateProject(name: String, description: String, status: String, startDate: String, endDate: String, deadline: String, projectId: String){
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
                    showLoading(false)
                    val dataProject = result.data.data?.updatedProject
                    if (dataProject != null) {
                        alertInfoDialogWithEvent(
                            context = this,
                            layoutInflater = layoutInflater,
                            onOkClicked = {
                                setResult(RESULT_OK)
                                finish()
                            },
                            title = "Update Project",
                            message = "Project ${dataProject.name} updated successfully!",
                            icons = "success"
                        )

                        Log.i("EditProjectActivity", "Project ${dataProject.name} updated successfully")
                    }

                }

                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Error",
                        message = "Failed to update project",
                        icons = "error"
                    )

                    Log.e("EditProjectActivity", "Failed to update project: ${result.error}")
                }
            }
        }
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

    private fun loadTeammates(projectId: String) {
        projectsViewModel.getProjectUsers(projectId).observe(this) { result ->
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
                                    data
                                )
                            }
                        })

                        projectUsersAdapter.setOnRemoveButtonClickCallback(object : ProjectUsersAdapter.OnRemoveButtonClickCallback {
                            override fun onRemoveButtonClicked(user: ListProjectUsersItem) {
                                alertConfirmDialog(
                                    context = this@EditProjectActivity,
                                    layoutInflater = layoutInflater,
                                    onYesClicked = {
                                        removeUserFromProject(user)
                                    },
                                    title = "Remove User",
                                    message = "Are you sure you want to remove ${user.fullName} from the project?",
                                    icons = "warning"
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

                    Log.e("EditProjectActivity", "Failed to load tasks: ${result.error}")
                    isTasksLoading = false
                    checkIfDataLoaded()
                }
            }
        }
    }

    private fun showSelectedUser(user: ListProjectUsersItem) {
        // TODO: Show user detail
    }

    private fun removeUserFromProject(user: ListProjectUsersItem) {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null && user.id != null) {
            usersViewModel.removeUserFromProject(user.id, projectId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.Success -> {
                        val data = result.data.message
                        if (data != null) {
                            alertInfoDialogWithEvent(
                                context = this,
                                layoutInflater = layoutInflater,
                                onOkClicked = {
                                    // Refresh data teammates setelah remove user
                                    isTeammatesLoading = true
                                    showLoading(true)
                                    loadTeammates(projectId)
                                },
                                title = "Remove User",
                                message = "User ${user.fullName} removed from project successfully!",
                                icons = "success"
                            )

                            Log.i("EditProjectActivity", "User: ${user.fullName}, ${data}")
                        }
                        showLoading(false)
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        alertInfoDialog(
                            context = this,
                            layoutInflater = layoutInflater,
                            title = "Error",
                            message = "Failed to remove user ${user.fullName}",
                            icons = "error"
                        )

                        Log.e("EditProjectActivity", "Failed to remove user: ${result.error}")
                    }
                }
            }
        }
    }


    private fun showSelectedTask(task: ListProjectTasksItem, viewHolder: RecyclerView.ViewHolder) {
        val detailProjectIntent = Intent(this, DetailTaskActivity::class.java).apply {
            putExtra(DetailTaskActivity.EXTRA_TASK_ID, task.id)
            putExtra(DetailTaskActivity.EXTRA_TASK_NAME, task.name)
            putExtra(DetailTaskActivity.EXTRA_TASK_DESCRIPTION, task.description)
            putExtra(DetailTaskActivity.EXTRA_TASK_STATUS, task.status)
            putExtra(DetailTaskActivity.EXTRA_TASK_START_DATE, task.startDate)
            putExtra(DetailTaskActivity.EXTRA_TASK_END_DATE, task.endDate)
            putExtra(DetailTaskActivity.EXTRA_TASK_PRIORITY, task.priority)
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
        const val REQUEST_ADD_TEAMMATES = 1
    }
}