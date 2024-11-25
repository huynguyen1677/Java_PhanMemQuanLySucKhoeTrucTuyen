package com.example.applan1
import android.app.DatePickerDialog
import android.widget.ArrayAdapter

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivityCompleteProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCompleteProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompleteProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            // Người dùng chưa đăng nhập, chuyển đến màn hình đăng nhập
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Cài đặt Spinner cho giới tính
        val genderOptions = resources.getStringArray(R.array.gender_options) // Mảng giới tính
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        binding.genderSpinner.adapter = genderAdapter

        // Cài đặt DatePicker cho ngày sinh
        binding.dobEt.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                val dob = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.dobEt.setText(dob)
            }, year, month, day)

            datePickerDialog.show()
        }

        // Xử lý khi nhấn nút Lưu
        binding.buttonSave.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val phone = binding.phoneEt.text.toString()
            val address = binding.addressEt.text.toString()
            val gender = binding.genderSpinner.selectedItem.toString() // Giới tính đã chọn
            val dob = binding.dobEt.text.toString() // Ngày sinh đã chọn

            if (name.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && gender.isNotEmpty() && dob.isNotEmpty()) {
                val userInfo = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "address" to address,
                    "gender" to gender,
                    "dob" to dob
                )

                firestore.collection("users").document(userId!!)  // Lưu thông tin vào Firestore
                    .set(userInfo)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Thông tin đã được cập nhật!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

