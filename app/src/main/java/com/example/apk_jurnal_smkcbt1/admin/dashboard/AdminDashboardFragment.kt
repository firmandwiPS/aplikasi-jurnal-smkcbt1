package com.example.apk_jurnal_smkcbt1.admin.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.admin.dashboard.fitur.berita.AdminFiturBeritaFragment
import java.text.SimpleDateFormat
import java.util.*

class AdminDashboardFragment : Fragment() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvDateTime: TextView
    private lateinit var ivBerita: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 1000 // 1 detik

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvDateTime = view.findViewById(R.id.tv_date_time)
        ivBerita = view.findViewById(R.id.iv_berita)

        startClock()
        setupClickListeners()

        return view
    }

    private fun setupClickListeners() {
        ivBerita.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminFiturBeritaFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun startClock() {
        handler.post(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale("id", "ID"))
                val currentDateTime = sdf.format(calendar.time)

                tvDateTime.text = currentDateTime
                tvGreeting.text = getGreeting(calendar.get(Calendar.HOUR_OF_DAY))

                handler.postDelayed(this, updateInterval)
            }
        })
    }

    private fun getGreeting(hour: Int): String {
        return when (hour) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..17 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}
