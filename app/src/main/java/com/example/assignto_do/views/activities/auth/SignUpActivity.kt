package com.example.assignto_do.views.activities.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignto_do.R
import com.example.assignto_do.databinding.AccountDialogBinding
import com.example.assignto_do.databinding.ActivitySignUpBinding
import com.example.assignto_do.models.Users
import com.example.assignto_do.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpActivity : AppCompatActivity() {

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private var userImageUri: Uri? = null
    private var userType: String = ""

    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        userImageUri = it
        binding.userImageIv.setImageURI(userImageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            userImageIv.setOnClickListener {
                selectImage.launch("image/*")
            }

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                userType = findViewById<RadioButton>(checkedId).text.toString()
                Log.d("TAG", "useType: $userType")
            }

            registerBtn.setOnClickListener {
                createUser()
            }

            signInTv.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            }
        }

    }

    private fun createUser() {
        Utils.showDialog(this)

        val name = binding.nameEdt.text.toString()
        val email = binding.emailEdt.text.toString()
        val password = binding.passwordEdt.text.toString()
        val confirmPassword = binding.confirmPasswordEdt.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (userImageUri == null) {
                Utils.showToast(this, "Please select profile image")
                Utils.hideDialog()
            } else if (password == confirmPassword) {
                if (userType != "") {
                    uploadImageUri(name, email, password)
                } else {
                    Utils.hideDialog()
                    Utils.showToast(this, "Select User Type")
                }
            } else {
                Utils.hideDialog()
                Utils.showToast(this, "Passwords are not matching")
            }
        } else {
            Utils.hideDialog()
            Utils.showToast(this, "Please fill all this fields")
        }

    }

    private fun uploadImageUri(name: String, email: String, password: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val storageReference =
            FirebaseStorage.getInstance().getReference("Profile").child(currentUserId)
                .child("Profile.jpg")

        lifecycleScope.launch {
            try {
                val uploadTask = storageReference.putFile(userImageUri!!).await()

                if (uploadTask.task.isSuccessful) {
                    val downloadURL = storageReference.downloadUrl.await()
                    saveUserData(name, email, password, downloadURL)
                } else {
                    Utils.hideDialog()
                    showToast("Upload failed: ${uploadTask.task.exception?.message}")
                }
            } catch (e: Exception) {
                Utils.hideDialog()
                showToast("Upload failed: ${e.message}")
            }
        }
    }

    private fun saveUserData(name: String, email: String, password: String, downloadURL: Uri?) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) return@OnCompleteListener

            val tokens = task.result

            lifecycleScope.launch {
                val db = FirebaseDatabase.getInstance().getReference("Users")
                try {
                    val firebaseAuth =
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .await()

                    if (firebaseAuth.user != null) {

                        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                val dialog =
                                    AccountDialogBinding.inflate(LayoutInflater.from(this@SignUpActivity))
                                val alertDialog = AlertDialog.Builder(this@SignUpActivity)
                                    .setView(dialog.root)
                                    .create()
                                Utils.hideDialog()
                                alertDialog.show()
                                dialog.okBtn.setOnClickListener {
                                    alertDialog.dismiss()
                                    startActivity(
                                        Intent(
                                            this@SignUpActivity,
                                            SignInActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }

                        val uid = firebaseAuth.user?.uid.toString()
                        val saveUserType = if (userType == "Boss") "Boss" else "Employee"
                        val users =
                            Users(
                                userType = saveUserType,
                                userId = uid,
                                userName = name,
                                userEmail = email,
                                userPassword = password,
                                userImage = downloadURL.toString(),
                                userToken = tokens
                            )
                        db.child(uid).setValue(users).await()
                    } else {
                        Utils.hideDialog()
                        Utils.showToast(this@SignUpActivity, "Signed Up Failed")
                    }
                } catch (e: Exception) {
                    Utils.hideDialog()
                    Utils.showToast(this@SignUpActivity, e.message.toString())
                }
            }

        })
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Utils.showToast(this, message)
        }
    }
}