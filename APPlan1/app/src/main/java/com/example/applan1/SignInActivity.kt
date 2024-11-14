package com.example.applan1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.fogetPass.setOnClickListener {
            // Chuyển đến màn hình quên mật khẩu
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "Email không tồn tại. Vui lòng kiểm tra lại."
                            is FirebaseAuthInvalidCredentialsException -> "Sai mật khẩu. Vui lòng thử lại."
                            else -> "Đăng nhập thất bại. Vui lòng thử lại."
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Không được để trống trường nào!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
