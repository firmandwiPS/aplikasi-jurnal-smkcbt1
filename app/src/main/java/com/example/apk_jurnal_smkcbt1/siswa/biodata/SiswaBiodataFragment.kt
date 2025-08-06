package com.example.apk_jurnal_smkcbt1.siswa.biodata

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.login.LoginActivity
import org.json.JSONException
import org.json.JSONObject

class SiswaBiodataFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var fotoSiswa: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvKelas: TextView
    private lateinit var tvJurusan: TextView
    private lateinit var tvJenisKelamin: TextView
    private lateinit var tvTempatLahir: TextView
    private lateinit var tvTanggalLahir: TextView
    private lateinit var tvAlamat: TextView
    private lateinit var tvNoHp: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvTempatPkl: TextView
    private lateinit var tvAlamatPkl: TextView
    private lateinit var tvBidangKerja: TextView
    private lateinit var tvPembimbing: TextView
    private lateinit var tvNoHpPembimbing: TextView
    private lateinit var tvMulaiPkl: TextView
    private lateinit var tvSelesaiPkl: TextView
    private lateinit var tvStatusPkl: TextView
    private lateinit var tvCatatanPkl: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_siswa_biodata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupLogoutButton()
        setupEditButton()
        fetchBiodata()
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        fotoSiswa = view.findViewById(R.id.ivFotoSiswa)
        tvNama = view.findViewById(R.id.tvNama)
        tvNis = view.findViewById(R.id.tvNis)
        tvKelas = view.findViewById(R.id.tvKelas)
        tvJurusan = view.findViewById(R.id.tvJurusan)
        tvJenisKelamin = view.findViewById(R.id.tvJenisKelamin)
        tvTempatLahir = view.findViewById(R.id.tvTempatLahir)
        tvTanggalLahir = view.findViewById(R.id.tvTanggalLahir)
        tvAlamat = view.findViewById(R.id.tvAlamat)
        tvNoHp = view.findViewById(R.id.tvNoHp)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvStatus = view.findViewById(R.id.tvStatus)
        tvTempatPkl = view.findViewById(R.id.tvTempatPkl)
        tvAlamatPkl = view.findViewById(R.id.tvAlamatPkl)
        tvBidangKerja = view.findViewById(R.id.tvBidangKerja)
        tvPembimbing = view.findViewById(R.id.tvPembimbing)
        tvNoHpPembimbing = view.findViewById(R.id.tvNoHpPembimbing)
        tvMulaiPkl = view.findViewById(R.id.tvMulaiPkl)
        tvSelesaiPkl = view.findViewById(R.id.tvSelesaiPkl)
        tvStatusPkl = view.findViewById(R.id.tvStatusPkl)
        tvCatatanPkl = view.findViewById(R.id.tvCatatanPkl)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnEdit = view.findViewById(R.id.btnEdit)
    }

    private fun setupLogoutButton() {
        btnLogout.setOnClickListener {
            val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun setupEditButton() {
        btnEdit.setOnClickListener {
            val fragment = UbahSiswaBiodataFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun fetchBiodata() {
        progressBar.visibility = View.VISIBLE

        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: run {
            showError("NIS tidak ditemukan")
            return
        }

        val url = "http://192.168.130.91/backend-app-jurnalcbt1/siswa_user/biodata_siswa/biodata_user_siswa.php?nis=$nis"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                Log.d("BiodataResponse", response)
                handleResponse(response)
            },
            { error ->
                Log.e("BiodataError", error.toString())
                showError("Gagal memuat data: ${error.message}")
            }
        )

        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    private fun handleResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            when (jsonObject.getString("status")) {
                "success" -> displayBiodata(jsonObject.getJSONObject("data"))
                else -> showError(jsonObject.getString("message"))
            }
        } catch (e: JSONException) {
            showError("Gagal memproses data")
            e.printStackTrace()
        } finally {
            progressBar.visibility = View.GONE
        }
    }

    private fun displayBiodata(data: JSONObject) {
        try {
            // Personal Data
            tvNis.text = "NIS: ${data.optString("nis", "-")}"
            tvNama.text = data.optString("nama_lengkap", "-")
            tvKelas.text = "Kelas: ${data.optString("kelas", "-")}"
            tvJurusan.text = "Jurusan: ${data.optString("jurusan", "-")}"
            tvJenisKelamin.text = "Jenis Kelamin: ${data.optString("jenis_kelamin", "-")}"
            tvTempatLahir.text = "Tempat Lahir: ${data.optString("tempat_lahir", "-")}"
            tvTanggalLahir.text = "Tanggal Lahir: ${data.optString("tanggal_lahir", "-")}"
            tvAlamat.text = "Alamat: ${data.optString("alamat_rumah", "-")}"
            tvNoHp.text = "No. HP: ${data.optString("no_hp", "-")}"
            tvEmail.text = "Email: ${data.optString("email", "-")}"
            tvStatus.text = "Status: ${data.optString("status", "-")}"

            // PKL Data
            val tempatPkl = data.optString("tempat_pkl", "Belum ada")
            tvTempatPkl.text = "Tempat PKL: $tempatPkl"
            tvTempatPkl.visibility = if (tempatPkl == "Belum ada") View.GONE else View.VISIBLE

            val alamatPkl = data.optString("alamat_pkl", "Belum ada")
            tvAlamatPkl.text = "Alamat PKL: $alamatPkl"
            tvAlamatPkl.visibility = if (alamatPkl == "Belum ada") View.GONE else View.VISIBLE

            val bidangKerja = data.optString("bidang_kerja", "Belum ada")
            tvBidangKerja.text = "Bidang Kerja: $bidangKerja"
            tvBidangKerja.visibility = if (bidangKerja == "Belum ada") View.GONE else View.VISIBLE

            val pembimbing = data.optString("pembimbing", "Belum ada")
            tvPembimbing.text = "Pembimbing: $pembimbing"
            tvPembimbing.visibility = if (pembimbing == "Belum ada") View.GONE else View.VISIBLE

            val noHpPembimbing = data.optString("no_hp_pembimbing", "-")
            tvNoHpPembimbing.text = "No. HP Pembimbing: $noHpPembimbing"
            tvNoHpPembimbing.visibility = if (noHpPembimbing == "-") View.GONE else View.VISIBLE

            val mulaiPkl = data.optString("mulai_pkl", "Belum ada")
            tvMulaiPkl.text = "Mulai PKL: $mulaiPkl"
            tvMulaiPkl.visibility = if (mulaiPkl == "Belum ada") View.GONE else View.VISIBLE

            val selesaiPkl = data.optString("selesai_pkl", "Belum ada")
            tvSelesaiPkl.text = "Selesai PKL: $selesaiPkl"
            tvSelesaiPkl.visibility = if (selesaiPkl == "Belum ada") View.GONE else View.VISIBLE

            val statusPkl = data.optString("status_pkl", "Belum PKL")
            tvStatusPkl.text = "Status PKL: $statusPkl"
            tvStatusPkl.visibility = if (statusPkl == "Belum PKL") View.GONE else View.VISIBLE

            val catatanPkl = data.optString("catatan_pkl", "Belum ada")
            tvCatatanPkl.text = "Catatan PKL: $catatanPkl"
            tvCatatanPkl.visibility = if (catatanPkl == "Belum ada") View.GONE else View.VISIBLE

            // Photo handling
            try {
                val fotoBase64 = data.getString("foto_base64")
                if (fotoBase64.isNotEmpty()) {
                    val imageBytes = Base64.decode(fotoBase64.split(",")[1], Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    fotoSiswa.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                fotoSiswa.setImageResource(R.drawable.ic_person)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Gagal menampilkan data")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }
}