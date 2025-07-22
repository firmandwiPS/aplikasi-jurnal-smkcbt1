package com.example.apk_jurnal_smkcbt1.siswa

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.apk_jurnal_smkcbt1.R
import org.json.JSONObject

class SiswaBiodataFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvKelas: TextView
    private lateinit var tvTTL: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvTempatPkl: TextView
    private lateinit var tvPembimbing: TextView
    private lateinit var tvWaktuPkl: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvCatatan: TextView
    private lateinit var ivProfile: ImageView

    private val BASE_URL = "http://192.168.1.110/backend-app-jurnalcbt1/biodata/biodata_user_siswa.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_siswa_biodata, container, false)

        tvName = view.findViewById(R.id.tvName)
        tvRole = view.findViewById(R.id.tvRole)
        tvNis = view.findViewById(R.id.tvNis)
        tvKelas = view.findViewById(R.id.tvKelas)
        tvTTL = view.findViewById(R.id.tvTTL)
        tvAlamat = view.findViewById(R.id.tvAlamat)
        tvNoHp = view.findViewById(R.id.tvNoHp)
        tvTempatPkl = view.findViewById(R.id.tvTempatPkl)
        tvPembimbing = view.findViewById(R.id.tvPembimbing)
        tvWaktuPkl = view.findViewById(R.id.tvWaktuPkl)
        tvStatus = view.findViewById(R.id.tvStatus)
        tvCatatan = view.findViewById(R.id.tvCatatan)
        ivProfile = view.findViewById(R.id.ivProfile)

        loadBiodata()

        return view
    }

    private fun loadBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null)

        if (nis == null) {
            Toast.makeText(requireContext(), "NIS tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "$BASE_URL?nis=$nis"
        val requestQueue = Volley.newRequestQueue(requireContext())

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        val data = jsonObject.getJSONArray("data").getJSONObject(0)

                        tvName.text = data.getString("nama_lengkap")
                        tvRole.text = data.getString("jurusan")
                        tvNis.text = data.getString("nis")
                        tvKelas.text = data.getString("kelas")
                        tvTTL.text = "${data.getString("tempat_lahir")}, ${data.getString("tanggal_lahir")}"
                        tvAlamat.text = data.getString("alamat_rumah")
                        tvNoHp.text = data.getString("no_hp")
                        tvTempatPkl.text = data.getString("tempat_pkl")
                        tvPembimbing.text = data.getString("pembimbing")

                        val mulai = data.getString("mulai_pkl")
                        val selesai = data.getString("selesai_pkl")
                        tvWaktuPkl.text = "$mulai s.d $selesai"

                        tvStatus.text = data.getString("status_pkl")
                        tvCatatan.text = data.getString("catatan_pkl")

                        val fotoUrl = data.getString("foto")
                        Glide.with(requireContext())
                            .load(fotoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(ivProfile)

                    } else {
                        Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(requireContext(), "Gagal ambil data", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(stringRequest)
    }
}
