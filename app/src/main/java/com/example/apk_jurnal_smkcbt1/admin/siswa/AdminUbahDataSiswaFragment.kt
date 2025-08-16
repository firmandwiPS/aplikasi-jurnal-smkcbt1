package com.example.apk_jurnal_smkcbt1.admin.siswa

import android.app.Activity
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
import com.bumptech.glide.Glide
import com.example.apk_jurnal_smkcbt1.R
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AdminUbahDataSiswaFragment : Fragment() {

    private lateinit var edtNis: EditText
    private lateinit var edtNama: EditText
    private lateinit var edtJenisKelamin: EditText
    private lateinit var edtKelas: EditText
    private lateinit var edtJurusan: EditText
    private lateinit var edtTempatPkl: EditText
    private lateinit var edtMulai: EditText
    private lateinit var edtSelesai: EditText
    private lateinit var edtPembimbingPerusahaan: EditText
    private lateinit var edtNoHpPembimbingPerusahaan: EditText
    private lateinit var edtPembimbingSekolah: EditText
    private lateinit var edtNoHpPembimbingSekolah: EditText
    private lateinit var imgFoto: ImageView
    private lateinit var btnPilihFoto: Button
    private lateinit var btnSimpan: Button

    private var selectedBitmap: Bitmap? = null
    private var nis: String? = null

    private val PICK_IMAGE_REQUEST = 1
    private val BASE_URL = "http://192.168.1.103/backend-app-jurnalcbt1/admin_user/data_siswa_admin/ubah_data_siswa.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_ubah_data_siswa, container, false)

        edtNis = view.findViewById(R.id.edtNis)
        edtNama = view.findViewById(R.id.edtNamaLengkap)
        edtJenisKelamin = view.findViewById(R.id.edtJenisKelamin)
        edtKelas = view.findViewById(R.id.edtKelas)
        edtJurusan = view.findViewById(R.id.edtJurusan)
        edtTempatPkl = view.findViewById(R.id.edtTempatPkl)
        edtMulai = view.findViewById(R.id.edtMulaiPkl)
        edtSelesai = view.findViewById(R.id.edtSelesaiPkl)
        edtPembimbingPerusahaan = view.findViewById(R.id.edtPembimbingPerusahaan)
        edtNoHpPembimbingPerusahaan = view.findViewById(R.id.edtNoHpPembimbingPerusahaan)
        edtPembimbingSekolah = view.findViewById(R.id.edtPembimbingSekolah)
        edtNoHpPembimbingSekolah = view.findViewById(R.id.edtNoHpPembimbingSekolah)
        imgFoto = view.findViewById(R.id.imgFoto)
        btnPilihFoto = view.findViewById(R.id.btnPilihFoto)
        btnSimpan = view.findViewById(R.id.btnSimpan)

        nis = arguments?.getString("nis")
        edtNis.setText(nis)

        loadDataSiswa()

        btnPilihFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSimpan.setOnClickListener {
            updateDataSiswa()
        }

        return view
    }

    private fun loadDataSiswa() {
        val url = "$BASE_URL?nis=$nis"
        val request = StringRequest(Request.Method.GET, url, { response ->
            val json = JSONObject(response)
            if(json.getBoolean("success")){
                val data = json.getJSONObject("data")
                edtNama.setText(data.optString("nama_lengkap", ""))
                edtJenisKelamin.setText(data.optString("jenis_kelamin", ""))
                edtKelas.setText(data.optString("kelas", ""))
                edtJurusan.setText(data.optString("jurusan", ""))
                edtTempatPkl.setText(data.optString("tempat_pkl",""))
                edtMulai.setText(data.optString("mulai_pkl",""))
                edtSelesai.setText(data.optString("selesai_pkl",""))
                edtPembimbingPerusahaan.setText(data.optString("pembimbing_perusahaan",""))
                edtNoHpPembimbingPerusahaan.setText(data.optString("no_hp_pembimbing_perusahaan",""))
                edtPembimbingSekolah.setText(data.optString("pembimbing_sekolah",""))
                edtNoHpPembimbingSekolah.setText(data.optString("no_hp_pembimbing_sekolah",""))

                val fotoName = data.optString("foto","")
                if(fotoName.isNotEmpty()){
                    val fotoUrl = "http://192.168.1.103/backend-app-jurnalcbt1/uploads/images_siswa/$fotoName"
                    Glide.with(this).load(fotoUrl).into(imgFoto)
                }
            }
        }, {
            Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
        })
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun updateDataSiswa() {
        val request = object : StringRequest(Method.POST, BASE_URL, { response ->
            val json = JSONObject(response)
            if(json.getBoolean("success")){
                Toast.makeText(requireContext(), "Data berhasil diubah", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), json.getString("message"), Toast.LENGTH_SHORT).show()
            }
        }, {
            Toast.makeText(requireContext(), "Gagal menghubungi server", Toast.LENGTH_SHORT).show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nis"] = edtNis.text.toString()
                params["nama_lengkap"] = edtNama.text.toString()
                params["jenis_kelamin"] = edtJenisKelamin.text.toString()
                params["kelas"] = edtKelas.text.toString()
                params["jurusan"] = edtJurusan.text.toString()
                params["tempat_pkl"] = edtTempatPkl.text.toString()
                params["mulai_pkl"] = edtMulai.text.toString()
                params["selesai_pkl"] = edtSelesai.text.toString()
                params["pembimbing_perusahaan"] = edtPembimbingPerusahaan.text.toString()
                params["no_hp_pembimbing_perusahaan"] = edtNoHpPembimbingPerusahaan.text.toString()
                params["pembimbing_sekolah"] = edtPembimbingSekolah.text.toString()
                params["no_hp_pembimbing_sekolah"] = edtNoHpPembimbingSekolah.text.toString()
                params["foto"] = selectedBitmap?.let { bitmapToBase64(it) } ?: ""
                return params
            }
        }
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            val uri: Uri? = data.data
            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            imgFoto.setImageBitmap(selectedBitmap)
        }
    }
}

