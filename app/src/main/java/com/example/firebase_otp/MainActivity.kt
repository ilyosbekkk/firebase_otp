package com.example.firebase_otp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private  val TAG = "MainActivity"
    lateinit var editText: EditText
    lateinit var result:TextView
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.editText)
        result = findViewById(R.id.result)
        button = findViewById(R.id.button)

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                result.setText("Done -> ${credential}")
                Log.e("TAG", "onVerificationCompleted: " + credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "onVerificationFailed: ${e}" )
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.e(TAG, "onVerificationFailed: ${e}")
                }
            }

        }

        button.setOnClickListener {
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(editText.text.toString())       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }




    }
}