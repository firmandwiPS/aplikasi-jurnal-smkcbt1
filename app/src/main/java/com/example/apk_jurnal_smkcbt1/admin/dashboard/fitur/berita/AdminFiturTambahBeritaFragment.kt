package com.example.apk_jurnal_smkcbt1.admin.dashboard.fitur.berita

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import org.json.JSONArray
import java.io.ByteArrayOutputStream

class AdminFiturTambahBeritaFragment : Fragment() {

    private lateinit var etJudulSingkat: EditText
    private lateinit var etJudulLengkap: EditText
    private lateinit var etIsi: EditText
    private lateinit var spinnerKategori: Spinner
    private lateinit var btnUploadFoto: Button
    private lateinit var btnSimpan: Button
    private lateinit var linearFoto: LinearLayout

    private val gambarList = mutableListOf<String>()
    private val PICK_IMAGE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_fitur_tambah_berita, container, false)

        etJudulSingkat = view.findViewById(R.id.etJudulSingkat)
        etJudulLengkap = view.findViewById(R.id.etJudulLengkap)
        etIsi = view.findViewById(R.id.etIsi)
        spinnerKategori = view.findViewById(R.id.spinnerKategori)
        btnUploadFoto = view.findViewById(R.id.btnUploadFoto)
        btnSimpan = view.findViewById(R.id.btnSimpan)
        linearFoto = view.findViewById(R.id.linearFoto)

        val kategoriList = listOf("Tentang Sekolah", "Pengumuman", "Kegiatan", "Prestasi", "Lainnya")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKategori.adapter = adapter

        btnUploadFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        btnSimpan.setOnClickListener { simpanBerita() }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            val encoded = encodeToBase64(bitmap)
            gambarList.add(encoded)

            val imageView = ImageView(requireContext())
            imageView.setImageBitmap(bitmap)
            imageView.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                setMargins(8, 8, 8, 8)
            }
            linearFoto.addView(imageView)
        }
    }

    private fun encodeToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    private fun simpanBerita() {
        val url = "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/dashboard/fitur/admin_berita/admin_tambah_berita.php"

        val request = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                Toast.makeText(requireContext(), "Berhasil: $response", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            },
            { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["judul_singkat"] = etJudulSingkat.text.toString()
                params["judul_lengkap"] = etJudulLengkap.text.toString()
                params["isi"] = etIsi.text.toString()
                params["kategori"] = spinnerKategori.selectedItem.toString()
                params["gambar_list"] = JSONArray(gambarList).toString()
                return params
            }
        }

        Volley.newRequestQueue(requireContext()).add(request)
    }
}