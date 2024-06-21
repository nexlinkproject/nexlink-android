package com.nexlink.nexlinkmobileapp.view.ui.projects

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListAllProjectsItem
import com.nexlink.nexlinkmobileapp.databinding.FragmentProjectsBinding
import com.nexlink.nexlinkmobileapp.view.adapter.DateAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectsAdapter
import com.nexlink.nexlinkmobileapp.view.factory.AuthModelFactory
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.auth.AuthViewModel
import com.nexlink.nexlinkmobileapp.view.ui.projects.crud.CreateProjectActivity
import com.nexlink.nexlinkmobileapp.view.ui.projects.crud.DetailProjectActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProjectsFragment : Fragment(), DateAdapter.OnDateClickListener {

    private val projectsViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(requireContext())
    }

    private val authViewModel by viewModels<AuthViewModel> {
        AuthModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!

    private var user_id: String? = null
    private var filter_status: String? = null
    private var filter_date: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProjectsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.rvDates
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val dates = getDatesOfMonth()
        val adapter = DateAdapter(dates, this)
        recyclerView.adapter = adapter

        binding.btnAddProject.setOnClickListener {
            val intent = Intent(context, CreateProjectActivity::class.java)
            startActivity(intent)
        }

        setUpProjectMenu()

        // Toggle buttons untuk langsung menampilkan semua project
        binding.btnGroupProjectFilter.check(binding.btnAllProject.id)
        binding.btnGroupProjectFilter.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.btnAllProject.id -> {
                        filter_status = null
                        getProjectsByUserId()
                    }
                    binding.btnInProgress.id -> {
                        filter_status = "in-progress"
                        filterProjects()
                    }
                    binding.btnDone.id -> {
                        filter_status = "completed"
                        filterProjects()
                    }
                }
            }
        }

        // Retry button
        binding.btnRetry.setOnClickListener {
            showTimeout(false)
            getProjectsByUserId()
        }

        // Setup recycler view
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllProjects.layoutManager = layoutManager

        return root
    }

    override fun onResume() {
        super.onResume()
        binding.btnGroupProjectFilter.check(binding.btnAllProject.id)
        getProjectsByUserId()
    }

    private fun setUpProjectMenu(){
        authViewModel.getSession().observe(viewLifecycleOwner) { user ->
            user_id = user.userId
        }
    }

    private fun getDatesOfMonth(): List<Date> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<Date>()
        val today = calendar.time

        binding.tvDateMonth.text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(today)

        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        for (i in currentDay..maxDay) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

//    private fun getAllProjects(status: String? = null){
//        projectsViewModel.getProjects(status).observe(viewLifecycleOwner) { result ->
//            when (result) {
//                is ResultState.Loading -> {
//                    showTimeout(false)
//                    showLoading(true)
//                }
//
//                is ResultState.Success -> {
//                    showLoading(false)
//                    showTimeout(false)
//                    val stories = result.data.data?.projects
//                    setProjectsData(stories)
//                }
//
//                is ResultState.Error -> {
//                    showLoading(false)
//                    if (result.error == "timeout"){
//                        showTimeout(true)
//                        showToast("Please check your internet connection")
//                    }else{
//                        binding.rvAllProjects.visibility = View.GONE
//                        binding.tvNoDataProjects.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
//    }

    private fun getProjectsByUserId(status: String? = null, date: String? = null){
        projectsViewModel.getProjectsByUserId(user_id.toString(), status, date).observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showTimeout(false)
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    showTimeout(false)
                    val stories = result.data.data?.projects
                    setProjectsData(stories)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    if (result.error == "timeout"){
                        showTimeout(true)
                        showToast("Please check your internet connection")
                    }else{
                        binding.rvAllProjects.visibility = View.GONE
                        binding.tvNoDataProjects.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setProjectsData(stories: List<ListAllProjectsItem?>?) {
        val adapter = ProjectsAdapter()
        adapter.submitList(stories)
        binding.rvAllProjects.adapter = adapter

        adapter.setOnItemClickCallback(object : ProjectsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListAllProjectsItem) {
                showSelectedProject(
                    data,
                    binding.rvAllProjects.findViewHolderForAdapterPosition(
                        adapter.currentList.indexOf(data)
                    )!!
                )
            }
        })
    }

    private fun filterProjects() {
        println(filter_status)
        println(filter_date)
        getProjectsByUserId(filter_status, filter_date)
    }

    override fun onDateClick(date: Date) {
        filter_date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        filterProjects()
    }

    private fun showSelectedProject(story: ListAllProjectsItem, viewHolder: RecyclerView.ViewHolder) {
        val detailProjectIntent = Intent(requireContext(), DetailProjectActivity::class.java).apply {
            putExtra(DetailProjectActivity.EXTRA_PROJECT_ID, story.id)
            putExtra(DetailProjectActivity.EXTRA_PROJECT_NAME, story.name)
            putExtra(DetailProjectActivity.EXTRA_PROJECT_DESCRIPTION, story.description)
            putExtra(DetailProjectActivity.EXTRA_PROJECT_STATUS, story.status)
            putExtra(DetailProjectActivity.EXTRA_PROJECT_START_DATE, story.startDate)
            putExtra(DetailProjectActivity.EXTRA_PROJECT_END_DATE, story.endDate)
        }
        startActivity(detailProjectIntent)
    }

    private fun showTimeout(isTimeout: Boolean) {
        binding.tvNoDataProjects.visibility = View.GONE
        binding.rvAllProjects.visibility = if (isTimeout) View.GONE else View.VISIBLE
        binding.timeoutLayout.visibility = if (isTimeout) View.VISIBLE else View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}