package com.example.kwuapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_keranjang.*
import kotlinx.android.synthetic.main.activity_keranjang.actionbar
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.File
import java.io.IOException

@Suppress("DEPRECATION")
class SettingActivity : AppCompatActivity() {

    lateinit var userDetail: UserDetail
    private var filePath: Uri? = null
    private lateinit var currentPhotoPath: String
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private lateinit var dbReference3: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        supportActionBar?.hide()

        userDetail = intent.getParcelableExtra("userDetail")!!

        firebaseDatabase = FirebaseDatabase.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            userId = user.uid
        }

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.pengaturan_akun)
        actionbar.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"))
        btnBack.background = resources.getDrawable(R.drawable.ic_arrow_back_white_24dp)

        if(userDetail.gambar != "kosong"){
            Glide.with(applicationContext)
                .load(userDetail.gambar)
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL))
                .into(img_my_photo)
        }
        else{
            Glide.with(applicationContext)
                .load(resources.getDrawable(R.drawable.akun))
                .apply(
                    RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                        Target.SIZE_ORIGINAL))
                .into(img_my_photo)
        }

        change_photo.setOnClickListener {
            dispatchTakePictureIntent()
        }

        btn_setting_batal.setOnClickListener { onBackPressed() }
    }

    val REQUEST_TAKE_PHOTO = 100
    private fun dispatchTakePictureIntent() {
        val options =
            arrayOf<CharSequence>("Camera", "Choose from Gallery", "Delete")
        val items = arrayOf(
            Item("Camera", R.drawable.photo),
            Item("Choose from gallery", R.drawable.gallery),
            Item("Delete", R.drawable.delete))
        val adapter: ListAdapter = object : ArrayAdapter<Item?>(
            this,
            android.R.layout.select_dialog_item,
            android.R.id.text1,
            items) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                //Use super class to create the View
                val v: View = super.getView(position, convertView, parent)
                val tv = v.findViewById(android.R.id.text1) as TextView
                tv.setTextSize(16F)

                //Put the image on the TextView
                val image: Drawable
                val res: Resources = resources
                image = res.getDrawable(items.get(position).icon)
                image.setBounds(0, 0, 100, 100)
                tv.setCompoundDrawables(image, null, null, null)

                //Add margin between image and text (support various screen densities)
                val dp5 = (15 * resources.displayMetrics.density + 0.5f).toInt()
                tv.compoundDrawablePadding = dp5

                return v
            }
        }
        val cw = ContextThemeWrapper(this, R.style.AlertDialogTheme)
        AlertDialog.Builder(cw)
            .setAdapter(adapter) { dialog, item ->
                when {
                    options[item] == "Camera" -> {
                        Toast.makeText(this,"Open Camera", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    options[item] == "Choose from Gallery" -> {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101)
                        dialog.dismiss()
                    }
                    options[item] == "Delete" -> {
                        Glide.with(applicationContext)
                            .load(resources.getDrawable(R.drawable.akun))
                            .apply(
                                RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                                    Target.SIZE_ORIGINAL))
                            .into(img_my_photo)

                        btn_setting_simpan.setOnClickListener {
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users2").document(userId)
                                .update("gambar", "kosong")
                                .addOnSuccessListener { result ->
                                }
                                .addOnFailureListener { exception ->
                                }
                            Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
                        }
                        dialog.dismiss()
                    }
                }
            }.show()
    }

    class Item(val text: String, val icon: Int) {
        override fun toString(): String {
            return text
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> if (resultCode == Activity.RESULT_OK) {
                val imgFile = File(currentPhotoPath)
                filePath = Uri.fromFile(imgFile)
            }
            101 -> if (resultCode == Activity.RESULT_OK) {
                filePath = data!!.data
                Glide.with(applicationContext)
                    .load(filePath)
                    .apply(
                        RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                            Target.SIZE_ORIGINAL))
                    .into(img_my_photo)

                btn_setting_simpan.setOnClickListener {
                    uploadImage()
                }
            }
        }
    }

    private fun uploadImage(){
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference3 = firebaseDatabase.getReference("imageProfil")
        val progressDialog = ProgressDialog(this)
        progressDialog.show()
        if(filePath != null){
            val ref = storageReference?.child("imageProfile/${userId}/foto")
            ref?.putFile(filePath!!)?.addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    writeNewImageInfoToDB(url)
                }.addOnFailureListener {}
                progressDialog.dismiss()
                Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
            }?.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Image Uploading Failed " + e.message, Toast.LENGTH_LONG)
                    .show()
            }?.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
            }
        }else{
            Toast.makeText(this, "Silahkan pilih foto atau gambar", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeNewImageInfoToDB(url: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users2").document(userId)
            .update("gambar", url)
            .addOnSuccessListener { result ->
            }
            .addOnFailureListener { exception ->
            }
    }
}
