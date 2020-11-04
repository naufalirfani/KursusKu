package com.example.kwuapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
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

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    var isShow: Boolean = false

    private lateinit var auth: FirebaseAuth

    private  var email: String? = null
    private var username: String = ""
    private lateinit var password: String
    var dataAkun: UserAkun? = null
    lateinit var progressDialog: ProgressDialog

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
        var iterator = 0

        progressDialog = ProgressDialog(this)
        progressDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(true)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progressdialog)
        getEmail()
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getEmail(){
        if(et_masuk_username.text.toString().contains("@")){
            email = et_masuk_username.text.toString()
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

                if(dataAkun?.email?.isNotEmpty()!!){
                    email = dataAkun?.email
                    email?.let { it1 ->
                        auth.signInWithEmailAndPassword(it1, password).addOnCompleteListener(this, OnCompleteListener { task ->
                            if(task.isSuccessful) {
                                progressDialog.dismiss()
                                val username2 = et_masuk_username.text.toString()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("username",username2)
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
