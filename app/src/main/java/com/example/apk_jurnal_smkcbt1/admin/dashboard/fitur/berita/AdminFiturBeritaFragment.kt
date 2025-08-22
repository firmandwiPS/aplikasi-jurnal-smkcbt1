package com.example.apk_jurnal_smkcbt1.admin.dashboard.fitur.berita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.apk_jurnal_smkcbt1.R

class AdminFiturBeritaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val beritaList = mutableListOf<Berita>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_fitur_berita, container, false)

        val btnTambah = view.findViewById<Button>(R.id.btnTambahBerita)
        recyclerView = view.findViewById(R.id.recyclerBerita)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        btnTambah.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminFiturTambahBeritaFragment())
                .addToBackStack(null)
                .commit()
        }

        loadBerita()
        return view
    }

    private fun loadBerita() {
        val url =
            "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/dashboard/fitur/admin_berita/admin_tampil_berita.php"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.getString("status") == "success") {
                    beritaList.clear()
                    val data = response.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        val obj = data.getJSONObject(i)
                        val gambarArray = obj.optJSONArray("gambar")
                        val gambarList = mutableListOf<String>()
                        if (gambarArray != null) {
                            for (j in 0 until gambarArray.length()) {
                                gambarList.add(gambarArray.getString(j))
                            }
                        }
                        val berita = Berita(
                            obj.getString("id_berita"),
                            obj.getString("judul_singkat"),
                            obj.getString("judul_lengkap"),
                            obj.getString("isi"),
                            obj.getString("kategori"),
                            gambarList,
                            obj.getString("tanggal")
                        )
                        beritaList.add(berita)
                    }
                    recyclerView.adapter = BeritaAdapter(beritaList)
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Gagal load: ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }

    data class Berita(
        val id_berita: String,
        val judul_singkat: String,
        val judul_lengkap: String,
        val isi: String,
        val kategori: String,
        val gambar: List<String>?,
        val tanggal: String
    )

    inner class BeritaAdapter(private val beritaList: List<Berita>) :
        RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

        inner class BeritaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvJudul: TextView = itemView.findViewById(R.id.tvJudulSingkat)
            val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
            val ivGambar: ImageView = itemView.findViewById(R.id.ivGambar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_berita, parent, false)
            return BeritaViewHolder(view)
        }

        override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
            val berita = beritaList[position]
            holder.tvJudul.text = berita.judul_singkat
            holder.tvTanggal.text = berita.tanggal

            if (!berita.gambar.isNullOrEmpty()) {
                Glide.with(holder.itemView.context)
                    .load("http://192.168.1.103/${berita.gambar[0]}")
                    .into(holder.ivGambar)
            } else {
                holder.ivGambar.setImageResource(R.drawable.ic_person)
            }
        }

        override fun getItemCount(): Int = beritaList.size
    }
}