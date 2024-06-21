package com.example.assignto_do.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignto_do.R
import com.example.assignto_do.adapters.EmployeeWorkAdapter
import com.example.assignto_do.databinding.ActivityEmployeeMainBinding
import com.example.assignto_do.models.Works
import com.example.assignto_do.utils.Utils
import com.example.assignto_do.views.activities.auth.SignInActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EmployeeMainActivity : AppCompatActivity() {

    private val binding: ActivityEmployeeMainBinding by lazy {
        ActivityEmployeeMainBinding.inflate(layoutInflater)
    }

    private lateinit var employeeWorkAdapter: EmployeeWorkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.employeeToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    showLogoutDialog()
                    true
                }

                else -> false
            }
        }

        prepareRvForEmployeeWorksAdapter()
        showEmployeeWorks()

    }

    private fun prepareRvForEmployeeWorksAdapter() {
        employeeWorkAdapter = EmployeeWorkAdapter(::onProgressBtnClicked, ::onCompletedBtnClicked)
        binding.employeeWorkRv.apply {
            layoutManager =
                LinearLayoutManager(this@EmployeeMainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = employeeWorkAdapter
        }
    }

    private fun showEmployeeWorks() {
        binding.progressBar.visibility = View.VISIBLE

        val empId = FirebaseAuth.getInstance().currentUser?.uid
        val workRef = FirebaseDatabase.getInstance().getReference("Works")
        workRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (workRooms in snapshot.children) {
                    if (workRooms.key?.contains(empId!!) == true) {
                        val empWorkRef = workRef.child(workRooms.key!!)
                        empWorkRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val workList = ArrayList<Works>()
                                for (works in snapshot.children) {
                                    val work = works.getValue(Works::class.java)
                                    workList.add(work!!)
                                }
                                employeeWorkAdapter.differ.submitList(workList)
                                binding.progressBar.visibility = View.GONE

                                binding.text.visibility = if (workList.isEmpty()){
                                    View.VISIBLE
                                } else{
                                    View.GONE
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                binding.progressBar.visibility = View.GONE
                                Utils.showToast(
                                    this@EmployeeMainActivity,
                                    "Something went wrong. Please try again"
                                )
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Utils.showToast(this@EmployeeMainActivity, "Something went wrong. Please try again")
            }
        })
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this)
        val alertDialog = builder.create()

        builder.setTitle("Logout")
            .setMessage("Are your sure you want to Logout?")
            .setPositiveButton("Yes") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            .setNegativeButton("No") { _, _ ->
                alertDialog.dismiss()
            }
            .show()
            .setCancelable(false)
    }

    private fun onProgressBtnClicked(works: Works, progressBtn: MaterialButton) {

        if (progressBtn.text != "In Progress") {
            val builder = AlertDialog.Builder(this)
            val alertDialog = builder.create()

            builder.setTitle("Starting Work")
                .setMessage("Are you starting this work?")
                .setPositiveButton("Yes") { _, _ ->
                    progressBtn.apply {
                        text = "In Progress"
                        setTextColor(
                            ContextCompat.getColor(
                                this@EmployeeMainActivity,
                                R.color.light5
                            )
                        )
                    }
                    updateStatus(works, "2")
                }
                .setNegativeButton("No") { _, _ ->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false)
        } else{
            Utils.showToast(this, "Work is progress or completed")
        }
    }

    private fun updateStatus(works: Works, status: String) {
        binding.progressBar.visibility = View.VISIBLE

        val empId = FirebaseAuth.getInstance().currentUser?.uid
        val workRef = FirebaseDatabase.getInstance().getReference("Works")
        workRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (workRooms in snapshot.children) {
                    if (workRooms.key?.contains(empId!!) == true) {
                        val empWorkRef = workRef.child(workRooms.key!!)
                        empWorkRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (allWorks in snapshot.children) {
                                    val work = allWorks.getValue(Works::class.java)
                                    if (works.workId == work?.workId) {
                                        empWorkRef.child(allWorks.key!!).child("workStatus").setValue(status)
                                    }
                                }
                                binding.progressBar.visibility = View.GONE
                            }

                            override fun onCancelled(error: DatabaseError) {
                                binding.progressBar.visibility = View.GONE
                                Utils.showToast(
                                    this@EmployeeMainActivity,
                                    "Something went wrong. Please try again"
                                )
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Utils.showToast(this@EmployeeMainActivity, "Something went wrong. Please try again")
            }
        })
    }

    private fun onCompletedBtnClicked(works: Works, completeBtn: MaterialButton) {

        if (completeBtn.text != "Work Completed") {
            val builder = AlertDialog.Builder(this)
            val alertDialog = builder.create()

            builder.setTitle("Completed Work")
                .setMessage("Are you sure you completed this work?")
                .setPositiveButton("Yes") { _, _ ->
                    completeBtn.apply {
                        setTextColor(
                            ContextCompat.getColor(
                                this@EmployeeMainActivity,
                                R.color.light5
                            )
                        )
                    }
                    updateStatus(works, "3")
                }
                .setNegativeButton("No") { _, _ ->
                    alertDialog.dismiss()
                }
                .show()
                .setCancelable(false)
        } else{
            Utils.showToast(this, "Work is progress or completed")
        }
    }

}