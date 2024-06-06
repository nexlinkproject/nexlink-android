package com.nexlink.nexlinkmobileapp.view.ui.projects

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.R
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.projects.ListAllProjectsItem
import com.nexlink.nexlinkmobileapp.databinding.FragmentProjectsBinding
import com.nexlink.nexlinkmobileapp.view.adapter.DateAdapter
import com.nexlink.nexlinkmobileapp.view.adapter.ProjectsAdapter
import com.nexlink.nexlinkmobileapp.view.factory.ProjectsModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.projects.crud.CreateProjectActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProjectsFragment : Fragment() {

    private val projectsViewModel by viewModels<ProjectsViewModel> {
        ProjectsModelFactory.getInstance(requireContext())
    }

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!

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
        val adapter = DateAdapter(dates)
        recyclerView.adapter = adapter

        binding.btnAddProject.setOnClickListener {
            val intent = Intent(context, CreateProjectActivity::class.java)
            startActivity(intent)
        }

        // Toggle buttons untuk langsung menampilkan semua project
        binding.btnGroupProjectFilter.check(binding.btnAllProject.id)

        // Menampilkan semua project
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllProjects.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvAllProjects.addItemDecoration(itemDecoration)

        getAllProjects()

        return root
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

    private fun getAllProjects(){
        projectsViewModel.getProjects().observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    val stories = result.data.data?.projects
                    setProjectsData(stories)
                }

                is ResultState.Error -> {
                    showLoading(false)
                    val message = result.error
                    showToast(message)
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

    private fun showSelectedProject(story: ListAllProjectsItem, viewHolder: RecyclerView.ViewHolder) {
        showToast("Project ${story.name} clicked")
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