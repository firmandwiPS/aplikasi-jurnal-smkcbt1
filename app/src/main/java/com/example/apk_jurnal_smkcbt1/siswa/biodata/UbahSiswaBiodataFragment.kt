package com.example.apk_jurnal_smkcbt1.siswa.biodata

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UbahSiswaBiodataFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var fotoSiswa: ImageView
    private lateinit var tvNis: TextView
    private lateinit var btnPilihFoto: Button
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button

    // Biodata Fields
    private lateinit var etNama: TextInputEditText
    private lateinit var etJenisKelamin: TextInputEditText
    private lateinit var etKelas: TextInputEditText
    private lateinit var etJurusan: TextInputEditText
    private lateinit var etTempatLahir: TextInputEditText
    private lateinit var etTanggalLahir: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etNoHp: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etStatus: TextInputEditText

    // PKL Fields
    private lateinit var etTempatPkl: TextInputEditText
    private lateinit var etAlamatPkl: TextInputEditText
    private lateinit var etBidangKerja: TextInputEditText
    private lateinit var etPembimbing: TextInputEditText
    private lateinit var etNoHpPembimbing: TextInputEditText
    private lateinit var etMulaiPkl: TextInputEditText
    private lateinit var etSelesaiPkl: TextInputEditText
    private lateinit var etStatusPkl: TextInputEditText
    private lateinit var etCatatanPkl: TextInputEditText

    private var fotoBase64: String = ""
    private var isPhotoChanged = false
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val UPDATE_URL = "http://192.168.1.103/backend-app-jurnalcbt1/siswa_user/biodata_siswa/ubah_biodata_user_siswa.php"
    private val BIODATA_URL = "http://192.168.1.103/backend-app-jurnalcbt1/siswa_user/biodata_siswa/biodata_user_siswa.php"

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    handleSelectedImage(uri)
                    isPhotoChanged = true
                } catch (e: Exception) {
                    showToast("Gagal memuat gambar: ${e.message}")
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            showToast("Izin akses galeri diperlukan untuk memilih foto")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ubah_siswa_biodata, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupDatePickers()
        setupDropdowns()
        loadBiodata()
        setupButtons()
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        fotoSiswa = view.findViewById(R.id.ivFotoSiswa)
        tvNis = view.findViewById(R.id.tvNis)
        btnPilihFoto = view.findViewById(R.id.btnPilihFoto)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        btnBatal = view.findViewById(R.id.btnBatal)

        // Initialize all fields
        etNama = view.findViewById(R.id.etNama)
        etJenisKelamin = view.findViewById(R.id.etJenisKelamin)
        etKelas = view.findViewById(R.id.etKelas)
        etJurusan = view.findViewById(R.id.etJurusan)
        etTempatLahir = view.findViewById(R.id.etTempatLahir)
        etTanggalLahir = view.findViewById(R.id.etTanggalLahir)
        etAlamat = view.findViewById(R.id.etAlamat)
        etNoHp = view.findViewById(R.id.etNoHp)
        etEmail = view.findViewById(R.id.etEmail)
        etStatus = view.findViewById(R.id.etStatus)
        etTempatPkl = view.findViewById(R.id.etTempatPkl)
        etAlamatPkl = view.findViewById(R.id.etAlamatPkl)
        etBidangKerja = view.findViewById(R.id.etBidangKerja)
        etPembimbing = view.findViewById(R.id.etPembimbing)
        etNoHpPembimbing = view.findViewById(R.id.etNoHpPembimbing)
        etMulaiPkl = view.findViewById(R.id.etMulaiPkl)
        etSelesaiPkl = view.findViewById(R.id.etSelesaiPkl)
        etStatusPkl = view.findViewById(R.id.etStatusPkl)
        etCatatanPkl = view.findViewById(R.id.etCatatanPkl)
    }

    private fun setupDatePickers() {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, day)
            }
            val dateString = dateFormat.format(calendar.time)

            when {
                etTanggalLahir.isFocused -> etTanggalLahir.setText(dateString)
                etMulaiPkl.isFocused -> etMulaiPkl.setText(dateString)
                etSelesaiPkl.isFocused -> etSelesaiPkl.setText(dateString)
            }
        }

        val dateClickListener = View.OnClickListener { view ->
            val currentField = when (view.id) {
                R.id.etTanggalLahir -> etTanggalLahir
                R.id.etMulaiPkl -> etMulaiPkl
                R.id.etSelesaiPkl -> etSelesaiPkl
                else -> null
            }

            currentField?.let { field ->
                val currentDate = field.text?.toString()
                showDatePicker(dateListener, currentDate)
            }
        }

        etTanggalLahir.setOnClickListener(dateClickListener)
        etMulaiPkl.setOnClickListener(dateClickListener)
        etSelesaiPkl.setOnClickListener(dateClickListener)

        // Make sure the fields can't be edited manually
        etTanggalLahir.keyListener = null
        etMulaiPkl.keyListener = null
        etSelesaiPkl.keyListener = null
    }

    private fun showDatePicker(listener: DatePickerDialog.OnDateSetListener, currentDate: String?) {
        val calendar = Calendar.getInstance()

        try {
            if (!currentDate.isNullOrEmpty()) {
                val parsedDate = dateFormat.parse(currentDate)
                parsedDate?.let {
                    calendar.time = it
                }
            }
        } catch (e: Exception) {
            Log.e("DateParse", "Error parsing date", e)
            // If parsing fails, use today's date
            calendar.timeInMillis = System.currentTimeMillis()
        }

        DatePickerDialog(
            requireContext(),
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupDropdowns() {
        // Setup dropdown for gender
        val genderOptions = arrayOf("L", "P")
        etJenisKelamin.setOnClickListener {
            showSingleChoiceDialog("Pilih Jenis Kelamin", genderOptions) { selected ->
                etJenisKelamin.setText(selected)
            }
        }

        // Setup dropdown for status
        val statusOptions = arrayOf("aktif", "tidak aktif")
        etStatus.setOnClickListener {
            showSingleChoiceDialog("Pilih Status", statusOptions) { selected ->
                etStatus.setText(selected)
            }
        }

        // Setup dropdown for PKL status
        val pklStatusOptions = arrayOf("berjalan", "selesai")
        etStatusPkl.setOnClickListener {
            showSingleChoiceDialog("Pilih Status PKL", pklStatusOptions) { selected ->
                etStatusPkl.setText(selected)
            }
        }

        // Make sure dropdown fields can't be edited manually
        etJenisKelamin.keyListener = null
        etStatus.keyListener = null
        etStatusPkl.keyListener = null
    }

    private fun showSingleChoiceDialog(title: String, options: Array<String>, onSelected: (String) -> Unit) {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setItems(options) { _, which ->
                onSelected(options[which])
            }
            .create()
            .show()
    }

    private fun setupButtons() {
        btnBatal.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSimpan.setOnClickListener {
            if (validateInputs()) {
                updateBiodata()
            }
        }

        btnPilihFoto.setOnClickListener {
            checkGalleryPermission()
        }
    }

    private fun validateInputs(): Boolean {
        if (etNama.text.isNullOrEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            return false
        }
        if (etJenisKelamin.text.isNullOrEmpty()) {
            etJenisKelamin.error = "Jenis kelamin tidak boleh kosong"
            return false
        }
        if (etKelas.text.isNullOrEmpty()) {
            etKelas.error = "Kelas tidak boleh kosong"
            return false
        }
        if (etJurusan.text.isNullOrEmpty()) {
            etJurusan.error = "Jurusan tidak boleh kosong"
            return false
        }
        return true
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                permission
            ) -> {
                showToast("Aplikasi membutuhkan izin untuk mengakses galeri")
                requestPermissionLauncher.launch(permission)
            }

            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    private fun handleSelectedImage(uri: Uri) {
        try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inSampleSize = 2
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            bitmap?.let {
                val resizedBitmap = resizeBitmap(it, 800)
                fotoSiswa.setImageBitmap(resizedBitmap)
                fotoBase64 = convertBitmapToBase64(resizedBitmap)
            } ?: showToast("Gagal memuat gambar")
        } catch (e: IOException) {
            showToast("Gagal memuat gambar: ${e.message}")
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        if (width > maxSize || height > maxSize) {
            val ratio = width.toFloat() / height.toFloat()
            if (ratio > 1) {
                width = maxSize
                height = (width / ratio).toInt()
            } else {
                height = maxSize
                width = (height * ratio).toInt()
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    private fun loadBiodata() {
        progressBar.visibility = View.VISIBLE

        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: run {
            showError("NIS tidak ditemukan")
            progressBar.visibility = View.GONE
            return
        }

        val stringRequest = StringRequest(
            Request.Method.GET, "$BIODATA_URL?nis=$nis",
            { response -> handleLoadResponse(response) },
            { error ->
                showError("Gagal memuat data: ${error.message ?: "Unknown error"}")
                progressBar.visibility = View.GONE
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

        Volley.newRequestQueue(requireContext()).add(stringRequest)
    }

    private fun handleLoadResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.getString("status") == "success") {
                val data = jsonObject.getJSONObject("data")
                displayBiodata(data)
            } else {
                showError(jsonObject.getString("message"))
            }
        } catch (e: Exception) {
            showError("Gagal memproses data: ${e.message}")
        } finally {
            progressBar.visibility = View.GONE
        }
    }

    private fun displayBiodata(data: JSONObject) {
        activity?.runOnUiThread {
            try {
                tvNis.text = "NIS: ${data.optString("nis", "-")}"

                // Set all fields
                etNama.setText(data.optString("nama_lengkap", ""))
                etJenisKelamin.setText(data.optString("jenis_kelamin", ""))
                etKelas.setText(data.optString("kelas", ""))
                etJurusan.setText(data.optString("jurusan", ""))
                etTempatLahir.setText(data.optString("tempat_lahir", ""))
                etTanggalLahir.setText(data.optString("tanggal_lahir", ""))
                etAlamat.setText(data.optString("alamat_rumah", ""))
                etNoHp.setText(data.optString("no_hp", ""))
                etEmail.setText(data.optString("email", ""))
                etStatus.setText(data.optString("status", ""))
                etTempatPkl.setText(data.optString("tempat_pkl", ""))
                etAlamatPkl.setText(data.optString("alamat_pkl", ""))
                etBidangKerja.setText(data.optString("bidang_kerja", ""))
                etPembimbing.setText(data.optString("pembimbing", ""))
                etNoHpPembimbing.setText(data.optString("no_hp_pembimbing", ""))
                etMulaiPkl.setText(data.optString("mulai_pkl", ""))
                etSelesaiPkl.setText(data.optString("selesai_pkl", ""))
                etStatusPkl.setText(data.optString("status_pkl", ""))
                etCatatanPkl.setText(data.optString("catatan_pkl", ""))

                // Load Photo
                val photoData = data.optString("foto_base64", "")
                if (photoData.isNotEmpty()) {
                    fotoBase64 = photoData
                    try {
                        val imageBytes = Base64.decode(
                            photoData.split(",").getOrNull(1) ?: photoData,
                            Base64.DEFAULT
                        )
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        fotoSiswa.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        Log.e("PhotoError", "Error loading photo", e)
                        fotoSiswa.setImageResource(R.drawable.ic_person)
                    }
                } else {
                    fotoSiswa.setImageResource(R.drawable.ic_person)
                }
            } catch (e: Exception) {
                Log.e("DisplayError", "Error displaying data", e)
            }
        }
    }

    private fun updateBiodata() {
        progressBar.visibility = View.VISIBLE

        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val nis = sharedPref.getString("nis", null) ?: run {
            showError("NIS tidak ditemukan")
            progressBar.visibility = View.GONE
            return
        }

        try {
            val requestData = JSONObject().apply {
                put("nis", nis)
                put("nama_lengkap", etNama.text?.toString() ?: "")
                put("jenis_kelamin", etJenisKelamin.text?.toString() ?: "")
                put("kelas", etKelas.text?.toString() ?: "")
                put("jurusan", etJurusan.text?.toString() ?: "")
                put("tempat_lahir", etTempatLahir.text?.toString() ?: "")
                put("tanggal_lahir", etTanggalLahir.text?.toString() ?: "")
                put("alamat_rumah", etAlamat.text?.toString() ?: "")
                put("no_hp", etNoHp.text?.toString() ?: "")
                put("email", etEmail.text?.toString() ?: "")
                put("status", etStatus.text?.toString() ?: "aktif")
                put("tempat_pkl", etTempatPkl.text?.toString() ?: "")
                put("alamat_pkl", etAlamatPkl.text?.toString() ?: "")
                put("bidang_kerja", etBidangKerja.text?.toString() ?: "")
                put("pembimbing", etPembimbing.text?.toString() ?: "")
                put("no_hp_pembimbing", etNoHpPembimbing.text?.toString() ?: "")
                put("mulai_pkl", etMulaiPkl.text?.toString() ?: "")
                put("selesai_pkl", etSelesaiPkl.text?.toString() ?: "")
                put("status_pkl", etStatusPkl.text?.toString() ?: "berjalan")
                put("catatan_pkl", etCatatanPkl.text?.toString() ?: "")

                if (isPhotoChanged && fotoBase64.isNotEmpty()) {
                    put("foto_base64", "data:image/jpeg;base64,$fotoBase64")
                }
            }

            val stringRequest = object : StringRequest(
                Request.Method.POST, UPDATE_URL,
                { response -> handleUpdateResponse(response) },
                { error ->
                    activity?.runOnUiThread {
                        progressBar.visibility = View.GONE
                        val errorMessage = if (error.networkResponse != null) {
                            String(error.networkResponse.data, Charsets.UTF_8)
                        } else {
                            error.message ?: "Unknown error"
                        }
                        showError("Gagal mengupdate data: $errorMessage")
                        Log.e("UpdateError", "Error: $errorMessage", error)
                    }
                }
            ) {
                override fun getBody(): ByteArray {
                    return requestData.toString().toByteArray(Charsets.UTF_8)
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getHeaders(): MutableMap<String, String> {
                    return HashMap<String, String>().apply {
                        put("Content-Type", "application/json")
                    }
                }
            }.apply {
                retryPolicy = DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            }

            Volley.newRequestQueue(requireContext()).add(stringRequest)
        } catch (e: Exception) {
            activity?.runOnUiThread {
                progressBar.visibility = View.GONE
                showError("Gagal membuat data request: ${e.message}")
                Log.e("RequestError", "Error creating request", e)
            }
        }
    }

    private fun handleUpdateResponse(response: String) {
        activity?.runOnUiThread {
            try {
                Log.d("API_Response", response)

                if (!response.trim().startsWith("{")) {
                    throw Exception("Respons bukan JSON: $response")
                }

                val jsonObject = JSONObject(response)
                when (jsonObject.getString("status")) {
                    "success" -> {
                        showToast("Data berhasil diperbarui")
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    else -> {
                        showError(jsonObject.optString("message", "Gagal memperbarui data"))
                    }
                }
            } catch (e: Exception) {
                showError("Gagal memproses respons: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        fotoSiswa.setImageBitmap(null)
        super.onDestroyView()
    }
}