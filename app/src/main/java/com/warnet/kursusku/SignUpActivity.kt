package com.warnet.kursusku


import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_up.*


@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private var isShow: Boolean = false

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var emailLogin: String? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        iv_daftar_mata.setOnClickListener {
            if (isShow){
                et_daftar_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                iv_daftar_mata.background = getDrawable(R.drawable.matanutup)
                isShow = false
            }
            else{
                et_daftar_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                iv_daftar_mata.background = getDrawable(R.drawable.matabuka)
                isShow = true
            }
        }

        val btnBack: Button = actionbar.findViewById(R.id.btn_actionbar_back)
        val tvTitle: TextView = actionbar.findViewById(R.id.tv_actionbar)
        btnBack.setOnClickListener { onBackPressed() }
        tvTitle.text = resources.getString(R.string.daftar)

        tv_masuk.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_daftar.setOnClickListener {
            if(tv_daftar_wasalah.visibility == View.VISIBLE){
                Toast.makeText(this, "Harap masukkan No. HP dengan format yang benar.", Toast.LENGTH_LONG).show()
            }
            else{
                signUp()
            }
        }

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference = firebaseDatabase.getReference("users")

        checkIsWATrue()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun checkIsWATrue(){
        et_daftar_hp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().contains("+")){
                    tv_daftar_wasalah.visibility = View.INVISIBLE
                    val colorStateList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
                    ViewCompat.setBackgroundTintList(et_daftar_hp, colorStateList)
                }
                else{
                    tv_daftar_wasalah.visibility = View.VISIBLE
                    val colorStateList = ColorStateList.valueOf(resources.getColor(R.color.colorMerah))
                    ViewCompat.setBackgroundTintList(et_daftar_hp, colorStateList)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    fun signUp(){
        dbReference = FirebaseDatabase.getInstance().getReference("users")
        val username: String = et_daftar_username.text.toString()
        val email: String = et_daftar_email.text.toString()
        val password: String = et_daftar_password.text.toString()
        val noHp: String = et_daftar_hp.text.toString()

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username) || TextUtils.isEmpty(noHp)) {
            Toast.makeText(this, "Harap isi semua.", Toast.LENGTH_LONG).show()
        }
        else{
            var user: UserAkun? = null
            dbReference.child(username).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    user = dataSnapshot.getValue(UserAkun::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
            if(TextUtils.isEmpty(user?.username)){
                progressDialog = ProgressDialog(this)
                progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                progressDialog?.isIndeterminate = true
                progressDialog?.setCancelable(true)
                progressDialog?.show()
                progressDialog?.setContentView(R.layout.progressdialog)

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{ task ->
                    if(task.isSuccessful){
                        val user2 = auth.currentUser

                        createUser(username, email, user2?.uid!!)
                        addUser(username, email, user2.uid)

                        val db = FirebaseFirestore.getInstance()
                        val userDetail = UserDetail(et_daftar_username.text.toString(),
                            et_daftar_email.text.toString(),
                            "kosong",
                            "0",
                            "kosong",
                            "0",
                            et_daftar_hp.text.toString())
                        db.collection("users2").document(user2.uid).set(userDetail)

                        val dataBayar = DataPesanan("kosong", 0,0,"kosong",0)
                        db.collection("statusBayar").document(user2.uid).set(dataBayar)

                        val listSearch: ArrayList<String> = arrayListOf("kosong")
                        val dataSearch = DataSearch("kosong", listSearch)
                        db.collection("search").document(user2.uid).set(dataSearch)

                        Toast.makeText(this, "Terimakasih telah mendaftar.", Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 2000)
                        progressDialog?.hide()
                    }else {
                        progressDialog?.hide()
                        Toast.makeText(this, "Username atau email telah digunakan.", Toast.LENGTH_LONG).show()
                    }
                })
            }
            else{
                progressDialog?.hide()
                Toast.makeText(this, "Username atau rmail telah digunakan.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createUser(name: String, email: String, userId: String) {
        val username: String = et_daftar_username.text.toString()
        val user = UserAkun(name, email, userId)
        dbReference.child(username).setValue(user)
    }

    private fun addUser(name: String, email: String, userId: String){
        val username: String = et_daftar_username.text.toString()
        val db = FirebaseFirestore.getInstance()
        val user = UserAkun(name, email, userId)
        db.collection("users").document(username).set(user)
    }
}
