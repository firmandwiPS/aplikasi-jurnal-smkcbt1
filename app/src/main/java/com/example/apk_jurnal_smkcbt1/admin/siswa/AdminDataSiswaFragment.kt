package com.example.apk_jurnal_smkcbt1.admin.siswa

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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.apk_jurnal_smkcbt1.R
import org.json.JSONArray
import org.json.JSONObject

class AdminDataSiswaFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var siswaAdapter: SiswaAdapter
    private val siswaList = mutableListOf<SiswaModel>()

    private val URL_DATA =
        "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/data_siswa_admin/data_siswa.php"
    private val URL_HAPUS =
        "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/data_siswa_admin/hapus_data_siswa.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_data_siswa, container, false)

        val btnTambah: Button = view.findViewById(R.id.btnTambahSiswa)
        recyclerView = view.findViewById(R.id.recyclerViewSiswa)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        siswaAdapter = SiswaAdapter(
            siswaList,
            onItemClick = { nis ->
                val detailFragment = AdminDetailDataSiswaFragment()
                val bundle = Bundle()
                bundle.putString("nis", nis)
                detailFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onUbahClick = { nis ->
                val ubahFragment = AdminUbahDataSiswaFragment() // ← Ganti ke fragment ubah
                val bundle = Bundle()
                bundle.putString("nis", nis) // Kirim NIS ke halaman ubah
                ubahFragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ubahFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onHapusClick = { nis ->
                hapusData(nis)
            }
        )
        recyclerView.adapter = siswaAdapter

        btnTambah.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminTambahDataSiswaFragment())
                .addToBackStack(null)
                .commit()
        }

        loadData()
        return view
    }

    private fun loadData() {
        val request = JsonObjectRequest(URL_DATA,
            { response ->
                if (response.getBoolean("success")) {
                    siswaList.clear()
                    val dataArray: JSONArray = response.getJSONArray("data")
                    for (i in 0 until dataArray.length()) {
                        val obj: JSONObject = dataArray.getJSONObject(i)
                        siswaList.add(
                            SiswaModel(
                                nis = obj.getString("nis"),
                                namaLengkap = obj.getString("nama_lengkap"),
                                foto = obj.optString("foto", ""),
                                tempatPkl = obj.optString("tempat_pkl", "-")
                            )
                        )
                    }
                    siswaAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun hapusData(nis: String) {
        val request = object : StringRequest(Method.POST, URL_HAPUS,
            { _ ->
                Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadData()
            },
            { error ->
                Toast.makeText(requireContext(), "Gagal menghapus: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nis"] = nis
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(request)
    }

    data class SiswaModel(
        val nis: String,
        val namaLengkap: String,
        val foto: String,
        val tempatPkl: String
    )

    class SiswaAdapter(
        private val siswaList: List<SiswaModel>,
        private val onItemClick: (String) -> Unit,
        private val onUbahClick: (String) -> Unit,
        private val onHapusClick: (String) -> Unit
    ) : RecyclerView.Adapter<SiswaAdapter.SiswaViewHolder>() {

        inner class SiswaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imgFoto: ImageView = itemView.findViewById(R.id.imgFotoSiswa)
            val txtNama: TextView = itemView.findViewById(R.id.txtNamaSiswa)
            val txtNis: TextView = itemView.findViewById(R.id.txtNisSiswa)
            val txtTempatPkl: TextView = itemView.findViewById(R.id.txtTempatPkl)
            val btnUbah: Button = itemView.findViewById(R.id.btnUbah)
            val btnHapus: Button = itemView.findViewById(R.id.btnHapus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiswaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_admin_data_siswa_card, parent, false)
            return SiswaViewHolder(view)
        }

        override fun onBindViewHolder(holder: SiswaViewHolder, position: Int) {
            val siswa = siswaList[position]
            holder.txtNama.text = siswa.namaLengkap
            holder.txtNis.text = "NIS: ${siswa.nis}"
            holder.txtTempatPkl.text = "Perusahaan: ${siswa.tempatPkl}"

            val fotoUrl = if (siswa.foto.isNotEmpty())
                "http://192.168.1.103/backend-app-jurnalcbt1/uploads/images_siswa/${siswa.foto}"
            else
                "http://192.168.1.103/backend-app-jurnalcbt1/uploads/images_siswa/default.jpg"

            Glide.with(holder.itemView.context)
                .load(fotoUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.imgFoto)

            holder.itemView.setOnClickListener {
                onItemClick(siswa.nis)
            }
            holder.btnUbah.setOnClickListener {
                onUbahClick(siswa.nis)
            }
            holder.btnHapus.setOnClickListener {
                onHapusClick(siswa.nis)
            }
        }

        override fun getItemCount(): Int = siswaList.size
    }
}
