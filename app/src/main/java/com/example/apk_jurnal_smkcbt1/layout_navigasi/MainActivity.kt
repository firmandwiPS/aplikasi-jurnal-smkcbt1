package com.example.apk_jurnal_smkcbt1.layout_navigasi

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.admin.*
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
                R.id.nav_jurnal -> {
                    loadFragment(AdminJurnalFragment())
                    true
                }
                R.id.nav_absensi -> {
                    loadFragment(AdminAbsensiFragment())
                    true
                }
                R.id.nav_siswa -> {
                    loadFragment(AdminDataSiswaFragment())
                    true
                }
                R.id.nav_more -> {
                    showMoreMenu(bottomNavView)
                    false
                }
                else -> false
            }
        }
    }

    private fun showMoreMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(R.menu.menu_more_items_admin, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_laporan -> {
                    loadFragment(AdminLaporanFragment())
                    true
                }
                R.id.nav_verifikasi -> {
                    loadFragment(AdminVerifikasiFragment())
                    true
                }
                R.id.nav_guru -> {
                    loadFragment(AdminDataGuruFragment())
                    true
                }
                R.id.nav_perusahaan -> {
                    loadFragment(AdminPerusahaanFragment())
                    true
                }
                R.id.nav_user -> {
                    loadFragment(AdminUserFragment())
                    true
                }
                R.id.nav_setting -> {
                    loadFragment(AdminSettingFragment())
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
