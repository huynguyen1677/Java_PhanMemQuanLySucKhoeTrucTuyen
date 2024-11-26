package com.example.carevault.authentication

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carevault.R
import com.example.carevault.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val passwordEditText = findViewById<EditText>(R.id.textView3)

        // Xử lý hiển thị/ẩn mật khẩu
        passwordEditText.setOnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= passwordEditText.right - passwordEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                    togglePasswordVisibility(passwordEditText)
                    return@setOnTouchListener true
                }
            }
            false
        }

        // Nút quay lại
        val backButton: ImageButton = findViewById(R.id.imageButton2)
        backButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Nút đăng ký
        val signUpButton: TextView = findViewById(R.id.textView5)
        signUpButton.setOnClickListener {
            if (validateInputs()) {
                val email = findViewById<EditText>(R.id.textView11).text.toString()
                val password = findViewById<EditText>(R.id.textView3).text.toString()

                // Xử lý đăng ký tài khoản Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val name = findViewById<EditText>(R.id.textView6).text.toString()
                            val mobileNumber = findViewById<EditText>(R.id.textView15).text.toString()

                            val userId = user?.uid ?: ""
                            val userData = hashMapOf(
                                "Name" to name,
                                "Email" to email,
                                "MobileNumber" to mobileNumber,
                                "Country" to "Not Specified", // Giá trị mặc định
                                "UserId" to userId
                            )

                            // Kiểm tra xem người dùng đã tồn tại chưa
                            db.collection("Users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    if (!document.exists()) {
                                        // Lưu dữ liệu người dùng vào Firestore
                                        db.collection("Users").document(userId)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "User registered successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                startActivity(Intent(this, HealthInfoInputActivity::class.java))
                                                finish()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Error saving user data: $e",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(applicationContext, "User already exists.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                baseContext, "Registration failed. ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val nameEditText: EditText = findViewById(R.id.textView6)
        val emailEditText: EditText = findViewById(R.id.textView11)
        val passwordEditText: EditText = findViewById(R.id.textView3)
        val mobileNumberEditText: EditText = findViewById(R.id.textView15)
        val checkBox: CheckBox = findViewById(R.id.checkBox)

        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val mobileNumber = mobileNumberEditText.text.toString().trim()

        // Kiểm tra tên
        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }

        // Kiểm tra email
        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Enter a valid email address"
            return false
        }

        // Kiểm tra mật khẩu
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }
        if (password.length < 8) {
            passwordEditText.error = "Password must be at least 8 characters long"
            return false
        }

        // Kiểm tra số điện thoại
        if (mobileNumber.isEmpty()) {
            mobileNumberEditText.error = "Mobile number is required"
            return false
        }
        if (!Patterns.PHONE.matcher(mobileNumber).matches() || mobileNumber.length < 10) {
            mobileNumberEditText.error = "Enter a valid mobile number (at least 10 digits)"
            return false
        }

        // Kiểm tra điều khoản
        if (!checkBox.isChecked) {
            Toast.makeText(applicationContext, "Please agree to the terms", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        return true
    }

    private fun togglePasswordVisibility(editText: EditText) {
        val selection = editText.selectionEnd
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        editText.setSelection(selection)
    }
}
