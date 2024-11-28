package com.example.carevault.authentication

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carevault.R
import com.google.firebase.auth.FirebaseAuth

class PasswordResetActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Tham chiếu tới các thành phần trong layout
        val backButton = findViewById<ImageButton>(R.id.imageButton2) // Nút quay lại
        val emailEditText = findViewById<EditText>(R.id.textView11)   // Ô nhập email
        val submitButton = findViewById<TextView>(R.id.textView5)    // Nút gửi yêu cầu

        // Sự kiện khi nhấn nút quay lại
        backButton.setOnClickListener {
            finish() // Quay lại màn hình trước đó
        }

        // Sự kiện khi nhấn nút gửi yêu cầu
        submitButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            // Kiểm tra email không được để trống
            if (email.isNotEmpty()) {
                // Gửi yêu cầu đặt lại mật khẩu qua Firebase
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Thông báo thành công
                            Toast.makeText(
                                this,
                                "Đã gửi email đặt lại mật khẩu tới $email",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        } else {
                            // Thông báo lỗi
                            Toast.makeText(
                                this,
                                "Lỗi khi gửi email: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                // Thông báo nếu email để trống
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
