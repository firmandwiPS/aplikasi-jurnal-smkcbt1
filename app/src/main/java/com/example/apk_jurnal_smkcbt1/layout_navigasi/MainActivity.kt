package com.example.apk_jurnal_smkcbt1.layout_navigasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.admin.AdminDashboardFragment
import com.example.apk_jurnal_smkcbt1.admin.AdminJurnalFragment
import com.example.apk_jurnal_smkcbt1.admin.AdminProfileFragment
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

        bottomNavView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(AdminDashboardFragment())
                    true
                }
                R.id.nav_jurnal -> {
                    loadFragment(AdminJurnalFragment())
                    true
                }
                R.id.nav_profil -> {
                    loadFragment(AdminProfileFragment())
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