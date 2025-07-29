package com.example.apk_jurnal_smkcbt1.admin.user.user_siswa

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject

class UserAdminFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private val adminList = mutableListOf<JSONObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_admin, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAdmin)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fabAdd = view.findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener {
            showBottomSheetDialog()
        }

        recyclerView.adapter = AdminAdapter(adminList)
        fetchData()

        return view
    }

    private fun fetchData() {
        val url = "http://192.168.1.106/backend-app-jurnalcbt1/admin_user/data_user_admin/user_admin/tampil_data_user_admin.php"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getBoolean("success")) {
                        val dataArray: JSONArray = response.getJSONArray("data")
                        adminList.clear()
                        for (i in 0 until dataArray.length()) {
                            adminList.add(dataArray.getJSONObject(i))
                        }
                        recyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Parsing error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(context, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_tambah_admin, null)

        val etNis = view.findViewById<EditText>(R.id.etNis)
        val etKeyAkses = view.findViewById<EditText>(R.id.etKeyAkses)
        val etLevel = view.findViewById<EditText>(R.id.etLevel) // Tambahkan field level di layout XML

        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnTutup = view.findViewById<Button>(R.id.btnTutup)

        btnSimpan.setOnClickListener {
            val nis = etNis.text.toString().trim()
            val keyAkses = etKeyAkses.text.toString().trim()
            val level = etLevel.text.toString().trim()

            if (nis.isEmpty() || keyAkses.isEmpty() || level.isEmpty()) {
                Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val urlTambah = "http://192.168.1.106/backend-app-jurnalcbt1/admin_user/data_user_admin/user_admin/tambah_data_user_admin.php"

            val params = HashMap<String, String>()
            params["nis"] = nis
            params["key_akses"] = keyAkses
            params["level"] = level

            val jsonObject = JSONObject(params as Map<*, *>)
            val requestQueue = Volley.newRequestQueue(requireContext())

            val request = JsonObjectRequest(
                Request.Method.POST, urlTambah, jsonObject,
                { response ->
                    if (response.getBoolean("success")) {
                        Toast.makeText(context, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        fetchData()
                    } else {
                        Toast.makeText(context, response.optString("message"), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(context, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )

            requestQueue.add(request)
        }

        btnTutup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }


    inner class AdminAdapter(private val items: List<JSONObject>) :
        RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

        inner class AdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNis: TextView
            val tvKeyAkses: TextView
            val tvLevel: TextView

            init {
                val layout = view as LinearLayout
                tvNis = layout.getChildAt(0) as TextView
                tvKeyAkses = layout.getChildAt(1) as TextView
                tvLevel = layout.getChildAt(2) as TextView
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
            val context = parent.context

            val layout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 32, 32, 32)
                setBackgroundColor(0xFFFFFFFF.toInt())
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 16, 16, 16)
                }
            }

            val tvNis = TextView(context).apply {
                textSize = 18f
                setTextColor(0xFF000000.toInt())
            }

            val tvKeyAkses = TextView(context).apply {
                textSize = 16f
                setTextColor(0xFF222222.toInt())
            }

            val tvLevel = TextView(context).apply {
                textSize = 16f
                setTextColor(0xFF444444.toInt())
            }

            layout.addView(tvNis)
            layout.addView(tvKeyAkses)
            layout.addView(tvLevel)

            return AdminViewHolder(layout)
        }

        override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
            val obj = items[position]
            holder.tvNis.text = "NIS: ${obj.optString("nis")}"
            holder.tvKeyAkses.text = "Key Akses: ${obj.optString("key_akses")}"
            holder.tvLevel.text = "Level: ${obj.optString("level")}"
        }

        override fun getItemCount(): Int = items.size
    }
}
