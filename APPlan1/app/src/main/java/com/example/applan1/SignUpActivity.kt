package com.example.applan1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmPassword = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    registerUserWithEmail(email, password)
                } else {
                    Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUserWithEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Đăng xuất người dùng để ngăn tự động đăng nhập
                    firebaseAuth.signOut()
                    Toast.makeText(this, "Đăng ký thành công. Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show()

                    // Chuyển hướng về SignInActivity
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish() // Kết thúc SignUpActivity để không quay lại trang đăng ký
                }  else {
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthUserCollisionException -> "Email này đã tồn tại. Vui lòng sử dụng email khác."
                        is FirebaseAuthWeakPasswordException -> "Mật khẩu yếu. Vui lòng chọn mật khẩu mạnh hơn với 6 kí tự trở lên."
                        else -> "Đăng ký thất bại. Vui lòng thử lại với email có dạng @gmail.com"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
