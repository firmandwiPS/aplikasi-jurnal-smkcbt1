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
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class SiswaBiodataFragment : Fragment() {

    private lateinit var tvName: EditText
    private lateinit var tvRole: EditText
    private lateinit var tvNis: EditText
    private lateinit var tvKelas: EditText
    private lateinit var tvTTL: EditText
    private lateinit var tvAlamat: EditText
    private lateinit var tvNoHp: EditText
    private lateinit var tvTempatPkl: EditText
    private lateinit var tvPembimbing: EditText
    private lateinit var tvWaktuPkl: EditText
    private lateinit var tvStatus: EditText
    private lateinit var tvCatatan: EditText
    private lateinit var ivProfile: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnChangePhoto: Button
    private lateinit var cvPersonalInfo: CardView
    private lateinit var cvPklInfo: CardView
    private lateinit var footerLayout: View

    private var isEditMode = false
    private var selectedImageUri: Uri? = null
    private var originalPhotoUrl: String = ""

    private val BASE_URL = "http://192.168.1.4/backend-app-jurnalcbt1/biodata/biodata_user_siswa.php"
    private val UPDATE_URL = "http://192.168.1.4/backend-app-jurnalcbt1/biodata/ubah_biodata_user_siswa.php"
    private val UPLOAD_PHOTO_URL = "http://192.168.1.4/backend-app-jurnalcbt1/biodata/upload_foto_biodata_user_siswa.php"

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
        inflater: LayoutInflater, container: ViewGroup?,
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

        return view
    }

    private fun initializeViews(view: View) {
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
        btnSave = view.findViewById(R.id.btnSave)
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
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
        if (enable) {
            btnSave.text = "SIMPAN PERUBAHAN"
            btnSave.setBackgroundColor(Color.parseColor("#FF9800"))
            btnChangePhoto.visibility = View.VISIBLE
            enableEditing(true)
            highlightEditableFields(true)
        } else {
            btnSave.text = "EDIT BIODATA"
            btnSave.setBackgroundColor(Color.parseColor("#FF9800"))
            btnChangePhoto.visibility = View.GONE
            enableEditing(false)
            highlightEditableFields(false)
        }
    }

    private fun highlightEditableFields(highlight: Boolean) {
        val highlightColor = if (highlight) Color.parseColor("#FFF3E0") else Color.TRANSPARENT
        tvName.setBackgroundColor(highlightColor)
        tvRole.setBackgroundColor(highlightColor)
        tvKelas.setBackgroundColor(highlightColor)
        tvTTL.setBackgroundColor(highlightColor)
        tvAlamat.setBackgroundColor(highlightColor)
        tvNoHp.setBackgroundColor(highlightColor)
        tvTempatPkl.setBackgroundColor(highlightColor)
        tvPembimbing.setBackgroundColor(highlightColor)
        tvWaktuPkl.setBackgroundColor(highlightColor)
        tvStatus.setBackgroundColor(highlightColor)
        tvCatatan.setBackgroundColor(highlightColor)
    }

    private fun enableEditing(enable: Boolean) {
        tvName.isEnabled = enable
        tvRole.isEnabled = enable
        tvKelas.isEnabled = enable
        tvTTL.isEnabled = enable
        tvAlamat.isEnabled = enable
        tvNoHp.isEnabled = enable
        tvTempatPkl.isEnabled = enable
        tvPembimbing.isEnabled = enable
        tvWaktuPkl.isEnabled = enable
        tvStatus.isEnabled = enable
        tvCatatan.isEnabled = enable
        tvNis.isEnabled = false
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun validateFields(): Boolean {
        if (tvName.text.toString().trim().isEmpty()) {
            tvName.error = "Nama lengkap harus diisi"
            return false
        }
        if (tvNoHp.text.toString().trim().isEmpty()) {
            tvNoHp.error = "Nomor HP harus diisi"
            return false
        }
        if (tvAlamat.text.toString().trim().isEmpty()) {
            tvAlamat.error = "Alamat harus diisi"
            return false
        }
        return true
    }

    private fun loadBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: run {
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

                        tvName.setText(data.getString("nama_lengkap"))
                        tvRole.setText(data.getString("jurusan"))
                        tvNis.setText(data.getString("nis"))
                        tvKelas.setText(data.getString("kelas"))
                        tvTTL.setText("${data.getString("tempat_lahir")}, ${data.getString("tanggal_lahir")}")
                        tvAlamat.setText(data.getString("alamat_rumah"))
                        tvNoHp.setText(data.getString("no_hp"))
                        tvTempatPkl.setText(data.getString("tempat_pkl"))
                        tvPembimbing.setText(data.getString("pembimbing"))
                        tvWaktuPkl.setText("${data.getString("mulai_pkl")} s.d ${data.getString("selesai_pkl")}")
                        tvStatus.setText(data.getString("status_pkl"))
                        tvCatatan.setText(data.getString("catatan_pkl"))

                        originalPhotoUrl = data.getString("foto")
                        Glide.with(requireContext())
                            .load(originalPhotoUrl)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(ivProfile)

                    } else {
                        Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan saat parsing data", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(requireContext(), "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(stringRequest)
    }

    private fun uploadPhotoThenUpdateData() {
        selectedImageUri?.let { uri ->
            val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val nis = sharedPref.getString("nis", null) ?: return@let

            Toast.makeText(requireContext(), "Mengunggah foto...", Toast.LENGTH_SHORT).show()

            val requestQueue = Volley.newRequestQueue(requireContext())
            val stringRequest = object : StringRequest(
                Request.Method.POST, UPLOAD_PHOTO_URL,
                { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        if (jsonObject.getBoolean("success")) {
                            originalPhotoUrl = jsonObject.getString("file_path")
                            updateBiodata()
                        } else {
                            Toast.makeText(requireContext(), "Gagal mengunggah foto", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                },
                {
                    Toast.makeText(requireContext(), "Gagal mengunggah foto", Toast.LENGTH_SHORT).show()
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return HashMap<String, String>().apply {
                        put("nis", nis)
                    }
                }

                override fun getBodyContentType(): String {
                    return "multipart/form-data"
                }

                override fun getBody(): ByteArray {
                    return createMultipartBody(uri, nis)
                }
            }
            requestQueue.add(stringRequest)
        }
    }

    private fun createMultipartBody(uri: Uri, nis: String): ByteArray {
        val boundary = "Boundary-${System.currentTimeMillis()}"
        val file = File(uri.path ?: "")
        val filePart = context?.contentResolver?.openInputStream(uri)?.readBytes() ?: byteArrayOf()

        return java.io.ByteArrayOutputStream().apply {
            write("--$boundary\r\n".toByteArray())
            write("Content-Disposition: form-data; name=\"foto\"; filename=\"${file.name}\"\r\n".toByteArray())
            write("Content-Type: image/*\r\n\r\n".toByteArray())
            write(filePart)
            write("\r\n--$boundary--\r\n".toByteArray())
        }.toByteArray()
    }

    private fun updateBiodata() {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: return

        Toast.makeText(requireContext(), "Menyimpan perubahan...", Toast.LENGTH_SHORT).show()

        val requestQueue = Volley.newRequestQueue(requireContext())
        val stringRequest = object : StringRequest(
            Request.Method.POST, UPDATE_URL,
            { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean("success")) {
                        Toast.makeText(requireContext(), "Biodata berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        setupEditMode(false)
                        loadBiodata()
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui biodata", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(requireContext(), "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val ttlParts = tvTTL.text.toString().split(",").map { it.trim() }
                val waktuPklParts = tvWaktuPkl.text.toString().split("s.d").map { it.trim() }

                return HashMap<String, String>().apply {
                    put("nis", nis)
                    put("nama_lengkap", tvName.text.toString())
                    put("jurusan", tvRole.text.toString())
                    put("kelas", tvKelas.text.toString())
                    put("tempat_lahir", if (ttlParts.isNotEmpty()) ttlParts[0] else "")
                    put("tanggal_lahir", if (ttlParts.size > 1) ttlParts[1] else "")
                    put("alamat_rumah", tvAlamat.text.toString())
                    put("no_hp", tvNoHp.text.toString())
                    put("tempat_pkl", tvTempatPkl.text.toString())
                    put("pembimbing", tvPembimbing.text.toString())
                    put("mulai_pkl", if (waktuPklParts.isNotEmpty()) waktuPklParts[0] else "")
                    put("selesai_pkl", if (waktuPklParts.size > 1) waktuPklParts[1] else "")
                    put("status_pkl", tvStatus.text.toString())
                    put("catatan_pkl", tvCatatan.text.toString())
                    put("foto", originalPhotoUrl)
                }
            }
        }
        requestQueue.add(stringRequest)
    }
}