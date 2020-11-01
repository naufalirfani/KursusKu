package com.example.kwuapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    var isShow: Boolean = false

    private lateinit var auth: FirebaseAuth

    private  var email: String? = null
    private var username: String = ""
    private lateinit var password: String
    var dataAkun: UserAkun? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar?.hide()

        iv_masuk_mata.setOnClickListener {
            if (isShow){
                et_masuk_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                iv_masuk_mata.background = getDrawable(R.drawable.matanutup)
                isShow = false
            }
            else{
                et_masuk_password.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                iv_masuk_mata.background = getDrawable(R.drawable.matabuka)
                isShow = true
            }
        }

        btn_masuk_back.setOnClickListener { onBackPressed() }

        tv_daftar.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        btn_masuk.setOnClickListener {
            getEmail()
            password = et_masuk_password.text.toString()
            var iterator = 0

            val progressDialog = ProgressDialog(this)
            progressDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            progressDialog.isIndeterminate = true
            progressDialog.setCancelable(true)
            progressDialog.show()
            progressDialog.setContentView(R.layout.progressdialog)


            if(TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
                progressDialog.dismiss()
                Toast.makeText(this@SignInActivity, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }
            else{
                getEmail()
                email?.let { it1 ->
                    auth.signInWithEmailAndPassword(it1, password).addOnCompleteListener(this, OnCompleteListener { task ->
                        if(task.isSuccessful) {
                            progressDialog.dismiss()
                            val username = et_masuk_username.text.toString()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("username",username)
                            startActivity(intent)
                            finish()
                        }else {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Invalid login, please try again", Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }
    }

    fun getEmail(){
        if(et_masuk_username.text.toString().contains("@")){
            email = et_masuk_username.text.toString()
        }
        if(!(et_masuk_username.text.toString().contains("@"))){
            username = et_masuk_username.text.toString()
            loadUser(username)
        }
    }

    private fun loadUser(username: String){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(username)
            .get()
            .addOnSuccessListener { result ->
                dataAkun =  UserAkun(result.getString("username")!!,
                        result.getString("email")!!)

                if(dataAkun?.username?.isNotEmpty()!!){
                    email = dataAkun?.username
                }
                else{
                    loadUser(username)
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
                    loadUser(username)

                }
                snackBar.show()
            }
    }
}
