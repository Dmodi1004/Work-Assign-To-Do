package com.example.assignto_do.views.activities.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.assignto_do.R
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


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = FirebaseAuth.getInstance().currentUser?.uid

            if (currentUser != null){
                lifecycleScope.launch {
                    try {
                        FirebaseDatabase.getInstance().getReference("Users").child(currentUser)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val currentUserData = snapshot.getValue(Users::class.java)
                                    when (currentUserData?.userType) {
                                        "Boss" -> {
                                            startActivity(
                                                Intent(
                                                    this@SplashScreen,
                                                    BossMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        "Employee" -> {
                                            startActivity(
                                                Intent(
                                                    this@SplashScreen,
                                                    EmployeeMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }

                                        else -> {
                                            startActivity(
                                                Intent(
                                                    this@SplashScreen,
                                                    EmployeeMainActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Utils.hideDialog()
                                    Utils.showToast(this@SplashScreen, error.message)
                                }
                            })
                    } catch (e: Exception){
                        Utils.showToast(this@SplashScreen, e.message.toString())
                    }
                }
            } else{
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }, 2000)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

    }
}