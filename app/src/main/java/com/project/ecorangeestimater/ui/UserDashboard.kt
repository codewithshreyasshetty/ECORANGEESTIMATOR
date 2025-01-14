package com.project.ecorangeestimater.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.ecorangeestimater.databinding.ActivityUserDashboardBinding
import com.project.ecorangeestimater.utils.SessionManager


class UserDashboard : AppCompatActivity() {
    private val bind by lazy { ActivityUserDashboardBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        val originalUserName = shared.getUserName()!!
        val userName = if (originalUserName.isNotEmpty()) {
            (originalUserName[0].uppercaseChar()) + originalUserName.substring(1)
        } else {
            originalUserName
        }

        val coloredUserName = "<font color='#FF5722'>$userName</font>"
        bind.usetTxt.text = Html.fromHtml("Welcome<br>$coloredUserName")
        bind.logout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Confirm") { _, _ ->
                    shared.clearLoginState()
                    finishAffinity()
                    startActivity(Intent(this@UserDashboard, LoginActivity::class.java))
                }
                .setNegativeButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        bind.Profile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        bind.ViewStations.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }


    }
}