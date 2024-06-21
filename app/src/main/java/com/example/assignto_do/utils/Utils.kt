package com.example.assignto_do.utils

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.example.assignto_do.R

object Utils {
    private var dialog: AlertDialog? = null

    fun showDialog(context: Context) {
        dialog = AlertDialog.Builder(context)
            .setView(R.layout.progress_dialog)
            .setCancelable(false)
            .create()
        dialog!!.show()
    }

    fun hideDialog() {
        dialog?.dismiss()
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}