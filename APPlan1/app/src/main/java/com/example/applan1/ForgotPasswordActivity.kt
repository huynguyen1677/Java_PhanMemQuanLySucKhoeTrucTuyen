package com.example.applan1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Sự kiện khi người dùng nhấn nút "Gửi yêu cầu"
        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailEt.text.toString()

            if (email.isNotEmpty()) {
                resetPassword(email)
            } else {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show()
            }
        }

        // Sự kiện khi người dùng nhấn vào "Bạn nhớ mật khẩu?"
        binding.textView.setOnClickListener {
            // Quay lại màn hình đăng nhập
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Kết thúc ForgotPasswordActivity
        }
    }

    private fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Đã gửi email khôi phục mật khẩu!", Toast.LENGTH_SHORT).show()

                    // Chuyển người dùng đến màn hình nhập mật khẩu mới
                    val intent = Intent(this, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email) // Truyền email người dùng
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
