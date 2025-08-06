package com.example.apk_jurnal_smkcbt1.admin.user.user_siswa

import android.app.AlertDialog
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

class UserSiswaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private val siswaList = mutableListOf<JSONObject>()

    private val urlTampil = "http://192.168.130.91/backend-app-jurnalcbt1/admin_user/data_user/user_siswa/tampil_data_user_siswa.php"
    private val urlTambah = "http://192.168.130.91/backend-app-jurnalcbt1/admin_user/data_user/user_siswa/tambah_data_user_siswa.php"
    private val urlUbah = "http://192.168.130.91/backend-app-jurnalcbt1/admin_user/data_user/user_siswa/ubah_data_user_siswa.php"
    private val urlHapus = "http://192.168.130.91/backend-app-jurnalcbt1/admin_user/data_user/user_siswa/hapus_data_user_siswa.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_user_siswa, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewSiswa)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = SiswaAdapter(siswaList)

        fabAdd = view.findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener {
            showFormDialog(null)
        }

        fetchData()
        return view
    }

    private fun fetchData() {
        val request = JsonObjectRequest(Request.Method.GET, urlTampil, null,
            { response ->
                if (response.optBoolean("success")) {
                    val dataArray = response.optJSONArray("data") ?: JSONArray()
                    siswaList.clear()
                    for (i in 0 until dataArray.length()) {
                        siswaList.add(dataArray.getJSONObject(i))
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(context, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun showFormDialog(data: JSONObject?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = if (data == null)
            layoutInflater.inflate(R.layout.dialog_tambah_siswa, null)
        else
            layoutInflater.inflate(R.layout.dialog_ubah_siswa, null)

        val etNis: EditText = view.findViewById(R.id.etNis)
        val etKeyAkses: EditText = view.findViewById(R.id.etKeyAkses)
        val etLevel: EditText = view.findViewById(R.id.etLevel)
        val btnSimpan: Button = view.findViewById(R.id.btnSimpan)
        val btnTutup: Button = view.findViewById(R.id.btnTutup)

        data?.let {
            etNis.setText(it.optString("nis"))
            etKeyAkses.setText(it.optString("key_akses"))
            etLevel.setText(it.optString("level"))
            btnSimpan.text = "Ubah"
        }

        btnSimpan.setOnClickListener {
            val nis = etNis.text.toString().trim()
            val keyAkses = etKeyAkses.text.toString().trim()
            val level = etLevel.text.toString().trim()

            if (nis.isEmpty() || keyAkses.isEmpty() || level.isEmpty()) {
                Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val params = JSONObject().apply {
                put("nis", nis)
                put("key_akses", keyAkses)
                put("level", level)
                data?.let { put("id", it.optString("id")) }
            }

            val urlToUse = if (data == null) urlTambah else urlUbah

            val request = JsonObjectRequest(Request.Method.POST, urlToUse, params,
                { response ->
                    if (response.optBoolean("success")) {
                        Toast.makeText(context, if (data == null) "Data ditambahkan" else "Data diubah", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        fetchData()
                    } else {
                        Toast.makeText(context, response.optString("message"), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(context, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
                })

            Volley.newRequestQueue(requireContext()).add(request)
        }

        btnTutup.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showDeleteConfirmation(data: JSONObject) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Hapus")
            .setMessage("Yakin ingin menghapus data ini?")
            .setPositiveButton("Ya") { _, _ ->
                val params = JSONObject().apply {
                    put("id", data.optString("id"))
                }

                val request = JsonObjectRequest(Request.Method.POST, urlHapus, params,
                    { response ->
                        if (response.optBoolean("success")) {
                            Toast.makeText(context, "Data dihapus", Toast.LENGTH_SHORT).show()
                            fetchData()
                        } else {
                            Toast.makeText(context, response.optString("message"), Toast.LENGTH_SHORT).show()
                        }
                    },
                    { error ->
                        Toast.makeText(context, "Gagal koneksi: ${error.message}", Toast.LENGTH_SHORT).show()
                    })

                Volley.newRequestQueue(requireContext()).add(request)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    inner class SiswaAdapter(private val items: List<JSONObject>) : RecyclerView.Adapter<SiswaAdapter.SiswaViewHolder>() {
        inner class SiswaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNis: TextView = view.findViewById(R.id.tvNis)
            val tvKeyAkses: TextView = view.findViewById(R.id.tvKeyAkses)
            val tvLevel: TextView = view.findViewById(R.id.tvLevel)
            val btnEdit: Button = view.findViewById(R.id.btnUbah)
            val btnDelete: Button = view.findViewById(R.id.btnHapus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiswaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_siswa_card, parent, false)
            return SiswaViewHolder(view)
        }

        override fun onBindViewHolder(holder: SiswaViewHolder, position: Int) {
            val obj = items[position]
            holder.tvNis.text = "NIS: ${obj.optString("nis")}"
            holder.tvKeyAkses.text = "Key Akses: ${obj.optString("key_akses")}"
            holder.tvLevel.text = "Level: ${obj.optString("level")}"

            holder.btnEdit.setOnClickListener {
                showFormDialog(obj)
            }

            holder.btnDelete.setOnClickListener {
                showDeleteConfirmation(obj)
            }
        }

        override fun getItemCount(): Int = items.size
    }
}
