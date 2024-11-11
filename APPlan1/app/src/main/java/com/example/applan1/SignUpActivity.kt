package com.example.applan1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.applan1.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.sendOtpBtn.setOnClickListener {
            val phoneNumber = binding.phoneEt.text.toString()
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }

        binding.button.setOnClickListener {
            val otp = binding.otpEt.text.toString()
            if (otp.isNotEmpty()) {
                verifyCode(otp)
            } else {
                Toast.makeText(this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hàm gửi mã OTP
    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)       // Số điện thoại để xác thực
            .setTimeout(60L, TimeUnit.SECONDS) // Thời gian chờ OTP
            .setActivity(this)                 // Hoạt động hiện tại
            .setCallbacks(callbacks)           // Gọi lại khi nhận OTP
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Các callbacks khi OTP được gửi hoặc xác thực
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Xác thực tự động hoặc khi OTP đúng
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@SignUpActivity, "Xác thực thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(verificationId, token)
            storedVerificationId = verificationId
            resendToken = token
            Toast.makeText(this@SignUpActivity, "Mã OTP đã được gửi", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm xác minh mã OTP khi người dùng nhập vào
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    // Hàm xử lý đăng ký sau khi OTP được xác thực thành công
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Đăng ký thành công
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()

                    // Chuyển hướng về SignInActivity
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish() // Kết thúc SignUpActivity để không quay lại trang đăng ký
                } else {
                    Toast.makeText(this, "Đăng ký thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
