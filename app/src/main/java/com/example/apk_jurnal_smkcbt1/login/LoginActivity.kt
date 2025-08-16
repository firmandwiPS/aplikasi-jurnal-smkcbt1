package com.example.apk_jurnal_smkcbt1.login

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.layout_navigasi.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var keyAkses: EditText
    private lateinit var btnLogin: Button
    private lateinit var tanggalText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        keyAkses = findViewById(R.id.keyakses)
        btnLogin = findViewById(R.id.btnLogin)
        tanggalText = findViewById(R.id.tanggalSekarang)

        // Tampilkan tanggal sekarang dalam format lokal Indonesia
        val currentDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id")).format(Date())
        tanggalText.text = currentDate

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val key = keyAkses.text.toString().trim()
        if (key.isEmpty()) {
            Toast.makeText(this, "Key Akses wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.103/backend-app-jurnalcbt1/login/login.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            StringRequest@{ response ->
                try {
                    val json = JSONObject(response)
                    val success = json.getBoolean("success")

                    if (success) {
                        val level = json.getString("level")
                        val nis = json.getString("nis")

                        // ✅ Simpan ke SharedPreferences
                        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                        sharedPref.edit()
                            .putString("nis", nis)
                            .putString("level", level)
                            .apply()

                        Toast.makeText(this, "Login Berhasil. NIS: $nis", Toast.LENGTH_LONG).show()

                        // Navigasi berdasarkan level
                        val intent = when (level) {
                            "1" -> Intent(this, MainActivity::class.java)
                            "2" -> Intent(this, KepsekActivity::class.java)
                            "3" -> Intent(this, GuruActivity::class.java)
                            "4" -> Intent(this, OrtuActivity::class.java)
                            "5" -> Intent(this, SiswaActivity::class.java)
                            else -> {
                                Toast.makeText(this, "Level tidak dikenali", Toast.LENGTH_SHORT).show()
                                return@StringRequest
                            }
                        }

                        startActivity(intent)
                        finish()
                    } else {
                        val msg = json.optString("message", "Login gagal")
                        Toast.makeText(this, "Login Gagal: $msg", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Kesalahan jaringan: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf("key_akses" to key)
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}
