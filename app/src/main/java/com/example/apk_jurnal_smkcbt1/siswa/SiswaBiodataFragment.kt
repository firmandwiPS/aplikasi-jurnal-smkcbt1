package com.example.apk_jurnal_smkcbt1.siswa

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.apk_jurnal_smkcbt1.R
import com.example.apk_jurnal_smkcbt1.login.LoginActivity
import org.json.JSONObject
import java.util.HashMap

class SiswaBiodataFragment : Fragment() {

    private lateinit var tvName: EditText
    private lateinit var tvRole: EditText
    private lateinit var tvNis: EditText
    private lateinit var tvKelas: EditText
    private lateinit var tvTempatLahir: EditText
    private lateinit var tvTanggalLahir: EditText
    private lateinit var tvAlamat: EditText
    private lateinit var tvNoHp: EditText
    private lateinit var tvTempatPkl: EditText
    private lateinit var tvAlamatPkl: EditText
    private lateinit var tvBidangKerja: EditText
    private lateinit var tvPembimbing: EditText
    private lateinit var tvMulaiPkl: EditText
    private lateinit var tvSelesaiPkl: EditText
    private lateinit var tvStatus: EditText
    private lateinit var tvCatatan: EditText
    private lateinit var ivProfile: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnChangePhoto: Button
    private lateinit var btnLogout: Button
    private lateinit var cvPersonalInfo: CardView
    private lateinit var cvPklInfo: CardView

    private var isEditMode = false
    private var selectedImageUri: Uri? = null
    private var originalPhotoUrl: String = ""

    private val BASE_URL = "http://192.168.1.105/backend-app-jurnalcbt1/siswa_user/biodata_siswa/biodata_user_siswa.php"
    private val UPDATE_URL = "http://192.168.1.105/backend-app-jurnalcbt1/siswa_user/biodata_siswa/ubah_biodata_user_siswa.php"
    private val UPLOAD_PHOTO_URL = "http://192.168.1.105/backend-app-jurnalcbt1/siswa_user/biodata_siswa/upload_foto_biodata_user_siswa.php"

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                ivProfile.setImageURI(uri)
                Toast.makeText(requireContext(), "Foto dipilih", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_siswa_biodata, container, false)
        initializeViews(view)
        setupEditMode(false)
        setupCardViews()
        loadBiodata()

        btnSave.setOnClickListener {
            if (isEditMode) {
                if (validateFields()) {
                    if (selectedImageUri != null) {
                        uploadPhotoThenUpdateData()
                    } else {
                        updateBiodata()
                    }
                }
            } else {
                setupEditMode(true)
            }
        }

        btnChangePhoto.setOnClickListener {
            openGallery()
        }

        btnLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun initializeViews(view: View) {
        tvName = view.findViewById(R.id.tvName)
        tvRole = view.findViewById(R.id.tvRole)
        tvNis = view.findViewById(R.id.tvNis)
        tvKelas = view.findViewById(R.id.tvKelas)
        tvTempatLahir = view.findViewById(R.id.tvTempatLahir)
        tvTanggalLahir = view.findViewById(R.id.tvTanggalLahir)
        tvAlamat = view.findViewById(R.id.tvAlamat)
        tvNoHp = view.findViewById(R.id.tvNoHp)
        tvTempatPkl = view.findViewById(R.id.tvTempatPkl)
        tvAlamatPkl = view.findViewById(R.id.tvAlamatPkl)
        tvBidangKerja = view.findViewById(R.id.tvBidangKerja)
        tvPembimbing = view.findViewById(R.id.tvPembimbing)
        tvMulaiPkl = view.findViewById(R.id.tvMulaiPkl)
        tvSelesaiPkl = view.findViewById(R.id.tvSelesaiPkl)
        tvStatus = view.findViewById(R.id.tvStatus)
        tvCatatan = view.findViewById(R.id.tvCatatan)
        ivProfile = view.findViewById(R.id.ivProfile)
        btnSave = view.findViewById(R.id.btnSave)
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
        btnLogout = view.findViewById(R.id.btnLogout)
        cvPersonalInfo = view.findViewById(R.id.cvPersonalInfo)
        cvPklInfo = view.findViewById(R.id.cvPklInfo)
    }

    private fun setupCardViews() {
        cvPersonalInfo.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
        cvPklInfo.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
        cvPersonalInfo.cardElevation = 8f
        cvPklInfo.cardElevation = 8f
        cvPersonalInfo.radius = 16f
        cvPklInfo.radius = 16f
    }

    private fun setupEditMode(enable: Boolean) {
        isEditMode = enable
        btnSave.text = if (enable) "SIMPAN PERUBAHAN" else "EDIT BIODATA"
        btnSave.setBackgroundColor(Color.parseColor(if (enable) "#4CAF50" else "#FF9800"))
        btnChangePhoto.visibility = if (enable) View.VISIBLE else View.GONE
        enableEditing(enable)
    }

