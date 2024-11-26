package com.example.carevault.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carevault.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HealthRecordActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_record)

        // Kiểm tra xem người dùng đã đăng nhập chưa
        val userId = auth.currentUser?.uid
        if (userId == null) {
            // Nếu chưa đăng nhập, chuyển hướng đến màn hình đăng nhập
            // startActivity(Intent(this, LoginActivity::class.java))
            // finish()
            return
        }

        // Lấy tham chiếu đến các TextView để hiển thị thông tin
        val nameTextView: TextView = findViewById(R.id.textViewName)
        val dobTextView: TextView = findViewById(R.id.textViewDOB)
        val genderTextView: TextView = findViewById(R.id.textViewGender)
        val disease1TextView: TextView = findViewById(R.id.textViewDisease1)
        val disease2TextView: TextView = findViewById(R.id.textViewDisease2)

        // Truy vấn dữ liệu từ Firestore
        val userMedicalDetailsRef = db.collection("Users").document(userId).collection("HealthInformation")
        userMedicalDetailsRef.document("details")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Lấy thông tin và gán vào các TextView
                    val name = document.getString("Name") ?: "N/A"
                    val dob = document.getString("Date of Birth") ?: "N/A"
                    val gender = document.getString("Gender") ?: "N/A"
                    val disease1 = document.getString("Disease 1") ?: "N/A"
                    val disease2 = document.getString("Disease 2") ?: "N/A"

                    nameTextView.text = "Name: $name"
                    dobTextView.text = "Date of Birth: $dob"
                    genderTextView.text = "Gender: $gender"
                    disease1TextView.text = "Disease 1: $disease1"
                    disease2TextView.text = "Disease 2: $disease2"
                } else {
                    Toast.makeText(this, "No health information found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching health information: $e", Toast.LENGTH_SHORT).show()
            }

        // Nút Trở lại (Back Button)
        val backButton: ImageButton = findViewById(R.id.imageButtonBack) // ID của nút trở lại là imageButtonBack trong XML
        backButton.setOnClickListener {
            onBackPressed() // Quay lại màn hình trước
        }

        // Nút Cập nhật (Update Button)
        val updateButton: TextView = findViewById(R.id.updateButton) // ID của nút cập nhật là updateButton trong XML
        updateButton.setOnClickListener {
            // Chuyển đến màn hình chỉnh sửa thông tin sức khỏe
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
