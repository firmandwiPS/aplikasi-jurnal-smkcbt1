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

class UserAdminFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private var btnBack: ImageButton? = null
    private val adminList = mutableListOf<JSONObject>()

    private val urlTampil = "http://192.168.1.13/backend-app-jurnalcbt1/admin_user/data_user/user_admin/tampil_data_user_admin.php"
    private val urlTambah = "http://192.168.1.13/backend-app-jurnalcbt1/admin_user/data_user/user_admin/tambah_data_user_admin.php"
    private val urlUbah = "http://192.168.1.13/backend-app-jurnalcbt1/admin_user/data_user/user_admin/ubah_data_user_admin.php"
    private val urlHapus = "http://192.168.1.13/backend-app-jurnalcbt1/admin_user/data_user/user_admin/hapus_data_user_admin.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_user_admin, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAdmin)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AdminAdapter(adminList)

        fabAdd = view.findViewById(R.id.fabAdd)
        fabAdd.setOnClickListener {
            showFormDialog(null)
        }

        btnBack = view.findViewById(R.id.btnBack)
        btnBack?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        fetchData()
        return view
    }

    private fun fetchData() {
        val request = JsonObjectRequest(
            Request.Method.GET, urlTampil, null,
            { response ->
                if (response.optBoolean("success")) {
                    val dataArray = response.optJSONArray("data") ?: JSONArray()
                    adminList.clear()
                    for (i in 0 until dataArray.length()) {
                        adminList.add(dataArray.getJSONObject(i))
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
        val view = if (data == null) {
            layoutInflater.inflate(R.layout.dialog_tambah_admin, null)
        } else {
            layoutInflater.inflate(R.layout.dialog_ubah_admin, null)
        }

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

            val request = JsonObjectRequest(
                Request.Method.POST, urlToUse, params,
                { response ->
                    if (response.optBoolean("success")) {
                        Toast.makeText(
                            context,
                            if (data == null) "Data ditambahkan" else "Data diubah",
                            Toast.LENGTH_SHORT
                        ).show()
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

                val request = JsonObjectRequest(
                    Request.Method.POST, urlHapus, params,
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

    inner class AdminAdapter(private val items: List<JSONObject>) :
        RecyclerView.Adapter<AdminAdapter.AdminViewHolder>() {

        inner class AdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvNis: TextView = view.findViewById(R.id.tvNis)
            val tvKeyAkses: TextView = view.findViewById(R.id.tvKeyAkses)
            val tvLevel: TextView = view.findViewById(R.id.tvLevel)
            val btnEdit: Button = view.findViewById(R.id.btnEdit)
            val btnDelete: Button = view.findViewById(R.id.btnDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_admin_card, parent, false)
            return AdminViewHolder(view)
        }

        override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
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
