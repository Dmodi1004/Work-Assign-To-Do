package com.example.assignto_do.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignto_do.adapters.WorksAdapter
import com.example.assignto_do.databinding.FragmentWorksBinding
import com.example.assignto_do.models.Works
import com.example.assignto_do.utils.Utils
import com.example.assignto_do.views.activities.auth.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WorksFragment : Fragment() {

    private val binding: FragmentWorksBinding by lazy {
        FragmentWorksBinding.inflate(layoutInflater)
    }
    private val employeeDetails by navArgs<WorksFragmentArgs>()
    private lateinit var worksAdapter: WorksAdapter
    private lateinit var workRoom: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.assignWorkFab.setOnClickListener {
            val action =
                WorksFragmentDirections.actionWorksFragmentToAssignWorkFragment(employeeDetails.employeeData)
            findNavController().navigate(action)
        }

        val empName = employeeDetails.employeeData.userName

        binding.employeeToolbar.apply {
            title = empName
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        prepareRvForWorksAdapter()
        showAllWorks()

        return binding.root
    }

    private fun showAllWorks() {
        Utils.showDialog(requireContext())
        val bossId = FirebaseAuth.getInstance().currentUser?.uid
        workRoom = bossId + employeeDetails.employeeData.userId

        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val workList = ArrayList<Works>()
                    for (allWorks in snapshot.children) {
                        val work = allWorks.getValue(Works::class.java)
                        workList.add(work!!)
                    }
                    worksAdapter.differ.submitList(workList)
                    Utils.hideDialog()

                    binding.text.visibility = if (workList.isEmpty()){
                        View.VISIBLE
                    } else{
                        View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }

    private fun prepareRvForWorksAdapter() {
        worksAdapter = WorksAdapter(::onUnassignedBtnClicked)
        binding.workRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = worksAdapter
        }
    }

    private fun onUnassignedBtnClicked(works: Works) {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()

        builder.setTitle("Unassigned Work")
            .setMessage("Are your sure you want to Unassigned this work?")
            .setPositiveButton("Yes") { _, _ ->
                unassignedWork(works)
            }
            .setNegativeButton("No") { _, _ ->
                alertDialog.dismiss()
            }
            .show()
    }

    private fun unassignedWork(works: Works) {
        works.expanded = !works.expanded
        FirebaseDatabase.getInstance().getReference("Works").child(workRoom)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (allWorks in snapshot.children){
                        val currentWork = allWorks.getValue(Works::class.java)
                        if (currentWork == works){
                            allWorks.ref.removeValue()
                            Utils.showToast(requireContext(), "Work has been Unassigned")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}