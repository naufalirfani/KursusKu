package com.WarnetIT.kursusku

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlin.random.Random


@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

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
    private var arrayUser: ArrayList<String> = arrayListOf()

    private val RC_SIGN_IN = 234
    var mGoogleSignInClient: GoogleSignInClient? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mAuth: FirebaseAuth? = null

    private var mCallbackManager: CallbackManager? = null

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

        setGooglePlusButtonText(sign_in_button_google, "Google")
        mAuth = FirebaseAuth.getInstance()

        //Then we need a GoogleSignInOptions object
        //And we need to build it as below
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        //Then we will get the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        sign_in_button_google.setOnClickListener {
            signInGoogle()
        }

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(
                Auth.GOOGLE_SIGN_IN_API,
                gso
            )
            .build()

        FacebookSdk.sdkInitialize(applicationContext)
        mCallbackManager = CallbackManager.Factory.create()
        auth = FirebaseAuth.getInstance()

        val loginButton: LoginButton = findViewById(R.id.btn_login_fb)
        loginButton.setReadPermissions("email", "public_profile")
        loginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(loginResult: LoginResult?) {
                handleFacebookAccessToken(loginResult!!.accessToken)
            }
        })
    }

    override fun onStart() {
        super.onStart()

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth!!.currentUser != null) {
            finish()
            loadKursus2()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun masuk(){
        closeKeyBoard()
        auth = FirebaseAuth.getInstance()
        password = et_masuk_password.text.toString()
        loadingShow()
        loadKursus()
    }

    private fun loadingShow(){
        progressDialog = ProgressDialog(this)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(true)
        progressDialog.show()
        progressDialog.setContentView(R.layout.progressdialog)
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getEmail(array: ArrayList<DataKursus>, array2: ArrayList<DataKursus>, array3: ArrayList<DataKursus>, kat: String, kat2: String){
        if(et_masuk_username.text.toString().contains("@")){
            email = et_masuk_username.text.toString()
            email?.let { it1 ->
                auth.signInWithEmailAndPassword(it1, password).addOnCompleteListener(this, OnCompleteListener { task ->
                    if(task.isSuccessful) {
                        val username = et_masuk_username.text.toString()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("username",username)
                        intent.putExtra("arrayList", array)
                        intent.putExtra("arrayList2", array2)
                        intent.putExtra("arrayList3", array3)
                        intent.putExtra("kategori1", kat)
                        intent.putExtra("kategori2", kat2)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }else {
                        progressDialog.dismiss()
                        Toast.makeText(this, "Username $email tidak ditemukan atau password salah.", Toast.LENGTH_SHORT).show()
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
                    dataAkun =  UserAkun(result.getString("username")!!, result.getString("email")!!, result.getString("userId"))
                }
                catch (e: Exception){
                    Toast.makeText(this, "Username $username tidak ditemukan atau password salah.", Toast.LENGTH_SHORT).show()
                }

                try{
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
                                    Toast.makeText(this, "Username $username tidak ditemukan atau password salah.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }
                    else{
                        loadUser(username, array, array2, array3, kat, kat2)
                    }
                }
                catch (e: java.lang.Exception){
                    progressDialog.dismiss()
                    Toast.makeText(this, "Username $username tidak ditemukan atau password salah.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                val snackBar = Snackbar.make(
                    currentFocus!!, "    Connection Failure.",
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
                Toast.makeText(this, "Koneksi error.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadKursus2(){
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
                    progressDialog.dismiss()
                    val username2 = et_masuk_username.text.toString()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("username",username2)
                    intent.putExtra("arrayList", arrayList)
                    intent.putExtra("arrayList2", arrayList2)
                    intent.putExtra("arrayList3", arrayList3)
                    intent.putExtra("kategori1", kategori1)
                    intent.putExtra("kategori2", kategori2)
                    startActivity(intent)
                    finish()
                }
                else{
                    loadKursus2()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Koneksi error.", Toast.LENGTH_SHORT).show()
            }
    }

    fun randomAngka(){
        angka1 = Random.nextInt(0,5)
        angka2 = Random.nextInt(0,5)
        if(angka1 == angka2){
            randomAngka()
        }
    }

    private fun setGooglePlusButtonText(signInButton: SignInButton, buttonText: String?) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (i in 0 until signInButton.childCount) {
            val v: View = signInButton.getChildAt(i)
            if (v is TextView) {
                v.text = buttonText
                v.setPadding(48,0,0,0)
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Google Sign In was successful, authenticate with Firebase
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                //authenticating with firebase
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error: $e", Toast.LENGTH_LONG).show()
            }
        }
        else{
            mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        //getting the auth credential
        val credential: AuthCredential = GoogleAuthProvider.getCredential(acct.idToken, null)

        //Now using firebase we are signing in the user here
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful()) {
                    val user: FirebaseUser? = mAuth!!.currentUser
                    loadingShow()
                    checkUser(user)
                } else {
                    Toast.makeText(this@SignInActivity, "Email telah digunakan.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    //this method is called on click
    private fun signInGoogle() {
        //getting the google signin intent
        val signInIntent = mGoogleSignInClient!!.signInIntent
        mGoogleApiClient?.clearDefaultAccountAndReconnect()
        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun checkUser(user: FirebaseUser?){
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document("daftarUser")
            .get()
            .addOnSuccessListener { result ->
                arrayUser.clear()
                arrayUser = result.get("listSearch") as ArrayList<String>


                if(arrayUser.isNotEmpty()){
                    if(arrayUser.contains(user?.email)){
                        loadKursus2()
                    }
                    else{
                        val userDetail = UserDetail(user?.displayName!!,
                            user.email!!,
                            user.photoUrl!!.toString(),
                            "0",
                            "kosong",
                            "0",
                            "kosong")
                        db.collection("users2").document(user.uid).set(userDetail)

                        val dataBayar = DataPesanan("kosong", 0,0,"kosong",0)
                        db.collection("statusBayar").document(user.uid).set(dataBayar)

                        val listSearch: ArrayList<String> = arrayListOf("kosong")
                        val dataSearch = DataSearch("kosong", listSearch)
                        db.collection("search").document(user.uid).set(dataSearch)

                        arrayUser.add(user.email!!)
                        val data = DataSearch("kosong", arrayUser)
                        db.collection("users").document("daftarUser").set(data)
                        loadKursus2()

                        addUser(user.displayName!!, user.email!!, user.uid)
                    }
                }
                else{
                    checkUser(user)
                }
            }
            .addOnFailureListener { exception ->
                progressDialog.dismiss()
            }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = mAuth!!.currentUser
                    loadingShow()
                    checkUser(user)
                } else {
                    disconnectFromFacebook()
                    Toast.makeText(applicationContext, "Email telah digunakan.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    }

    private fun addUser(name: String, email: String, userId: String){
        val username: String = name
        val db = FirebaseFirestore.getInstance()
        val user = UserAkun(name, email, userId)
        db.collection("users").document(username).set(user)
    }
}
