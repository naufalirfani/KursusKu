package com.WarnetIT.kursusku

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_keranjang.actionbar
import kotlinx.android.synthetic.main.activity_setting.*
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class SettingActivity : AppCompatActivity() {

    private lateinit var userDetail: UserDetail
    private var filePath: Uri? = null
    private lateinit var currentPhotoPath: String
    private lateinit var auth: FirebaseAuth
    private var userId: String = ""
    private lateinit var dbReference3: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var storageReference: StorageReference? = null
    private val MY_PERMISSIONS_REQUEST_CAMERA = 100
    private val CAMERA_REQUEST = 1888
    private val ALLOW_KEY = "ALLOWED"
    private val CAMERA_PREF = "camera_pref"
    private var mImageUri: Uri? = null
    private var isSave: Boolean = true
    private var isWaSave: Boolean = true
    private var photo: Bitmap? = null

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
        btnBack.setOnClickListener { isDisimpan() }
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

        btn_setting_batal.setOnClickListener { isDisimpan() }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (getFromPref(this, ALLOW_KEY)!!) {
                showSettingsAlert()
            } else if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CAMERA
                    )
                ) {
                    showAlert()
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CAMERA),
                        MY_PERMISSIONS_REQUEST_CAMERA
                    )
                }
            }
        }

        settingnowa()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            isDisimpan()
            true
        } else super.onKeyDown(keyCode, event)
    }

    private fun settingnowa(){
        et_setting_wa.setOnClickListener { et_setting_wa.isCursorVisible = true }
        et_setting_wa.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    et_setting_wa.isCursorVisible = false
                    closeKeyBoard()
                    true
                }
                else -> false
            }
        }
        et_setting_wa.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().contains("+")){
                    isWaSave = false
                    tv_setting_wasalah.visibility = View.INVISIBLE
                    cv_nominal.background = resources.getDrawable(R.drawable.border_grey_1dp)
                    btn_setting_simpan.setOnClickListener {
                        if(!isSave && !isWaSave){
                            saveWa()
                            isWaSave = true
                            uploadImage()
                            isSave = true
                        }
                        else if(!isSave){
                            uploadImage()
                            isSave = true
                        }
                        else if(!isWaSave){
                            saveWa()
                            isWaSave = true
                            Toast.makeText(applicationContext, "Perubahan disimpan", Toast.LENGTH_LONG).show()
                        }

                        btn_setting_simpan.setOnClickListener {
                            Toast.makeText(applicationContext, "Tidak ada perubahan", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    tv_setting_wasalah.visibility = View.VISIBLE
                    cv_nominal.background = resources.getDrawable(R.drawable.border_red_1dp)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        if(userDetail.wa != "kosong"){
            et_setting_wa.setText(userDetail.wa)
        }
    }

    private fun closeKeyBoard() {

        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

        }
    }

    private fun isDisimpan(){
        if(isSave){
            onBackPressed()
        }
        else{
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setMessage("Perubahan belum disimpan. Apakah Anda ingin kembali?")

            builder.setPositiveButton("Ya"
            ) { dialog, which -> // Do nothing but close the dialo

                onBackPressed()
                dialog.dismiss()
            }

            builder.setNegativeButton("Tidak"
            ) { dialog, which -> // Do nothing
                dialog.dismiss()
            }

            val alert: AlertDialog = builder.create()
            alert.setOnShowListener {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.colorAbuGelap))
            }
            alert.show()
        }
    }

    private fun saveWa(){
        if(tv_setting_wasalah.visibility == View.INVISIBLE){
            val db2 = FirebaseFirestore.getInstance()
            db2.collection("users2").document(userId)
                .update("wa", et_setting_wa.text.toString())
                .addOnSuccessListener { result2 ->
                }
                .addOnFailureListener { exception ->
                }
        }
    }

    fun saveToPreferences(
        context: Context,
        key: String?,
        allowed: Boolean?
    ) {
        val myPrefs: SharedPreferences = context.getSharedPreferences(
            CAMERA_PREF,
            Context.MODE_PRIVATE
        )
        val prefsEditor: SharedPreferences.Editor = myPrefs.edit()
        if (allowed != null) {
            prefsEditor.putBoolean(key, allowed)
        }
        prefsEditor.apply()
    }

    fun getFromPref(context: Context, key: String?): Boolean? {
        val myPrefs = context.getSharedPreferences(
            CAMERA_PREF,
            Context.MODE_PRIVATE
        )
        return myPrefs.getBoolean(key, false)
    }

    private fun showAlert() {
        val alertDialog =
            AlertDialog.Builder(this@SettingActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Camera.")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW"
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "ALLOW"
        ) { dialog, which ->
            dialog.dismiss()
            ActivityCompat.requestPermissions(
                this@SettingActivity,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        }
        alertDialog.show()
    }

    private fun showSettingsAlert() {
        val alertDialog =
            AlertDialog.Builder(this@SettingActivity).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("App needs to access the Camera.")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW"
        ) { dialog, which ->
            dialog.dismiss()
            //finish();
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "SETTINGS"
        ) { dialog, which ->
            dialog.dismiss()
            startInstalledAppDetailsActivity(this@SettingActivity)
        }
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                var i = 0
                val len = permissions.size
                while (i < len) {
                    val permission = permissions[i]
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        val showRationale: Boolean =
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                this, permission!!
                            )
                        if (showRationale) {
                            showAlert()
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(this@SettingActivity, ALLOW_KEY, true)
                        }
                    }
                    i++
                }
            }
        }
    }

    fun startInstalledAppDetailsActivity(context: Activity?) {
        if (context == null) {
            return
        }
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + context.packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        context.startActivity(i)
    }

    private fun openCamera() {
        val cameraIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

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
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            if (getFromPref(this, ALLOW_KEY)!!) {
                                showSettingsAlert()
                            } else if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.CAMERA
                                )
                                != PackageManager.PERMISSION_GRANTED
                            ) {

                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        this,
                                        Manifest.permission.CAMERA
                                    )
                                ) {
                                    showAlert()
                                } else {
                                    // No explanation needed, we can request the permission.
                                    ActivityCompat.requestPermissions(
                                        this, arrayOf(Manifest.permission.CAMERA),
                                        MY_PERMISSIONS_REQUEST_CAMERA
                                    )
                                }
                            }
                        } else {
                            openCamera()
                        }
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

                        isSave = false
                        btn_setting_simpan.setOnClickListener {
                            if(!isSave && !isWaSave){
                                saveWa()
                                isWaSave = true
                                val db = FirebaseFirestore.getInstance()
                                db.collection("users2").document(userId)
                                    .update("gambar", "kosong")
                                    .addOnSuccessListener { result ->
                                    }
                                    .addOnFailureListener { exception ->
                                    }
                                Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
                                isSave = true
                            }
                            else if(!isSave){
                                val db = FirebaseFirestore.getInstance()
                                db.collection("users2").document(userId)
                                    .update("gambar", "kosong")
                                    .addOnSuccessListener { result ->
                                    }
                                    .addOnFailureListener { exception ->
                                    }
                                Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
                                isSave = true
                            }
                            else if(!isWaSave){
                                saveWa()
                                isWaSave = true
                                Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
                            }

                            btn_setting_simpan.setOnClickListener {
                                Toast.makeText(this, "Tidak ada perubahan", Toast.LENGTH_LONG).show()
                            }
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
            CAMERA_REQUEST -> if (resultCode == Activity.RESULT_OK) {
//                val imgFile = File(currentPhotoPath)
//                filePath = Uri.fromFile(imgFile)
                val imageBitmap: Bitmap? = data!!.extras!!["data"] as Bitmap?
                photo = imageBitmap
                img_my_photo.setImageBitmap(imageBitmap)

                isSave = false
                btn_setting_simpan.setOnClickListener {
                    submit()
                    isSave = true

                    btn_setting_simpan.setOnClickListener {
                        Toast.makeText(this, "Tidak ada perubahan", Toast.LENGTH_LONG).show()
                    }
                }
            }
            101 -> if (resultCode == Activity.RESULT_OK) {
                filePath = data!!.data
                Glide.with(applicationContext)
                    .load(filePath)
                    .apply(
                        RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(
                            Target.SIZE_ORIGINAL))
                    .into(img_my_photo)

                isSave = false
                btn_setting_simpan.setOnClickListener {
                    if(!isSave && !isWaSave){
                        saveWa()
                        isWaSave = true
                        uploadImage()
                        isSave = true
                    }
                    else if(!isSave){
                        uploadImage()
                        isSave = true
                    }
                    else if(!isWaSave){
                        saveWa()
                        isWaSave = true
                        Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
                    }

                    btn_setting_simpan.setOnClickListener {
                        Toast.makeText(this, "Tidak ada perubahan", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun uploadImage(){
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference3 = firebaseDatabase.getReference("imageProfil")
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialoguploadphoto)
        progressDialog.setCancelable(true)
        progressDialog.show()
        val progressbarUpload:ProgressBar = progressDialog.findViewById(R.id.setting_progress_bar)

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
                progressbarUpload.progress = progress.toInt()
            }
        }else{
            Toast.makeText(this, "Silahkan pilih foto atau gambar", Toast.LENGTH_LONG).show()
        }
    }

    fun submit() {
        val progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialoguploadphoto)
        progressDialog.setCancelable(true)
        progressDialog.show()
        val progressbarUpload:ProgressBar = progressDialog.findViewById(R.id.setting_progress_bar)

        val stream = ByteArrayOutputStream()
        photo?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val b: ByteArray = stream.toByteArray()
        val storageReference = FirebaseStorage.getInstance().reference.child("imageProfile/${userId}/foto")
        //StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
        storageReference.putBytes(b)
            .addOnSuccessListener { p0 ->
                storageReference.downloadUrl.addOnSuccessListener {
                    val url = it.toString()
                    writeNewImageInfoToDB(url)
                }.addOnFailureListener {}
                progressDialog.dismiss()
                Toast.makeText(this, "Perubahan disimpan", Toast.LENGTH_LONG).show()
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Image Uploading Failed " + e.message, Toast.LENGTH_LONG)
                    .show()
            }.addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressbarUpload.progress = progress.toInt()
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
