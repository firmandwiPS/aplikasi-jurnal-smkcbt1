package com.example.apk_jurnal_smkcbt1.siswa.biodata

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R

class UbahSiswaBiodataFragment : Fragment() {

    private lateinit var etNama: EditText
    private lateinit var etKelas: EditText
    private lateinit var etJurusan: EditText
    private lateinit var etTempatLahir: EditText
    private lateinit var etTanggalLahir: EditText
    private lateinit var etAlamatRumah: EditText
    private lateinit var etNoHp: EditText

    private lateinit var etTempatPkl: EditText
    private lateinit var etAlamatPkl: EditText
    private lateinit var etBidangKerja: EditText
    private lateinit var etPembimbing: EditText
    private lateinit var etMulaiPkl: EditText
    private lateinit var etSelesaiPkl: EditText
    private lateinit var etStatusPkl: EditText
    private lateinit var etCatatanPkl: EditText
    private lateinit var btnSimpan: Button
    private val UPDATE_URL = "http://192.168.1.13/backend-app-jurnalcbt1/siswa_user/biodata_siswa/ubah_biodata_user_siswa.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ubah_siswa_biodata, container, false)

        initViews(view)
        fillFormFromArguments()
        btnSimpan.setOnClickListener { updateBiodata() }

        return view
    }

    private fun initViews(view: View) {
        etNama = view.findViewById(R.id.etNama)
        etKelas = view.findViewById(R.id.etKelas)
        etJurusan = view.findViewById(R.id.etJurusan)
        etTempatLahir = view.findViewById(R.id.etTempatLahir)
        etTanggalLahir = view.findViewById(R.id.etTanggalLahir)
        etAlamatRumah = view.findViewById(R.id.etAlamatRumah)
        etNoHp = view.findViewById(R.id.etNoHp)

        etTempatPkl = view.findViewById(R.id.etTempatPkl)
        etAlamatPkl = view.findViewById(R.id.etAlamatPkl)
        etBidangKerja = view.findViewById(R.id.etBidangKerja)
        etPembimbing = view.findViewById(R.id.etPembimbing)
        etMulaiPkl = view.findViewById(R.id.etMulaiPkl)
        etSelesaiPkl = view.findViewById(R.id.etSelesaiPkl)
        etStatusPkl = view.findViewById(R.id.etStatusPkl)
        etCatatanPkl = view.findViewById(R.id.etCatatanPkl)

        btnSimpan = view.findViewById(R.id.btnSimpan)
    }

    private fun fillFormFromArguments() {
        arguments?.let {
            etNama.setText(it.getString("nama"))
            etKelas.setText(it.getString("kelas"))
            etJurusan.setText(it.getString("jurusan"))
            etTempatLahir.setText(it.getString("tempat_lahir"))
            etTanggalLahir.setText(it.getString("tanggal_lahir"))
            etAlamatRumah.setText(it.getString("alamat_rumah"))
            etNoHp.setText(it.getString("no_hp"))

            etTempatPkl.setText(it.getString("tempat_pkl"))
            etAlamatPkl.setText(it.getString("alamat_pkl"))
            etBidangKerja.setText(it.getString("bidang_kerja"))
            etPembimbing.setText(it.getString("pembimbing"))
            etMulaiPkl.setText(it.getString("mulai_pkl"))
            etSelesaiPkl.setText(it.getString("selesai_pkl"))
            etStatusPkl.setText(it.getString("status_pkl"))
            etCatatanPkl.setText(it.getString("catatan_pkl"))
        }
    }

    private fun updateBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null)

        if (nis == null) {
            Toast.makeText(requireContext(), "NIS tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        if (!validateInput()) return

        val request = object : StringRequest(Request.Method.POST, UPDATE_URL,
            { response ->
                Log.d("UPDATE_RESPONSE", response)
                Toast.makeText(requireContext(), "Biodata berhasil diubah", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            },
            { error ->
                Log.e("UPDATE_ERROR", error.toString())
                Toast.makeText(requireContext(), "Gagal mengubah data: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params["nis"] = nis

                // Biodata
                params["biodata[nama_lengkap]"] = etNama.text.toString()
                params["biodata[kelas]"] = etKelas.text.toString()
                params["biodata[jurusan]"] = etJurusan.text.toString()
                params["biodata[tempat_lahir]"] = etTempatLahir.text.toString()
                params["biodata[tanggal_lahir]"] = etTanggalLahir.text.toString()
                params["biodata[alamat_rumah]"] = etAlamatRumah.text.toString()
                params["biodata[no_hp]"] = etNoHp.text.toString()

                // PKL
                params["pkl[tempat_pkl]"] = etTempatPkl.text.toString()
                params["pkl[alamat_pkl]"] = etAlamatPkl.text.toString()
                params["pkl[bidang_kerja]"] = etBidangKerja.text.toString()
                params["pkl[pembimbing]"] = etPembimbing.text.toString()
                params["pkl[mulai_pkl]"] = etMulaiPkl.text.toString()
                params["pkl[selesai_pkl]"] = etSelesaiPkl.text.toString()
                params["pkl[status_pkl]"] = etStatusPkl.text.toString().lowercase()
                params["pkl[catatan_pkl]"] = etCatatanPkl.text.toString()

                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (etNama.text.isNullOrEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            isValid = false
        }
        if (etKelas.text.isNullOrEmpty()) {
            etKelas.error = "Kelas tidak boleh kosong"
            isValid = false
        }
        if (etJurusan.text.isNullOrEmpty()) {
            etJurusan.error = "Jurusan tidak boleh kosong"
            isValid = false
        }
        if (etNoHp.text.isNullOrEmpty()) {
            etNoHp.error = "No HP tidak boleh kosong"
            isValid = false
        } else if (!isValidPhoneNumber(etNoHp.text.toString())) {
            etNoHp.error = "Format nomor HP tidak valid"
            isValid = false
        }

        return isValid
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        return phone.matches(Regex("^[0-9]{10,15}$"))
    }
}
