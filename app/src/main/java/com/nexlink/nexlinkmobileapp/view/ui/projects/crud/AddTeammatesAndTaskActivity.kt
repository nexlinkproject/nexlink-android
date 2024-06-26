package com.nexlink.nexlinkmobileapp.view.ui.projects.crud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.ml.ListDataScheduleItem
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectTasksItem
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivityAddTeammatesAndTaskBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectTasksAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ScheduleMLAdapter
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.MLModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.TasksModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ml.MLViewModel
import com.nexlink.nexlinkmobileapp.view.ui.auth.AuthViewModel
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.ui.tasks.CreateTaskActivity
import com.nexlink.nexlinkmobileapp.view.ui.tasks.DetailTaskActivity
import com.nexlink.nexlinkmobileapp.view.ui.tasks.TasksViewModel
import com.nexlink.nexlinkmobileapp.view.ui.users.SearchUserActivity
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent
import com.nexlink.nexlinkmobileapp.view.utils.formatDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class AddTeammatesAndTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTeammatesAndTaskBinding

    private lateinit var projectUsersAdapter: ProjectUsersAdapter
    private lateinit var projectTasksAdapter: ProjectTasksAdapter
    private lateinit var mlScheduleAdaptar: ScheduleMLAdapter

    private var isTeammatesLoading = false
    private var isTasksLoading = false

    private val projectsViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(this)
    }

    private val tasksViewModel by viewModels<TasksViewModel> {
        TasksModelFactory.getInstance(this)
    }

    private val usersViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(this)
    }

    private val authViewModel by viewModels<AuthViewModel> {
        AuthModelFactory.getInstance(this)
    }

    private val mlViewModel by viewModels<MLViewModel> {
        MLModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTeammatesAndTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set up buttons click listeners
        binding.btnAddTeammate.setOnClickListener {
            val searchUserIntent = Intent(this, SearchUserActivity::class.java).apply {
                putExtra(
                    SearchUserActivity.EXTRA_PROJECT_ID,
                    intent.getStringExtra(EXTRA_PROJECT_ID)
                )
                putExtra(SearchUserActivity.EXTRA_USER_ADD_TYPE, "project")
            }
            startActivityForResult(searchUserIntent, REQUEST_ADD_TEAMMATES)
        }

        binding.btnAddTask.setOnClickListener {
            val detailProjectIntent =
                Intent(this@AddTeammatesAndTaskActivity, CreateTaskActivity::class.java).apply {
                    putExtra(
                        CreateTaskActivity.EXTRA_PROJECT_ID,
                        intent.getStringExtra(EXTRA_PROJECT_ID)
                    )
                }
            startActivityForResult(detailProjectIntent, REQUEST_CREATE_TASK)
        }

        binding.btnGenerateMl.setOnClickListener {
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    generateAIForTask()
                },
                title = "Confirm Generate AI",
                message = "Are you sure you want to generate AI for change tasks this project? This action cannot be undone.",
                icons = "warning"
            )
        }

        binding.btnSubmit.setOnClickListener {
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    sendFeedbackToML()
                },
                title = "Confirm Project",
                message = "Are you sure you want to submit this project?",
                icons = "warning"
            )
        }

        // save user to project
        authViewModel.getSession().observe(this) { user ->
            addUserToProject(user.userId)
        }

        setupRecyclerViews()
        setupDataDetailProject()
    }

    override fun onResume() {
        super.onResume()
        getDataTeammatesAndTasks()
    }

    override fun onBackPressed() {
        alertConfirmDialog(
            context = this,
            layoutInflater = layoutInflater,
            onYesClicked = {
                sendFeedbackToML()
                super.onBackPressed()
            },
            title = "Confirm Exit",
            message = "Are you sure you want to leave this page? Your changes might not be saved.",
            icons = "warning"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CREATE_TASK -> {
                if (resultCode == RESULT_OK) {
                    // Refresh data tasks setelah kembali dari CreateTaskActivity
                    val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
                    if (projectId != null) {
                        isTasksLoading = true
                        showLoading(true)
                        loadTasks()
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
                        loadTeammates()
                    }
                }
            }
        }
    }

    private fun setupRecyclerViews() {
        projectUsersAdapter = ProjectUsersAdapter(showRemoveButton = true)
        binding.rvTeammates.adapter = projectUsersAdapter

        projectTasksAdapter = ProjectTasksAdapter()
        binding.rvTask.adapter = projectTasksAdapter
    }

    private fun addUserToProject(userId: String) {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            usersViewModel.addUserToProject(userId, projectId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        // Refresh data teammates setelah menambahkan user ke project
                        isTeammatesLoading = true
                        showLoading(true)
                        loadTeammates()
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        Log.e(
                            "AddTeamAndTaskActivity",
                            "Failed to add user to project: ${result.error}"
                        )
                    }
                }
            }
        }
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
    }

    private fun getDataTeammatesAndTasks() {
        isTeammatesLoading = true
        isTasksLoading = true
        showLoading(true)

        loadTeammates()
        loadTasks()
    }

    private fun loadTeammates() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
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
                                        data,
                                        binding.rvTask.findViewHolderForAdapterPosition(
                                            projectUsersAdapter.currentList.indexOf(data)
                                        )!!
                                    )
                                }
                            })

                            projectUsersAdapter.setOnRemoveButtonClickCallback(object :
                                ProjectUsersAdapter.OnRemoveButtonClickCallback {
                                override fun onRemoveButtonClicked(user: ListProjectUsersItem) {
                                    alertConfirmDialog(
                                        context = this@AddTeammatesAndTaskActivity,
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

                        Log.e("AddTeamAndTaskActivity", "Failed to load teammates: ${result.error}")
                        isTeammatesLoading = false
                        checkIfDataLoaded()
                    }
                }
            }
        }
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
                                    loadTeammates()
                                },
                                title = "Remove User",
                                message = "User ${user.fullName} removed from project successfully!",
                                icons = "success"
                            )

                            Log.i("AddTeamAndTaskActivity", "User: ${user.fullName}, ${data}")
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

                        Log.e("AddTeamAndTaskActivity", "Failed to remove user: ${result.error}")
                    }
                }
            }
        }
    }

    private fun loadTasks() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
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

                        Log.e("AddTeamAndTaskActivity", "Failed to load tasks: ${result.error}")
                        isTasksLoading = false
                        checkIfDataLoaded()
                    }
                }
            }
        }
    }

    private fun showSelectedUser(user: ListProjectUsersItem, viewHolder: RecyclerView.ViewHolder) {
        // Todo: Show user detail
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

    // ============================ GENERATE AI FOR TASK ============================
    private fun generateAIForTask() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            mlViewModel.scheduleML(projectId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.Success -> {
                        val data = result.data.data
                        if (data != null) {
                            updateListTask(data)
                            alertInfoDialogWithEvent(
                                context = this,
                                layoutInflater = layoutInflater,
                                onOkClicked = {
                                    // Refresh data tasks setelah generate AI
                                    isTasksLoading = true
                                    showLoading(true)
                                    loadTasks()
                                },
                                title = "Generate AI",
                                message = "AI for tasks in this project has been generated successfully!\nif task not change please try again",
                                icons = "success"
                            )

                            Log.i("AddTeamAndTaskActivity", "AI generated: ${data}")
                        }
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        showLoading(false)

                        if (result.error == "timeout") {
                            alertInfoDialog(
                                context = this,
                                layoutInflater = layoutInflater,
                                title = "Error",
                                message = "Failed to generate AI for tasks in this project, please try again.",
                                icons = "error"
                            )
                        } else {
                            alertInfoDialog(
                                context = this,
                                layoutInflater = layoutInflater,
                                title = "Error",
                                message = "Failed to generate AI for tasks in this project, please check if the task have some user and try again.",
                                icons = "error"
                            )
                        }

                        Log.e("AddTeamAndTaskActivity", "Failed to generate AI: ${result.error}")
                    }
                }
            }
        }
    }

