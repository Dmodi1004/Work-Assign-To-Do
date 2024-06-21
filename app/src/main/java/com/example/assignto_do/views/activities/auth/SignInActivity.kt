package com.example.assignto_do.views.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignto_do.R
import com.example.assignto_do.databinding.ActivitySignInBinding
import com.example.assignto_do.databinding.ForgorPasswordDialogBinding
import com.example.assignto_do.models.Users
import com.example.assignto_do.utils.Utils
import com.example.assignto_do.views.activities.BossMainActivity
import com.example.assignto_do.views.activities.EmployeeMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdt.text.toString()
            val password = binding.passwordEdt.text.toString()

            loginUser(email, password)
        }
        binding.signUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.forgotPasswordTv.setOnClickListener {
            showForgotPasswordDialog()
        }

    }

    private fun showForgotPasswordDialog() {
        val dialog = ForgorPasswordDialogBinding.inflate(LayoutInflater.from(this))
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialog.root)
            .create()
        alertDialog.show()

        dialog.emailEdt.requestFocus()
        dialog.forgotPasswordBtn.setOnClickListener {
            val email = dialog.emailEdt.text.toString()
            alertDialog.dismiss()
            resetPassword(email)
        }
        dialog.backToLoginTv.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun resetPassword(email: String) {
        lifecycleScope.launch {
            try {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                Utils.showToast(
                    this@SignInActivity,
                    "Please check your email and reset your password"
                )
            } catch (e: Exception) {
                Utils.showToast(this@SignInActivity, e.message.toString())
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        Utils.showDialog(this)
        val firebaseAuth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val currentUser = authResult.user?.uid
                val verifyEmail = firebaseAuth.currentUser?.isEmailVerified

                if (verifyEmail == true) {
                    if (currentUser != null) {
                        Utils.hideDialog()
                        FirebaseDatabase.getInstance().getReference("Users").child(currentUser)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val currentUserData = snapshot.getValue(Users::class.java)
                                    when (currentUserData?.userType) {
                                        "Boss" -> {
                                            startActivity(
                                                Intent(
                                                    this@SignInActivity,
                                                    BossMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        "Employee" -> {
                                            startActivity(
                                                Intent(
                                                    this@SignInActivity,
                                                    EmployeeMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        else -> {
                                            startActivity(
                                                Intent(
                                                    this@SignInActivity,
                                                    EmployeeMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Utils.hideDialog()
                                    Utils.showToast(this@SignInActivity, error.message)
                                }
                            })
                    } else {
                        Utils.hideDialog()
                        Utils.showToast(this@SignInActivity, "User Not Found, Please Register")
                    }
                } else {
                    Utils.hideDialog()
                    Utils.showToast(this@SignInActivity, "Email is not verified")
                }

            } catch (e: Exception) {
                Utils.hideDialog()
                Utils.showToast(this@SignInActivity, e.message.toString())
            }
        }
    }
}