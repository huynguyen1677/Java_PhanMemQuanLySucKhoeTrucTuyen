package com.example.applan1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if the user is logged in
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            // Check if the user info exists in Firestore
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        // User info does not exist, navigate to UserInfoActivity
                        val intent = Intent(this, UserInfoActivity::class.java)
                        startActivity(intent)
                        finish() // Close MainActivity so user can't return here
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle failure (if needed)
                }
        } ?: run {
            // User is not logged in, navigate to SignInActivity
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Setup logout button
        binding.logoutButton.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close MainActivity
        }
    }
}
