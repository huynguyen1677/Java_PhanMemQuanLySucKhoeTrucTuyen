package com.example.applan1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Lấy email đã được truyền từ ForgotPasswordActivity
        val email = intent.getStringExtra("email") ?: ""

        // Sự kiện khi người dùng nhấn nút "Xác nhận mật khẩu mới"
        binding.confirmNewPasswordButton.setOnClickListener {
            val newPassword = binding.newPasswordEt.text.toString()

            if (newPassword.isNotEmpty()) {
                updatePassword(newPassword)
            } else {
                Toast.makeText(this, "Vui lòng nhập mật khẩu mới!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword(newPassword: String) {
        val user = firebaseAuth.currentUser
        user?.updatePassword(newPassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_SHORT).show()

                // Sau khi thay đổi mật khẩu, quay lại màn hình đăng nhập
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish() // Kết thúc ResetPasswordActivity
            } else {
                Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
