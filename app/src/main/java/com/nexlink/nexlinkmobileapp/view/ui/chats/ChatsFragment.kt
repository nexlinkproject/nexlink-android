package com.nexlink.nexlinkmobileapp.view.ui.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nexlink.nexlinkmobileapp.data.ResultState
import com.nexlink.nexlinkmobileapp.data.remote.response.users.ListAllUsersItem
import com.nexlink.nexlinkmobileapp.databinding.FragmentChatsBinding
import com.nexlink.nexlinkmobileapp.view.adapter.UsersAdapter
import com.nexlink.nexlinkmobileapp.view.factory.UsersModelFactory
import com.nexlink.nexlinkmobileapp.view.ui.users.UsersViewModel

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var usersAdapter: UsersAdapter

    private val usersViewModel by viewModels<UsersViewModel> {
        UsersModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val chatsViewModel =
            ViewModelProvider(this).get(ChatsViewModel::class.java)

        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup recycler view
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvChats.layoutManager = layoutManager

        return root
    }

    override fun onResume() {
        super.onResume()
        loadAllUsers()
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
                    binding.tvNoDataUser.visibility = View.VISIBLE
                    Log.e("DetailProjectActivity", "Failed to load teammates: ${result.error}")
                }
            }
        }
    }

    private fun setUsersData(users: List<ListAllUsersItem?>?) {
        usersAdapter = UsersAdapter()
        usersAdapter.submitList(users)
        binding.rvChats.adapter = usersAdapter

        binding.tvNoDataUser.visibility = if (users.isNullOrEmpty()) View.VISIBLE else View.GONE

        usersAdapter.setOnItemClickCallback(object : UsersAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListAllUsersItem) {
                showSelectedUsers(
                    data,
                    binding.rvChats.findViewHolderForAdapterPosition(
                        usersAdapter.currentList.indexOf(data)
                    )!!
                )
            }
        })
    }

    private fun showSelectedUsers(user: ListAllUsersItem, viewHolder: RecyclerView.ViewHolder) {
        val detailProjectIntent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(ChatActivity.EXTRA_USER_ID, user.id)
            putExtra(ChatActivity.EXTRA_USER_NAME, user.fullName)
        }
        startActivity(detailProjectIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}