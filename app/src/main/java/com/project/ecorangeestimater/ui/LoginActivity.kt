package com.project.ecorangeestimater.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.project.ecorangeestimater.R
import com.project.ecorangeestimater.databinding.ActivityLoginBinding
import com.project.ecorangeestimater.response.CommonResponse
import com.project.ecorangeestimater.response.RetrofitInstance
import com.project.ecorangeestimater.utils.SessionManager
import com.project.ecorangeestimater.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val bind by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        checkPermissions()
        if (shared.isLoggedIn()) { shared.getUserRole()?.let { navigateToDashboard(it) } }

        bind.signupText.setOnClickListener {
            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
        }


        bind.loginButton.setOnClickListener {
            val email = bind.email.text.toString().trim()
            val password = bind.password.text.toString().trim()

            if (email.isEmpty()) {
                showToast("Please enter your email")
            } else if (password.isEmpty()) {
                showToast("Please enter your password")
            } else {
                if (email == "admin" && password == "admin") {
                    shared.saveLoginState("-1", "Admin", "", "", "", "", "", "")
                    navigateToDashboard("Admin")
                    finish()
                } else {
                    bind.progressBar.isVisible = true
                    CoroutineScope(IO).launch {
                        RetrofitInstance.instance.userLogin(email, password,"UserEcoRange")
                            .enqueue(object : Callback<CommonResponse?> {
                                override fun onResponse(
                                    call: Call<CommonResponse?>,
                                    response: Response<CommonResponse?>
                                ) {
                                    val loginResponse = response.body()!!
                                    if (!loginResponse.error) {
                                        loginResponse.data.firstOrNull()?.let { user ->
                                            shared.saveLoginState(
                                                id = "${user.id}",
                                                role = user.role,
                                                name = user.name,
                                                location = user.location,
                                                mobile = user.mobile,
                                                email = user.email,
                                                password = user.password,
                                                rating = user.type
                                            )
                                            navigateToDashboard(user.role)
                                            runOnUiThread {
                                                showToast("Login Successful")
                                            }

                                        }
                                    } else {
                                        showToast("Invalid credentials")
                                    }
                                    bind.progressBar.isVisible = false
                                }


                                override fun onFailure(call: Call<CommonResponse?>, t: Throwable) {
                                    showToast(t.message ?: "Login failed")
                                    bind.progressBar.isVisible = false
                                }
                            })
                    }

                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun checkPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.CALL_PHONE
        )

        val permissionsNotGranted = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNotGranted.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(permissionsNotGranted.toTypedArray(), 100)
            } else {
                ActivityCompat.requestPermissions(this, permissionsNotGranted.toTypedArray(), 100)
            }
        }
    }


    private fun navigateToDashboard(role: String) {
        val intent = when (role) {
            "UserEcoRange" -> Intent(this, UserDashboard::class.java)
            "Admin" -> Intent(this, AdminDashboard::class.java)
            else -> Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }

}
