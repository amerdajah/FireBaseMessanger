@file:Suppress("DEPRECATION")

package com.example.nicefirebasemessanger.registerlogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var fireBaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait, try to login...")
        fireBaseAuth = FirebaseAuth.getInstance()

        dont_have_an_account.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        button_login.setOnClickListener {
            val email = editText_email_login.text.toString()
            val password = editText_password_login.text.toString()

            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener
            logUserIn(email, password)
        }
    }

    private fun logUserIn(email: String, password: String){
        progressDialog.show()
        fireBaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                progressDialog.dismiss()
                if (!it.isSuccessful){
                    return@addOnCompleteListener
                }else{
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
    }
}
