package com.example.connect.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.connect.model.UserModel
import com.example.connect.viewModel.FireBaseViewModel
import com.example.connect.databinding.ActivityUserDetailBinding
import com.google.firebase.auth.FirebaseAuth

class UserDetailActivity :  AppCompatActivity() {
    lateinit var binding : ActivityUserDetailBinding
    lateinit var auth: FirebaseAuth
    lateinit var viewModel: FireBaseViewModel
    lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[FireBaseViewModel::class.java]
        auth = FirebaseAuth.getInstance()
        viewModel = ViewModelProvider(this)[FireBaseViewModel::class.java]
        viewModel.user().observe(this, Observer {
            binding.TextInputEtBio.setText(it.bio.toString())
            binding.TextInputEtName.setText(it.fullName.toString())
            Glide.with(this).load(it.photoURL).into(binding.ProfilePic)
        })
        binding.ProfilePic.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Update Profile Picture?")
            builder.setMessage("open gallery")
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                val gallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
        binding.btDone.setOnClickListener {
            var User = UserModel(auth.uid, "", "", "")
            User.fullName = binding.TextInputEtName.text.toString()
            User.bio = binding.TextInputEtBio.text.toString()
            User.photoURL = auth.currentUser?.photoUrl.toString()
            viewModel.saveUser(User)
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            imageUri = data?.data!!
            viewModel.uploadProfilePic(imageUri)
            binding.ProfilePic.setImageURI(imageUri)
        }

    }
}