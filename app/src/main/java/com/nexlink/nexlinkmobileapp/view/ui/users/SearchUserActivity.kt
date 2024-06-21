package com.nexlink.nexlinkmobileapp.view.ui.users

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListProjectUsersItem
import com.nexlink.nexlinkmobileapp.data.remote.response.users.ListAllUsersItem
import com.nexlink.nexlinkmobileapp.databinding.ActivitySearchUserBinding
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectUsersAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.UsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.ProjectsViewModel
import com.nexlink.nexlinkmobileapp.view.utils.alertConfirmDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialog
import com.nexlink.nexlinkmobileapp.view.utils.alertInfoDialogWithEvent

class SearchUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchUserBinding

    private lateinit var usersAdapter: UsersAdapter
    private lateinit var projectUsersAdapter: ProjectUsersAdapter

    private val usersViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(this)
    }

    private val projectViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySearchUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.btnSearch.setOnClickListener {
            binding.searchView.visibility = View.VISIBLE
            binding.searchView.isIconified = false
            binding.btnSearch.visibility = View.GONE
        }

        binding.searchView.setOnCloseListener {
            binding.searchView.visibility = View.GONE
            binding.btnSearch.visibility = View.VISIBLE
            false
        }

        val userAddType = intent.getStringExtra(EXTRA_USER_ADD_TYPE)
        when (userAddType) {
            "project" -> {
                println("Project")
                usersAdapter = UsersAdapter()
                binding.rvUser.adapter = usersAdapter
                setupSearchView()
                loadAllUsers()
            }
            "tasks" -> {
                println("Task")
                projectUsersAdapter = ProjectUsersAdapter(showRemoveButton = false)
                binding.rvUser.adapter = projectUsersAdapter
                setupProjectSearchView()
                loadAllProjectUsers()
            }
        }

        binding.rvUser.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchUser(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchUser(it)
                }
                return true
            }
        })
    }

    private fun searchUser(query: String) {
        usersViewModel.getAllUsers().observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }
                is ResultState.Success -> {
                    showLoading(false)
                    val users = result.data.data?.users?.filter {
                        it?.fullName?.contains(query, ignoreCase = true) ?: false
                    }
                    setUsersData(users)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    Log.e("SearchUserActivity", "Failed to search users: ${result.error}")
                }
            }
        }
    }

    private fun loadAllUsers() {
        usersViewModel.getAllUsers().observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val users = result.data.data?.users
                    setUsersData(users)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    binding.tvNoUsers.visibility = View.VISIBLE
                    Log.e("DetailProjectActivity", "Failed to load teammates: ${result.error}")
                }
            }
        }
    }

    private fun setUsersData(users: List<ListAllUsersItem?>?) {
        usersAdapter.submitList(users)
        binding.tvNoUsers.visibility = if (users.isNullOrEmpty()) View.VISIBLE else View.GONE

        usersAdapter.setOnItemClickCallback(object : UsersAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListAllUsersItem) {
                showSelectedUsers(
                    data,
                    binding.rvUser.findViewHolderForAdapterPosition(
                        usersAdapter.currentList.indexOf(data)
                    )!!
                )
            }
        })
    }

    private fun showSelectedUsers(user: ListAllUsersItem, viewHolder: RecyclerView.ViewHolder) {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            // Tampilkan dialog konfirmasi terlebih dahulu
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    addUserToProject(user.id.toString(), projectId)
                },
                title = "Add User to Project",
                message = "Are you sure you want to add ${user.fullName} to the project?",
                icons = "info"
            )
        }
    }

    private fun addUserToProject(userId: String, projectId: String){
        usersViewModel.addUserToProject(userId, projectId).observe(this@SearchUserActivity) { result ->
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
                            val dataUser = result.data.data?.addProjectUser
                            if (dataUser != null) {
                                setResult(RESULT_OK)
                                finish()
                            }
                        },
                        title = "Add User to Project",
                        message = "User added to project successfully!",
                        icons = "success"
                    )
                    Log.i("SearchUserActivity", "User with id: $userId added to project successfully!")
                }
                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Add User to Project",
                        message = "User is already added to the project",
                        icons = "error"
                    )
                    Log.e("SearchUserActivity", "Failed to add user to project: ${result.error}")
                }
            }
        }
    }

    // ================ Function for get user in project ================
    private fun setupProjectSearchView() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchUserProject(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchUserProject(it)
                }
                return true
            }
        })
    }

    private fun searchUserProject(query: String) {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            projectViewModel.getProjectUsers(projectId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        val users = result.data.data?.project?.users?.filter {
                            it?.fullName?.contains(query, ignoreCase = true) ?: false
                        }
                        setProjectUsersData(users)
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        Log.e("SearchUserActivity", "Failed to search users: ${result.error}")
                    }
                }
            }
        }
    }

    private fun loadAllProjectUsers() {
        val projectId = intent.getStringExtra(EXTRA_PROJECT_ID)
        if (projectId != null) {
            projectViewModel.getProjectUsers(projectId).observe(this) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showLoading(false)
                        val users = result.data.data?.project?.users
                        setProjectUsersData(users)
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        binding.tvNoUsers.visibility = View.VISIBLE
                        Log.e("DetailProjectActivity", "Failed to load teammates: ${result.error}")
                    }
                }
            }
        }
    }

    private fun setProjectUsersData(users: List<ListProjectUsersItem?>?) {
        projectUsersAdapter.submitList(users)
        binding.tvNoUsers.visibility = if (users.isNullOrEmpty()) View.VISIBLE else View.GONE

        projectUsersAdapter.setOnItemClickCallback(object : ProjectUsersAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListProjectUsersItem) {
                showSelectedProjectUsers(
                    data,
                    binding.rvUser.findViewHolderForAdapterPosition(
                        projectUsersAdapter.currentList.indexOf(data)
                    )!!
                )
            }
        })
    }

    private fun showSelectedProjectUsers(user: ListProjectUsersItem, viewHolder: RecyclerView.ViewHolder) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        if (taskId != null) {
            // Tampilkan dialog konfirmasi terlebih dahulu
            alertConfirmDialog(
                context = this,
                layoutInflater = layoutInflater,
                onYesClicked = {
                    addUserToTask(user.id.toString(), taskId)
                },
                title = "Add User to Task",
                message = "Are you sure you want to add ${user.fullName} to the task?",
                icons = "info"
            )
        }
    }

    private fun addUserToTask(userId: String, taskId: String){
        usersViewModel.addUserToTask(userId, taskId).observe(this@SearchUserActivity) { result ->
            when (result) {
                is ResultState.Loading -> { showLoading(true) }
                is ResultState.Success -> {
                    showLoading(false)
                    alertInfoDialogWithEvent(
                        context = this,
                        layoutInflater = layoutInflater,
                        onOkClicked = {
                            val dataUser = result.data.data?.addTaskUser
                            if (dataUser != null) {
                                setResult(RESULT_OK)
                                finish()
                            }
                        },
                        title = "Add User to Task",
                        message = "User added to task successfully!",
                        icons = "success"
                    )
                    Log.i("SearchUserActivity", "User with id: $userId added to task successfully!")
                }
                is ResultState.Error -> {
                    showLoading(false)
                    alertInfoDialog(
                        context = this,
                        layoutInflater = layoutInflater,
                        title = "Add User to Task",
                        message = "User is already added to the task",
                        icons = "error"
                    )
                    Log.e("SearchUserActivity", "Failed to add user to task: ${result.error}")
                }
            }
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
        const val EXTRA_PROJECT_ID = "extra_project_id"
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_USER_ADD_TYPE = "extra_user_add_type"
    }
}
