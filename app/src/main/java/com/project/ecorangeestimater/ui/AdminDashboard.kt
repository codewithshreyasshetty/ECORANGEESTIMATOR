package com.project.ecorangeestimater.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.ecorangeestimater.databinding.ActivityAdminDashboardBinding
import com.project.ecorangeestimater.utils.SessionManager


class AdminDashboard : AppCompatActivity() {
    private val bind by lazy { ActivityAdminDashboardBinding.inflate(layoutInflater) }
    private val shared by lazy { SessionManager(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        bind.logout.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Confirm") { _, _ ->
                    shared.clearLoginState()
                    finishAffinity()
                    startActivity(Intent(this@AdminDashboard, LoginActivity::class.java))
                }
                .setNegativeButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

        bind.addsellers.setOnClickListener {
            startActivity(Intent(this, AddStationActivity::class.java))
        }

        bind.viewSeller.setOnClickListener {
            startActivity(Intent(this, ViewList::class.java))
        }


    }
}