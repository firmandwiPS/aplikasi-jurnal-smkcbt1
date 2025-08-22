package com.example.apk_jurnal_smkcbt1.siswa.beranda

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.siswa.beranda.fitur.galeri.FiturGaleriFragment
import com.example.apk_jurnal_smkcbt1.siswa.beranda.fitur.jurnal.FiturJurnalFragment
import com.example.apk_jurnal_smkcbt1.siswa.beranda.fitur.kegiatan.FiturKegiatanFragment
import com.example.apk_jurnal_smkcbt1.siswa.beranda.fitur.laporan.FiturLaporanFragment
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class SiswaBerandaFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    // View dari layout
    private var tvDateTime: TextView? = null
    private var tvGreeting: TextView? = null
    private var tvUserName: TextView? = null
    private var tvUserClass: TextView? = null
    private var ivProfile: CircleImageView? = null

    // Fitur icons
    private var ivJurnal: ImageView? = null
    private var ivKegiatan: ImageView? = null
    private var ivLaporan: ImageView? = null
    private var ivGaleri: ImageView? = null

    // Handler untuk update jam realtime
    private val handler = Handler(Looper.getMainLooper())
    private val timeRunnable = object : Runnable {
        override fun run() {
            val currentDate =
                SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm:ss", Locale("id")).format(Date())
            tvDateTime?.text = currentDate
            handler.postDelayed(this, 1000) // update setiap 1 detik
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_siswa_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)

        // Inisialisasi view
        tvDateTime = view.findViewById(R.id.tv_date_time)
        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvUserName = view.findViewById(R.id.tv_user_name)
        tvUserClass = view.findViewById(R.id.tv_user_class)
        ivProfile = view.findViewById(R.id.iv_profile)

        ivJurnal = view.findViewById(R.id.iv_jurnal)
        ivKegiatan = view.findViewById(R.id.iv_kegiatan)
        ivLaporan = view.findViewById(R.id.iv_laporan)
        ivGaleri = view.findViewById(R.id.iv_galeri)

        // Mulai update jam berjalan
        handler.post(timeRunnable)

        // Ambil data dari API
        loadStudentData()

        // 🔹 Set klik listener untuk pindah ke fragment
        ivJurnal?.setOnClickListener { openFragment(FiturJurnalFragment()) }
        ivKegiatan?.setOnClickListener { openFragment(FiturKegiatanFragment()) }
        ivLaporan?.setOnClickListener { openFragment(FiturLaporanFragment()) }
        ivGaleri?.setOnClickListener { openFragment(FiturGaleriFragment()) }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, fragment)
            addToBackStack(null)
        }
    }

    private fun loadStudentData() {
        val nis = sharedPref.getString("nis", "") ?: ""
        if (nis.isEmpty()) {
            Toast.makeText(requireContext(), "NIS tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val url =
            "http://192.168.1.103/backend-app-jurnalcbt1/siswa_user/baranda_siswa/beranda_siswa.php?nis=$nis"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonObj = JSONObject(response)
                    if (jsonObj.getBoolean("success")) {
                        // Update UI
                        tvGreeting?.text = jsonObj.getString("greeting")
                        tvUserName?.text = jsonObj.getString("nama_lengkap")
                        tvUserClass?.text =
                            "${jsonObj.getString("kelas")} - ${jsonObj.getString("jurusan")}"

                        // Ambil foto profil
                        val fotoUrl = jsonObj.optString("foto_url", "")
                        if (fotoUrl.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(fotoUrl)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(ivProfile!!)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            jsonObj.getString("message"),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error parsing data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timeRunnable) // stop jam kalau fragment ditutup
    }

    companion object {
        fun newInstance() = SiswaBerandaFragment()
    }
}
