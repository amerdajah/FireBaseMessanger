@file:Suppress("DEPRECATION")

package com.example.nicefirebasemessanger.registerlogin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity
import com.example.nicefirebasemessanger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var fireBaseAuth: FirebaseAuth
    private var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("please wait, try to create new user...")
        fireBaseAuth = FirebaseAuth.getInstance()


        already_have_an_account.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        button_register.setOnClickListener {
            val email = editText_email_register.text.toString()
            val password = editText_password_register.text.toString()

            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener
            registerNewUser(email, password)
        }

        button_select_photo_register.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun registerNewUser(email: String, password: String) {
        progressDialog.show()
        fireBaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    progressDialog.dismiss()
                    return@addOnCompleteListener
                } else {
                    //Toast.makeText(this, "Successfully created user with uid: ${it.result!!.user!!.uid}", Toast.LENGTH_LONG).show()
                    uploadImageToFireBaseStorage()
                }
            }
    }

    private fun uploadImageToFireBaseStorage() {
        if (selectedPhotoUri == null) {
            saveUserToFireBaseDataBase("https://firebasestorage.googleapis.com/v0/b/fir-b3607.appspot.com/o/images%2FdefaultImage.png?alt=media&token=5b3b337b-c8a0-4851-89a6-fb6468840442")
        } else {
            val fileName = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/${fileName}")

            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    //Toast.makeText(this, "Successfully uploaded the image", Toast.LENGTH_LONG).show()
                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFireBaseDataBase(it.toString())
                    }
                }
                .addOnFailureListener {
                    //Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun saveUserToFireBaseDataBase(profileImageUri: String) {
        val uid = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        val user = User(
            uid,
            editText_userName_register.text.toString(),
            profileImageUri
        )

        ref.setValue(user)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null){
                    selectedPhotoUri = data.data!!
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
                    imageView_circle_register.setImageBitmap(bitmap)
                    button_select_photo_register.alpha = 0f
                }
            }
        }
    }
}
