package com.example.apk_jurnal_smkcbt1.admin.user


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.admin.user.user_siswa.UserSiswaFragment
import android.widget.Toast
import com.example.apk_jurnal_smkcbt1.admin.user.user_siswa.UserAdminFragment

class AdminDataUserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_data_user, container, false)

        val cardAdmin = view.findViewById<CardView>(R.id.card_admin)
        val cardSiswa = view.findViewById<CardView>(R.id.card_siswa)
        val cardGuru = view.findViewById<CardView>(R.id.card_guru)
        val cardOrtu = view.findViewById<CardView>(R.id.card_ortu)
        val cardKebsek = view.findViewById<CardView>(R.id.card_kebsek)

        // Admin
        cardAdmin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserAdminFragment())
                .addToBackStack(null)
                .commit()
        }

        // Siswa - Navigasi ke Fragment UserSiwaFragment
        cardSiswa.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, UserSiswaFragment())
                .addToBackStack(null)
                .commit()
        }

        // Guru
        cardGuru.setOnClickListener {
            Toast.makeText(context, "Guru clicked", Toast.LENGTH_SHORT).show()
        }

        // Orang Tua
        cardOrtu.setOnClickListener {
            Toast.makeText(context, "Orang Tua clicked", Toast.LENGTH_SHORT).show()
        }

        // Kepala Sekolah
        cardKebsek.setOnClickListener {
            Toast.makeText(context, "Kepala Sekolah clicked", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
