package com.example.apk_jurnal_smkcbt1.layout_navigasi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.siswa.*
import com.example.apk_jurnal_smkcbt1.siswa.beranda.SiswaBerandaFragment
import com.example.apk_jurnal_smkcbt1.siswa.biodata.SiswaBiodataFragment
import com.example.apk_jurnal_smkcbt1.siswa.prasensi.SiswaPrasensiFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class SiswaActivity : AppCompatActivity() {

    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siswa)

        bottomNavView = findViewById(R.id.bottom_nav_view)

        // Default fragment saat dibuka
        if (savedInstanceState == null) {
            loadFragment(SiswaBerandaFragment())
            bottomNavView.selectedItemId = R.id.nav_dashboard
        }

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    loadFragment(SiswaBerandaFragment())
                    true
                }
                R.id.nav_jurnal -> {
                    loadFragment(SiswaPrasensiFragment())
                    true
                }
                R.id.nav_biodata -> {
                    loadFragment(SiswaBiodataFragment())
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
