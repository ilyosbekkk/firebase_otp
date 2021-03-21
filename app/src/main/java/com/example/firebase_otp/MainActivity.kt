package com.example.firebase_otp

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var phoneNumber: EditText
    lateinit var name: EditText
    lateinit var email: EditText
    lateinit var progresBar: ProgressBar
    lateinit var result: TextView
    lateinit var button: Button
    lateinit var systemVerification: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phoneNumber = findViewById(R.id.editText)
        name = findViewById(R.id.editText1)
        email = findViewById(R.id.editText2)
        result = findViewById(R.id.result)
        button = findViewById(R.id.button)
        progresBar = findViewById(R.id.progressBar)
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                systemVerification = p0
                signIn_with_credential("980325")
            }
            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Log.e(TAG, "onCodeAutoRetrievalTimeOut: ${p0}")

            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted: ${credential.smsCode}")
                signIn_with_credential(credential.smsCode!!)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "onVerificationFailed: ${e}")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.e(TAG, "onVerificationFailed: ${e}")
                }
            }

        }

        button.setOnClickListener {
            progresBar.visibility = android.view.View.VISIBLE
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNumber.text.toString())       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)     // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    private fun signIn_with_credential(code: String) {
        val credential = PhoneAuthProvider.getCredential(systemVerification, code)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) {
            Log.e(TAG, "signIn: Hey  you  digned  in")
            progresBar.visibility = android.view.View.GONE
            val dialog = AlertDialog.Builder(this, R.style.ThemeOverlay_AppCompat_Dialog)
            dialog.setTitle("CONGRATULATIONS!")
            dialog.setMessage("Name:${name.text}\nEmail:${email.text}\nCode:${credential.smsCode}")
            dialog.show()
        }.addOnFailureListener(this) {
            Toast.makeText(this, "Sign  in with ${credential.smsCode} failed:(", Toast.LENGTH_LONG)
                .show()
        }
    }

}