package com.example.apk_jurnal_smkcbt1.admin.siswa

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_jurnal_smkcbt1.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AdminTambahDataSiswaFragment : Fragment() {

    private lateinit var edtNis: EditText
    private lateinit var edtNamaLengkap: EditText
    private lateinit var spinnerJenisKelamin: Spinner
    private lateinit var edtKelas: EditText
    private lateinit var edtJurusan: EditText
    private lateinit var edtTempatPKL: EditText
    private lateinit var edtMulaiPKL: EditText
    private lateinit var edtSelesaiPKL: EditText
    private lateinit var edtPembimbingPerusahaan: EditText
    private lateinit var edtNoHpPembimbingPerusahaan: EditText
    private lateinit var edtPembimbingSekolah: EditText
    private lateinit var edtNoHpPembimbingSekolah: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnPilihFoto: Button
    private lateinit var imgPreview: ImageView

    private var selectedBitmap: Bitmap? = null
    private val PICK_IMAGE_REQUEST = 1
    private val URL_TAMBAH = "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/data_siswa_admin/tambah_data_siswa.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_tambah_data_siswa, container, false)

        // Inisialisasi view
        edtNis = view.findViewById(R.id.edtNis)
        edtNamaLengkap = view.findViewById(R.id.edtNamaLengkap)
        spinnerJenisKelamin = view.findViewById(R.id.spinnerJenisKelamin)
        edtKelas = view.findViewById(R.id.edtKelas)
        edtJurusan = view.findViewById(R.id.edtJurusan)
        edtTempatPKL = view.findViewById(R.id.edtTempatPKL)
        edtMulaiPKL = view.findViewById(R.id.edtMulaiPKL)
        edtSelesaiPKL = view.findViewById(R.id.edtSelesaiPKL)
        edtPembimbingPerusahaan = view.findViewById(R.id.edtPembimbingPerusahaan)
        edtNoHpPembimbingPerusahaan = view.findViewById(R.id.edtNoHpPembimbingPerusahaan)
        edtPembimbingSekolah = view.findViewById(R.id.edtPembimbingSekolah)
        edtNoHpPembimbingSekolah = view.findViewById(R.id.edtNoHpPembimbingSekolah)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        btnPilihFoto = view.findViewById(R.id.btnPilihFoto)
        imgPreview = view.findViewById(R.id.imgPreview)

        // Setup Spinner Jenis Kelamin
        val jkOptions = arrayOf("Laki-laki", "Perempuan")
        spinnerJenisKelamin.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            jkOptions
        )

        // Pilih tanggal Mulai PKL
        edtMulaiPKL.setOnClickListener { showDatePicker(edtMulaiPKL) }

        // Pilih tanggal Selesai PKL
        edtSelesaiPKL.setOnClickListener { showDatePicker(edtSelesaiPKL) }

        // Pilih foto
        btnPilihFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Simpan data
        btnSimpan.setOnClickListener {
            uploadData()
        }

        return view
    }

    private fun showDatePicker(target: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                calendar.set(year, month, dayOfMonth)
                target.setText(sdf.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            imgPreview.setImageBitmap(selectedBitmap)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    private fun uploadData() {
        if (selectedBitmap == null) {
            Toast.makeText(requireContext(), "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        val fotoBase64 = bitmapToBase64(selectedBitmap!!)

        val request = object : StringRequest(
            Request.Method.POST, URL_TAMBAH,
            { response ->
                Toast.makeText(requireContext(), response, Toast.LENGTH_LONG).show()
                if (response.contains("berhasil", ignoreCase = true)) {
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragment_container, AdminDataSiswaFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["nis"] = edtNis.text.toString()
                params["nama_lengkap"] = edtNamaLengkap.text.toString()
                params["jenis_kelamin"] = spinnerJenisKelamin.selectedItem.toString()
                params["kelas"] = edtKelas.text.toString()
                params["jurusan"] = edtJurusan.text.toString()
                params["tempat_pkl"] = edtTempatPKL.text.toString()
                params["mulai_pkl"] = edtMulaiPKL.text.toString()
                params["selesai_pkl"] = edtSelesaiPKL.text.toString()
                params["pembimbing_perusahaan"] = edtPembimbingPerusahaan.text.toString()
                params["no_hp_pembimbing_perusahaan"] = edtNoHpPembimbingPerusahaan.text.toString()
                params["pembimbing_sekolah"] = edtPembimbingSekolah.text.toString()
                params["no_hp_pembimbing_sekolah"] = edtNoHpPembimbingSekolah.text.toString()
                params["foto"] = fotoBase64
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }
}
