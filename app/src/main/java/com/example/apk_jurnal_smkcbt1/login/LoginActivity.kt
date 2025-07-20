package com.example.apk_jurnal_smkcbt1.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.layout_navigasi.MainActivity
import com.example.apk_jurnal_smkcbt1.layout_navigasi.KepsekActivity
import com.example.apk_jurnal_smkcbt1.layout_navigasi.GuruActivity
import com.example.apk_jurnal_smkcbt1.layout_navigasi.OrtuActivity
import com.example.apk_jurnal_smkcbt1.layout_navigasi.SiswaActivity
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val user = username.text.toString().trim()
        val pass = password.text.toString().trim()

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.1.110/login-backand/login/login.php"  // Ganti dengan endpoint login kamu

        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            StringRequest@{ response ->
                try {
                    val json = JSONObject(response)
                    val success = json.getBoolean("success")
                    if (success) {
                        val level = json.getString("level")
                        val nama = json.getString("nama")

                        Toast.makeText(this, "Login Berhasil, Selamat Datang $nama", Toast.LENGTH_LONG).show()

                        val intent = when (level) {
                            "1" -> Intent(this, MainActivity::class.java)
                            "2" -> Intent(this, KepsekActivity::class.java)
                            "3" -> Intent(this, GuruActivity::class.java)
                            "4" -> Intent(this, OrtuActivity::class.java)
                            "5" -> Intent(this, SiswaActivity::class.java)
                            else -> {
                                Toast.makeText(this, "Role tidak dikenali", Toast.LENGTH_SHORT).show()
                                return@StringRequest
                            }
                        }

                        intent.putExtra("nama", nama)
                        intent.putExtra("level", level)
                        startActivity(intent)
                        finish()
                    } else {
                        val message = json.optString("message", "Login gagal")
                        Toast.makeText(this, "Login Gagal: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Terjadi kesalahan: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "username" to user,
                    "password" to pass
                )
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}