    private fun enableEditing(enable: Boolean) {
        val editableFields = listOf(
            tvName, tvRole, tvKelas, tvTempatLahir, tvTanggalLahir,
            tvAlamat, tvNoHp, tvTempatPkl, tvAlamatPkl, tvBidangKerja,
            tvPembimbing, tvMulaiPkl, tvSelesaiPkl, tvStatus, tvCatatan
        )

        editableFields.forEach { field ->
            field.isEnabled = enable
            field.setBackgroundColor(
                if (enable) Color.parseColor("#FFF3E0") else Color.TRANSPARENT
            )
        }

        tvNis.isEnabled = false
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun validateFields(): Boolean {
        var isValid = true
        if (tvName.text.toString().trim().isEmpty()) {
            tvName.error = "Nama lengkap harus diisi"
            isValid = false
        }
        if (tvNoHp.text.toString().trim().isEmpty()) {
            tvNoHp.error = "Nomor HP harus diisi"
            isValid = false
        }
        if (tvAlamat.text.toString().trim().isEmpty()) {
            tvAlamat.error = "Alamat harus diisi"
            isValid = false
        }
        if (tvTempatLahir.text.toString().trim().isEmpty()) {
            tvTempatLahir.error = "Tempat lahir harus diisi"
            isValid = false
        }
        if (tvTanggalLahir.text.toString().trim().isEmpty()) {
            tvTanggalLahir.error = "Tanggal lahir harus diisi"
            isValid = false
        }
        return isValid
    }

    private fun loadBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: return
        val url = "$BASE_URL?nis=$nis"

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject.getBoolean("success")) {
                    val data = jsonObject.getJSONArray("data").getJSONObject(0)

                    tvName.setText(data.getString("nama_lengkap"))
                    tvRole.setText(data.getString("jurusan"))
                    tvNis.setText(data.getString("nis"))
                    tvKelas.setText(data.getString("kelas"))
                    tvTempatLahir.setText(data.getString("tempat_lahir"))
                    tvTanggalLahir.setText(data.getString("tanggal_lahir"))
                    tvAlamat.setText(data.getString("alamat_rumah"))
                    tvNoHp.setText(data.getString("no_hp"))
                    tvTempatPkl.setText(data.getString("tempat_pkl"))
                    tvAlamatPkl.setText(data.getString("alamat_pkl"))
                    tvBidangKerja.setText(data.getString("bidang_kerja"))
                    tvPembimbing.setText(data.getString("pembimbing"))
                    tvMulaiPkl.setText(data.getString("mulai_pkl"))
                    tvSelesaiPkl.setText(data.getString("selesai_pkl"))
                    tvStatus.setText(data.getString("status_pkl"))
                    tvCatatan.setText(data.getString("catatan_pkl"))

                    originalPhotoUrl = data.getString("foto")
                    Glide.with(requireContext())
                        .load(originalPhotoUrl)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(ivProfile)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, { error ->
            Toast.makeText(requireContext(), "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
        })

        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    private fun uploadPhotoThenUpdateData() {
        updateBiodata() // Tambahkan logika upload jika diperlukan
    }

    private fun updateBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: return

        val stringRequest = object : StringRequest(Method.POST, UPDATE_URL,
            { response ->
                try {
                    val json = JSONObject(response)
                    if (json.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Biodata berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        setupEditMode(false)
                        loadBiodata()
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui: ${json.getString("message")}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Gagal menyimpan: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                return HashMap<String, String>().apply {
                    put("nis", nis)
                    put("nama_lengkap", tvName.text.toString())
                    put("jurusan", tvRole.text.toString())
                    put("kelas", tvKelas.text.toString())
                    put("tempat_lahir", tvTempatLahir.text.toString())
                    put("tanggal_lahir", tvTanggalLahir.text.toString())
                    put("alamat_rumah", tvAlamat.text.toString())
                    put("no_hp", tvNoHp.text.toString())
                    put("tempat_pkl", tvTempatPkl.text.toString())
                    put("alamat_pkl", tvAlamatPkl.text.toString())
                    put("bidang_kerja", tvBidangKerja.text.toString())
                    put("pembimbing", tvPembimbing.text.toString())
                    put("mulai_pkl", tvMulaiPkl.text.toString())
                    put("selesai_pkl", tvSelesaiPkl.text.toString())
                    put("status_pkl", tvStatus.text.toString())
                    put("catatan_pkl", tvCatatan.text.toString())
                    put("foto", originalPhotoUrl)
                }
            }
        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    private fun logout() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
