package com.example.apk_jurnal_smkcbt1.layout_navigasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.admin.*
import com.example.apk_jurnal_smkcbt1.admin.dashboard.AdminDashboardFragment

import com.example.apk_jurnal_smkcbt1.admin.siswa.AdminDataSiswaFragment
import com.example.apk_jurnal_smkcbt1.admin.user.AdminDataUserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavView = findViewById(R.id.bottom_nav_view)

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(AdminDashboardFragment())
            bottomNavView.selectedItemId = R.id.nav_dashboard
        }

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(AdminDashboardFragment())
                    true
                }
                R.id.nav_data_siswa -> {
                    loadFragment(AdminDataSiswaFragment())
                    true
                }
                R.id.nav_data_user -> {
                    loadFragment(AdminDataUserFragment())
                    true
                }
                R.id.nav_data_pembimbing -> {
                    loadFragment(AdminDataGuruFragment())
                    true
                }
                R.id.nav_data_perusahaan -> {
                    loadFragment(AdminDataPerusahaanFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
