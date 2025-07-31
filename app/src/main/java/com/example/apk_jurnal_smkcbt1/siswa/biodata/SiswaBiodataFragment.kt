package com.example.apk_jurnal_smkcbt1.siswa.biodata

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class SiswaBiodataFragment : Fragment() {

    private lateinit var btnBiodata: Button
    private lateinit var btnPkl: Button
    private lateinit var layoutBiodata: LinearLayout
    private lateinit var layoutPkl: LinearLayout
    private lateinit var fabEdit: FloatingActionButton

    // Personal Data Views
    private lateinit var tvNis: TextView
    private lateinit var tvNama: TextView
    private lateinit var tvKelas: TextView
    private lateinit var tvJurusan: TextView
    private lateinit var tvTempatLahir: TextView
    private lateinit var tvTanggalLahir: TextView
    private lateinit var tvAlamatRumah: TextView
    private lateinit var tvNoHp: TextView

    // PKL Data Views
    private lateinit var tvTempatPkl: TextView
    private lateinit var tvAlamatPkl: TextView
    private lateinit var tvBidangKerja: TextView
    private lateinit var tvPembimbing: TextView
    private lateinit var tvMulaiPkl: TextView
    private lateinit var tvSelesaiPkl: TextView
    private lateinit var tvStatusPkl: TextView
    private lateinit var tvCatatanPkl: TextView

    private val BIODATA_URL = "http://192.168.1.13/backend-app-jurnalcbt1/siswa_user/biodata_siswa/biodata_user_siswa.php"

    // Add color variables
    private val selectedColor by lazy { ContextCompat.getColor(requireContext(), R.color.oren) }
    private val unselectedColor by lazy { ContextCompat.getColor(requireContext(), android.R.color.white) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_siswa_biodata, container, false)

        initViews(view)
        setupClickListeners()
        loadBiodata()
        showBiodata()

        return view
    }

    private fun initViews(view: View) {
        btnBiodata = view.findViewById(R.id.btnBiodata)
        btnPkl = view.findViewById(R.id.btnPkl)
        layoutBiodata = view.findViewById(R.id.layoutBiodata)
        layoutPkl = view.findViewById(R.id.layoutPkl)
        fabEdit = view.findViewById(R.id.fabEdit)

        // Initialize all TextViews
        tvNis = view.findViewById(R.id.tvNis)
        tvNama = view.findViewById(R.id.tvNama)
        tvKelas = view.findViewById(R.id.tvKelas)
        tvJurusan = view.findViewById(R.id.tvJurusan)
        tvTempatLahir = view.findViewById(R.id.tvTempatLahir)
        tvTanggalLahir = view.findViewById(R.id.tvTanggalLahir)
        tvAlamatRumah = view.findViewById(R.id.tvAlamatRumah)
        tvNoHp = view.findViewById(R.id.tvNoHp)

        tvTempatPkl = view.findViewById(R.id.tvTempatPkl)
        tvAlamatPkl = view.findViewById(R.id.tvAlamatPkl)
        tvBidangKerja = view.findViewById(R.id.tvBidangKerja)
        tvPembimbing = view.findViewById(R.id.tvPembimbing)
        tvMulaiPkl = view.findViewById(R.id.tvMulaiPkl)
        tvSelesaiPkl = view.findViewById(R.id.tvSelesaiPkl)
        tvStatusPkl = view.findViewById(R.id.tvStatusPkl)
        tvCatatanPkl = view.findViewById(R.id.tvCatatanPkl)
    }

    private fun setupClickListeners() {
        btnBiodata.setOnClickListener { showBiodata() }
        btnPkl.setOnClickListener { showPklData() }
        fabEdit.setOnClickListener { navigateToEditFragment() }
    }

    private fun navigateToEditFragment() {
        // Create instance of destination fragment
        val ubahFragment = UbahSiswaBiodataFragment()

        // Prepare bundle with data
        val bundle = Bundle().apply {
            // Personal Data
            putString("nis", tvNis.text.toString())
            putString("nama", tvNama.text.toString())
            putString("kelas", tvKelas.text.toString())
            putString("jurusan", tvJurusan.text.toString())
            putString("tempat_lahir", tvTempatLahir.text.toString())
            putString("tanggal_lahir", tvTanggalLahir.text.toString())
            putString("alamat_rumah", tvAlamatRumah.text.toString())
            putString("no_hp", tvNoHp.text.toString())

            // PKL Data
            putString("tempat_pkl", tvTempatPkl.text.toString())
            putString("alamat_pkl", tvAlamatPkl.text.toString())
            putString("bidang_kerja", tvBidangKerja.text.toString())
            putString("pembimbing", tvPembimbing.text.toString())
            putString("mulai_pkl", tvMulaiPkl.text.toString())
            putString("selesai_pkl", tvSelesaiPkl.text.toString())
            putString("status_pkl", tvStatusPkl.text.toString())
            putString("catatan_pkl", tvCatatanPkl.text.toString())
        }

        // Set arguments to fragment
        ubahFragment.arguments = bundle

        // Perform fragment transaction
        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Replace current fragment with new fragment
        transaction.replace(R.id.fragment_container, ubahFragment)

        // Add to back stack to allow back navigation
        transaction.addToBackStack(null)

        // Commit transaction
        transaction.commit()
    }

    private fun showBiodata() {
        layoutBiodata.visibility = View.VISIBLE
        layoutPkl.visibility = View.GONE
        updateButtonColors(true)
    }

    private fun showPklData() {
        layoutBiodata.visibility = View.GONE
        layoutPkl.visibility = View.VISIBLE
        updateButtonColors(false)
    }

    private fun updateButtonColors(isBiodataSelected: Boolean) {
        btnBiodata.setBackgroundColor(if (isBiodataSelected) selectedColor else unselectedColor)
        btnBiodata.setTextColor(if (isBiodataSelected) unselectedColor else selectedColor)

        btnPkl.setBackgroundColor(if (!isBiodataSelected) selectedColor else unselectedColor)
        btnPkl.setTextColor(if (!isBiodataSelected) unselectedColor else selectedColor)
    }

    private fun loadBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: run {
            Toast.makeText(requireContext(), "NIS tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "$BIODATA_URL?nis=$nis"

        val request = object : StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    Log.d("BiodataResponse", response)
                    val jsonResponse = JSONObject(response)

                    when (jsonResponse.getString("status")) {
                        "success" -> {
                            val data = jsonResponse.getJSONObject("data")
                            updateUIWithData(data)
                        }
                        "error" -> {
                            val message = jsonResponse.getString("message")
                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BiodataError", "Error parsing: ${e.message}", e)
                    Toast.makeText(requireContext(), "Error memproses data", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Log.e("BiodataError", "Network error: ${error.message}", error)
                Toast.makeText(
                    requireContext(),
                    "Error jaringan: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateUIWithData(data: JSONObject) {
        // Personal Data
        tvNis.text = data.optString("nis", "-")
        tvNama.text = data.optString("nama_lengkap", "-")
        tvKelas.text = data.optString("kelas", "-")
        tvJurusan.text = data.optString("jurusan", "-")
        tvTempatLahir.text = data.optString("tempat_lahir", "-")
        tvTanggalLahir.text = data.optString("tanggal_lahir", "-")
        tvAlamatRumah.text = data.optString("alamat_rumah", "-")
        tvNoHp.text = data.optString("no_hp", "-")

        // PKL Data
        tvTempatPkl.text = data.optString("tempat_pkl", "-")
        tvAlamatPkl.text = data.optString("alamat_pkl", "-")
        tvBidangKerja.text = data.optString("bidang_kerja", "-")
        tvPembimbing.text = data.optString("pembimbing", "-")
        tvMulaiPkl.text = data.optString("mulai_pkl", "-")
        tvSelesaiPkl.text = data.optString("selesai_pkl", "-")
        tvStatusPkl.text = data.optString("status_pkl", "-")
        tvCatatanPkl.text = data.optString("catatan_pkl", "-")
    }
}