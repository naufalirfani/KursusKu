package com.example.kwuapp


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() {

    var isShow: Boolean = false

    private lateinit var auth: FirebaseAuth
    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        iv_daftar_mata.setOnClickListener {
            if (isShow){
                et_daftar_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                iv_daftar_mata.background = getDrawable(R.drawable.matanutup)
                isShow = false
            }
            else{
                et_daftar_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                iv_daftar_mata.background = getDrawable(R.drawable.matabuka)
                isShow = true
            }
        }

        btn_daftar_back.setOnClickListener { onBackPressed() }

        tv_masuk.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        btn_daftar.setOnClickListener { signUp() }

        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference = firebaseDatabase.getReference("users")
    }

    fun signUp(){
        dbReference = FirebaseDatabase.getInstance().getReference("search")
        val username: String = et_daftar_username.text.toString()
        val email: String = et_daftar_email.text.toString()
        val password: String = et_daftar_password.text.toString()

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
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
                createUser(username, email)
                addUser(username, email)
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show()
                        val handler = Handler()
                        handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 3000)
                    }else {
                        Toast.makeText(this, "Username or rmail already used", Toast.LENGTH_LONG).show()
                    }
                })
            }
            else{
                Toast.makeText(this, "Username or rmail already used", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createUser(name: String, email: String) {
        val username: String = et_daftar_username.text.toString()
        val user = UserAkun(name, email)
        dbReference.child(username).setValue(user)
    }

    private fun addUser(name: String, email: String){
        val username: String = et_daftar_username.text.toString()
        val db = FirebaseFirestore.getInstance()
        val user = UserAkun(name, email)
        db.collection("users").document(username).set(user)
    }
}