//    private fun updateListTask(data: List<ListDataScheduleItem?>?) {
//        mlScheduleAdaptar = ScheduleMLAdapter()
//        binding.rvTask.adapter = mlScheduleAdaptar
//        mlScheduleAdaptar.submitList(data)
//
//        mlScheduleAdaptar.setOnItemClickCallback(object : ScheduleMLAdapter.OnItemClickCallback {
//            override fun onItemClicked(data: ListDataScheduleItem) {
//                showSelectedTaskML(
//                    data
//                )
//            }
//        })
//    }

    private fun updateListTask(data: List<ListDataScheduleItem?>?) {
        // Tampilkan loading
        showLoading(true)

        // Update data satu persatu untuk task yang sudah di generate AI
        val updateTaskJobs = mutableListOf<Job>()

        data?.forEach { task ->
            if (task != null) {
                val fields = mutableMapOf<String, Any>()
                task.name?.let { fields["name"] = it }
                task.startDate?.let { fields["startDate"] = it }
                task.dueDate?.let { fields["endDate"] = it }

                task.taskId?.let {
                    val job = lifecycleScope.launch {
                        tasksViewModel.updateTaskPartial(it, fields).observe(this@AddTeammatesAndTaskActivity) { result ->
                            when (result) {
                                is ResultState.Loading -> {}
                                is ResultState.Success -> {
                                    val message = result.data.message
                                    val data = result.data.data
                                    if (message != null) {
                                        Log.i("AddTeamAndTaskActivity", message)
                                        Log.i("AddTeamAndTaskActivity", "Task updated: ${data}")
                                    }
                                }
                                is ResultState.Error -> {
                                    Log.e("AddTeamAndTaskActivity", "Failed to update task: ${result.error}")
                                }
                            }
                        }
                    }
                    updateTaskJobs.add(job)
                }
            }
        }

        // Setelah semua task selesai diupdate, panggil loadTasks
        lifecycleScope.launch {
            updateTaskJobs.joinAll()
            showLoading(false)
        }
    }

    private fun sendFeedbackToML() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            mlViewModel.feedbackML(projectId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> showLoading(true)
                    is ResultState.Success -> {
                        showLoading(false)
                        val message = result.data.message
                        val data = result.data.data
                        if (message != null) {
                            Log.i("AddTeamAndTaskActivity", message)
                            Log.i("AddTeamAndTaskActivity", "Feedback sent: ${data}")
                        }
                        finish()
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        Log.e("AddTeamAndTaskActivity", "Failed to send feedback: ${result.error}")
                        finish()
                    }
                }
            }
        }
    }
    // ============================ END GENERATE AI FOR TASK ============================

    private fun checkIfDataLoaded() {
        if (!isTeammatesLoading && !isTasksLoading) {
            showLoading(false)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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