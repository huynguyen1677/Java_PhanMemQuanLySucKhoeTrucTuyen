package com.example.applan1

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivityUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class UserInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Set up DatePicker for the date of birth field
        binding.dobEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.dobEditText.setText(dob) // Set the selected date in the EditText
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        binding.saveButton.setOnClickListener {
            val userId = firebaseAuth.currentUser?.uid
            val name = binding.nameEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            val dob = binding.dobEditText.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && dob.isNotEmpty()) {
                val userInfo = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "address" to address,
                    "dob" to dob
                )

                // Save user info to Firestore
                userId?.let {
                    firestore.collection("users").document(it)
                        .set(userInfo)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Thông tin đã được lưu!", Toast.LENGTH_SHORT).show()
                            finish() // Close UserInfoActivity and return to MainActivity
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Lỗi: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
