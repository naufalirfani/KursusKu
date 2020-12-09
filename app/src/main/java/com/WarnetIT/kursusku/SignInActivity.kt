package com.WarnetIT.kursusku

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.lang.Exception
import kotlin.random.Random

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private var isShow: Boolean = false

    private lateinit var auth: FirebaseAuth

    private  var email: String? = null
    private var username: String = ""
    private lateinit var password: String
    private var dataAkun: UserAkun? = null
    private lateinit var progressDialog: ProgressDialog

    private var arrayList = ArrayList<DataKursus>()
    private var arrayList2 = ArrayList<DataKursus>()
    private var arrayList3 = ArrayList<DataKursus>()
    private val kategori = arrayOf("Desain", "Bisnis", "Finansial", "Kantor", "Pendidikan", "Pengembangan")
    private var angka1: Int = 0
    private var angka2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.hide()

        iv_masuk_mata.setOnClickListener {
            if (isShow){
                et_masuk_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_masuk_mata.background = getDrawable(R.drawable.matanutup)
                isShow = false
            }
            else{
                et_masuk_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_masuk_mata.background = getDrawable(R.drawable.matabuka)
                isShow = true
            }
        }

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.masuk)

        tv_daftar.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        btn_masuk.setOnClickListener { masuk()}

        et_masuk_password.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                masuk()
                return@OnEditorActionListener true
            }
            false
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun masuk(){
        closeKeyBoard()
        auth = FirebaseAuth.getInstance()
        password = et_masuk_password.text.toString()

        progressDialog = ProgressDialog(this)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(true)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progressdialog)
        loadKursus()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getEmail(array: ArrayList<DataKursus>, array2: ArrayList<DataKursus>, array3: ArrayList<DataKursus>, kat: String, kat2: String){
        if(et_masuk_username.text.toString().contains("@")){
            email = et_masuk_username.text.toString()
            email?.let { it1 ->
                auth.signInWithEmailAndPassword(it1, password).addOnCompleteListener(this, OnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val username = et_masuk_username.text.toString()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("username",username)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Invalid login, please try again", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
        if(!(et_masuk_username.text.toString().contains("@"))){
            username = et_masuk_username.text.toString()
            loadUser(username, array, array2, array3, kat, kat2)
        }
    }

    private fun loadUser(username: String,
                         array: ArrayList<DataKursus>,
                         array2: ArrayList<DataKursus>,
                         array3: ArrayList<DataKursus>,
                         kat: String,
                         kat2: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(username)
            .get()
            .addOnSuccessListener { result ->
                try{
                    dataAkun =  UserAkun(result.getString("username")!!,
                        result.getString("email")!!)
                }
                catch (e: Exception){
                    Toast.makeText(this,"Login gagal", Toast.LENGTH_SHORT).show()
                }


                if(dataAkun?.email?.isNotEmpty()!!){
                    email = dataAkun?.email
                    email?.let { it1 ->
                        auth.signInWithEmailAndPassword(it1, password).addOnCompleteListener(this, OnCompleteListener { task ->
                            if(task.isSuccessful) {
                                progressDialog.dismiss()
                                val username2 = et_masuk_username.text.toString()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("username",username2)
                                intent.putExtra("arrayList", array)
                                intent.putExtra("arrayList2", array2)
                                intent.putExtra("arrayList3", array3)
                                intent.putExtra("kategori1", kat)
                                intent.putExtra("kategori2", kat2)
                                startActivity(intent)
                                finish()
                            }else {
                                progressDialog.dismiss()
                                Toast.makeText(this, "Invalid login, please try again", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                }
                else{
                    loadUser(username, array, array2, array3, kat, kat2)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting documents: ", exception)
                val snackBar = Snackbar.make(
                    currentFocus!!, "    Connection Failure",
                    Snackbar.LENGTH_INDEFINITE
                )
                val snackBarView = snackBar.view
                snackBarView.setBackgroundColor(Color.BLACK)
                val textView = snackBarView.findViewById<TextView>(R.id.snackbar_text)
                textView.setTextColor(Color.WHITE)
                textView.textSize = 16F
                textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.warning, 0, 0, 0)
                val snack_action_view = snackBarView.findViewById<Button>(R.id.snackbar_action)
                snack_action_view.setTextColor(Color.YELLOW)

                // Set an action for snack bar
                snackBar.setAction("Retry") {
                    loadUser(username, array, array2, array3, kat, kat2)

                }
                snackBar.show()
            }
    }

    private fun loadKursus(){
        randomAngka()
        val kategori1 = kategori[angka1]
        val kategori2 = kategori[angka2]
        val db = FirebaseFirestore.getInstance()
        db.collection("kursus")
            .get()
            .addOnSuccessListener { result ->
                arrayList.clear()
                arrayList2.clear()
                arrayList3.clear()
                for (document in result) {
                    arrayList.add(DataKursus(document.getString("deskripsi")!!,
                        document.getLong("dilihat")!!,
                        document.getString("gambar")!!,
                        document.getString("harga")!!,
                        document.getString("kategori")!!,
                        document.getString("nama")!!,
                        document.getString("pembuat")!!,
                        document.getLong("pengguna")!!,
                        document.getString("rating")!!,
                        document.getString("remaining")!!))
                }

                if(arrayList.isNotEmpty()){
                    for(i in 0 until arrayList.size){
                        if(arrayList[i].kategori == kategori1){
                            arrayList2.add(arrayList[i])
                        }
                        if(arrayList[i].kategori == kategori2){
                            arrayList3.add(arrayList[i])
                        }
                    }

                    getEmail(arrayList, arrayList2, arrayList3, kategori1, kategori2)
                }
                else{
                    loadKursus()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "Error getting documents: ", exception)
                Toast.makeText(this, "Koneksi error", Toast.LENGTH_SHORT).show()
            }
    }

    fun randomAngka(){
        angka1 = Random.nextInt(0,5)
        angka2 = Random.nextInt(0,5)
        if(angka1 == angka2){
            randomAngka()
        }
    }
}
