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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.apk_jurnal_smkcbt1.R

class AdminDetailDataSiswaFragment : Fragment() {

    private lateinit var imgFoto: ImageView
    private lateinit var txtNama: TextView
    private lateinit var txtNis: TextView
    private lateinit var txtJK: TextView
    private lateinit var txtKelas: TextView
    private lateinit var txtJurusan: TextView
    private lateinit var txtTempatPkl: TextView
    private lateinit var txtMulai: TextView
    private lateinit var txtSelesai: TextView
    private lateinit var txtPembimbingPerusahaan: TextView
    private lateinit var txtNoHpPerusahaan: TextView
    private lateinit var txtPembimbingSekolah: TextView
    private lateinit var txtNoHpSekolah: TextView
    private lateinit var btnKembali: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_detail_data_siswa, container, false)

        // Pastikan ID sesuai dengan XML
        imgFoto = view.findViewById(R.id.imgFotoDetail)
        txtNama = view.findViewById(R.id.txtNamaDetail)
        txtNis = view.findViewById(R.id.txtNisDetail)
        txtJK = view.findViewById(R.id.txtJenisKelaminDetail)
        txtKelas = view.findViewById(R.id.txtKelasDetail)
        txtJurusan = view.findViewById(R.id.txtJurusanDetail)
        txtTempatPkl = view.findViewById(R.id.txtTempatPklDetail)
        txtMulai = view.findViewById(R.id.txtMulaiPklDetail)
        txtSelesai = view.findViewById(R.id.txtSelesaiPklDetail)
        txtPembimbingPerusahaan = view.findViewById(R.id.txtPembimbingPerusahaanDetail)
        txtNoHpPerusahaan = view.findViewById(R.id.txtNoHpPembimbingPerusahaanDetail)
        txtPembimbingSekolah = view.findViewById(R.id.txtPembimbingSekolahDetail)
        txtNoHpSekolah = view.findViewById(R.id.txtNoHpPembimbingSekolahDetail)
        btnKembali = view.findViewById(R.id.btnKembaliDetail)

        val nis = arguments?.getString("nis") ?: ""
        if (nis.isNotEmpty()) {
            loadDetail(nis)
        } else {
            Toast.makeText(requireContext(), "NIS tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        btnKembali.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadDetail(nis: String) {
        val url = "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/data_siswa_admin/detail_data_siswa.php?nis=$nis"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                if (response.getBoolean("success")) {
                    val data = response.getJSONObject("data")

                    txtNama.text = data.getString("nama_lengkap")
                    txtNis.text = "NIS: ${data.getString("nis")}"
                    txtJK.text = "Jenis Kelamin: ${data.getString("jenis_kelamin")}"
                    txtKelas.text = "Kelas: ${data.getString("kelas")}"
                    txtJurusan.text = "Jurusan: ${data.getString("jurusan")}"
                    txtTempatPkl.text = "Tempat PKL: ${data.getString("tempat_pkl")}"
                    txtMulai.text = "Mulai PKL: ${data.getString("mulai_pkl")}"
                    txtSelesai.text = "Selesai PKL: ${data.getString("selesai_pkl")}"
                    txtPembimbingPerusahaan.text = "Pembimbing Perusahaan: ${data.getString("pembimbing_perusahaan")}"
                    txtNoHpPerusahaan.text = "No HP: ${data.getString("no_hp_pembimbing_perusahaan")}"
                    txtPembimbingSekolah.text = "Pembimbing Sekolah: ${data.getString("pembimbing_sekolah")}"
                    txtNoHpSekolah.text = "No HP: ${data.getString("no_hp_pembimbing_sekolah")}"

                    val foto = data.optString("foto", "")
                    val fotoUrl = if (foto.isNotEmpty())
                        "http://192.168.1.103/backend-app-jurnalcbt1/uploads/images_siswa/$foto"
                    else
                        "http://192.168.1.103/backend-app-jurnalcbt1/uploads/images_siswa/default.jpg"

                    Glide.with(requireContext())
                        .load(fotoUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(imgFoto)
                } else {
                    Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        )

        Volley.newRequestQueue(requireContext()).add(request)
    }
}
