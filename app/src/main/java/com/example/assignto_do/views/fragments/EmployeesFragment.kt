package com.example.assignto_do.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignto_do.R
import com.example.assignto_do.adapters.EmployeesAdapter
import com.example.assignto_do.databinding.FragmentEmployeesBinding
import com.example.assignto_do.models.Users
import com.example.assignto_do.utils.Utils
import com.example.assignto_do.views.activities.auth.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmployeesFragment : Fragment() {

    private val binding: FragmentEmployeesBinding by lazy {
        FragmentEmployeesBinding.inflate(layoutInflater)
    }

    private lateinit var empAdapter: EmployeesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.apply {
            employeeToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.logout -> {
                        showLogoutDialog()
                        true
                    }

                    else -> false
                }
            }
        }
        prepareRvForEmployeeAdapter()
        showAllEmployees()

        return binding.root
    }

    private fun showAllEmployees() {
        binding.progressBar.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().getReference("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val empList = arrayListOf<Users>()
                    for (employees in snapshot.children) {
                        val currentUser = employees.getValue(Users::class.java)
                        if (currentUser?.userType == "Employee") {
                            empList.add(currentUser)
                        }
                    }
                    empAdapter.differ.submitList(empList)
                    binding.progressBar.visibility = View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressBar.visibility = View.GONE
                    Utils.showToast(requireContext(), error.message)
                }
            })
    }

    private fun prepareRvForEmployeeAdapter() {
        empAdapter = EmployeesAdapter()
        binding.employeesRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = empAdapter
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()

        builder.setTitle("Logout")
            .setMessage("Are your sure you want to Logout?")
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(requireContext(), SignInActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("No") { _, _ ->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

}