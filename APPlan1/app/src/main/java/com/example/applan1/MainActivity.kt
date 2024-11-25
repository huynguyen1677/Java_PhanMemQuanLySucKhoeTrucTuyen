package com.example.applan1
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseUser
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var spinnerDoctor: Spinner
    private lateinit var btnConfirm: Button
    private lateinit var btnLogout: Button
    private lateinit var tvUsername: TextView  // Thêm TextView để hiển thị tên người dùng
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        spinnerDoctor = findViewById(R.id.spinnerDoctor)
        btnConfirm = findViewById(R.id.btnConfirm)
        btnLogout = findViewById(R.id.btnLogout)
        tvUsername = findViewById(R.id.tvUsername)  // Khai báo TextView tên người dùng
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // Hiển thị tên người dùng
        displayUserName()

        setupDateAndTimePickers()
        setupDoctorSpinner()
        setupConfirmButton()
        setupLogoutButton()
    }

    private fun displayUserName() {
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            val displayName = it.displayName ?: "Người dùng"
            tvUsername.text = "Xin chào, $displayName"  // Hiển thị tên người dùng trong TextView
        } ?: run {
            tvUsername.text = "Xin chào, Người dùng"
        }
    }

    private fun setupDateAndTimePickers() {
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etDate.setText("$year-${month + 1}-$day")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        etTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hour, minute ->
                etTime.setText("$hour:$minute")
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun setupDoctorSpinner() {
        val doctors = listOf("Dr. John Doe", "Dr. Jane Smith", "Dr. Alice Brown")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDoctor.adapter = adapter
    }

    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {
            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val doctor = spinnerDoctor.selectedItem.toString()

            if (date.isEmpty() || time.isEmpty() || doctor.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val appointment = hashMapOf(
                "date" to date,
                "time" to time,
                "doctor" to doctor,
                "status" to "pending"
            )

            firestore.collection("appointments")
                .add(appointment)
                .addOnSuccessListener {
                    Toast.makeText(this, "Lịch hẹn đã được đặt thành công!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Đặt lịch thất bại: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupLogoutButton() {
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()  // Đăng xuất khỏi Firebase
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()

            // Chuyển đến màn hình đăng nhập sau khi đăng xuất
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()  // Đóng MainActivity
        }
    }
}
