package com.example.assignto_do.views.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.assignto_do.R
import com.example.assignto_do.databinding.FragmentAssignWorkBinding
import com.example.assignto_do.models.Works
import com.example.assignto_do.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class AssignWorkFragment : Fragment() {

    private val binding: FragmentAssignWorkBinding by lazy {
        FragmentAssignWorkBinding.inflate(layoutInflater)
    }

    private var priority = "1"
    private val employeeData by navArgs<AssignWorkFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding.employeeToolbar.apply {
            setNavigationOnClickListener { activity?.onBackPressed() }
        }

        setPriority()
        setDate()
        binding.doneBtn.setOnClickListener {
            assignWork()
        }

        return binding.root
    }

    private fun assignWork() {
        Utils.showDialog(requireContext())

        val workTitle = binding.titleEdt.text.toString()
        val workDesc = binding.workDesc.text.toString()
        val workLastDate = binding.dateTv.text.toString()

        if (workTitle.isEmpty()) {
            Utils.apply {
                showToast(requireContext(), "Please select work title")
            }
        } else if (workLastDate == "Last Date : ") {
            Utils.apply {
                hideDialog()
                showToast(requireContext(), "Please choose last date")
            }
        } else {
            val empId = employeeData.employeeDatail.userId
            val bossId = FirebaseAuth.getInstance().currentUser?.uid
            val workRoom = bossId + empId
            val randomId =
                (1..20).map { (('A'..'Z') + ('a'..'z') + ('0'..'0')).random() }.joinToString("")

            val work = Works(
                bossId = bossId,
                workId = randomId,
                workTitle = workTitle,
                workDesc = workDesc,
                workPriority = priority,
                workLastDate = workLastDate,
                workStatus = "1"
            )
            FirebaseDatabase.getInstance().getReference("Works")
                .child(workRoom).child(randomId).setValue(work)
                .addOnSuccessListener {
                    Utils.hideDialog()
                    Utils.showToast(
                        requireContext(),
                        "Work has been assigned to ${employeeData.employeeDatail.userName}"
                    )
                    val action =
                        AssignWorkFragmentDirections.actionAssignWorkFragmentToWorksFragment(
                            employeeData.employeeDatail
                        )
                    findNavController().navigate(action)
                }
        }
    }

    private fun setDate() {
        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONDAY, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLable(myCalender)
            }
        }
        binding.datePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                myCalender.get(Calendar.YEAR),
                myCalender.get(Calendar.MONTH),
                myCalender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateLable(myCalender: Calendar?) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.dateTv.text = sdf.format(myCalender!!.time)
    }

    private fun setPriority() {
        binding.apply {
            greenOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority : Low")
                priority = "1"
                binding.greenOval.setImageResource(R.drawable.ic_done)
                binding.yellowOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }

            yellowOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority : Medium")
                priority = "2"
                binding.yellowOval.setImageResource(R.drawable.ic_done)
                binding.greenOval.setImageResource(0)
                binding.redOval.setImageResource(0)
            }

            redOval.setOnClickListener {
                Utils.showToast(requireContext(), "Priority : High")
                priority = "3"
                binding.redOval.setImageResource(R.drawable.ic_done)
                binding.yellowOval.setImageResource(0)
                binding.greenOval.setImageResource(0)
            }
        }
    }

}