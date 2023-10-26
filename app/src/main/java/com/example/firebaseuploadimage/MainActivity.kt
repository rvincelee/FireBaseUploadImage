package com.example.firebaseuploadimage

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseuploadimage.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var chooseImageBtn: Button
    lateinit var uploadImageBtn: Button
    lateinit var imageView: ImageView
    var fileUri: Uri? = null
    val progressBar: ProgressBar by lazy { findViewById(R.id.idProgressBar) }

    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chooseImageBtn = findViewById(R.id.idBtnChooseImage)
        uploadImageBtn = findViewById(R.id.idBtnUploadImage)
        imageView = findViewById(R.id.idIVImage)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                if (data != null && data.data != null) {
                    fileUri = data.data
                    try {
                        val source = ImageDecoder.createSource(contentResolver, fileUri!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        chooseImageBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(intent)
        }

        uploadImageBtn.setOnClickListener {
            uploadImage()
        }
    }

    fun uploadImage() {
        if (fileUri != null) {
            progressBar.visibility = View.VISIBLE

            val ref: StorageReference = FirebaseStorage.getInstance().getReference()
                .child(UUID.randomUUID().toString())

            ref.putFile(fileUri!!).addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Image Uploaded.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Fail to Upload Image.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
