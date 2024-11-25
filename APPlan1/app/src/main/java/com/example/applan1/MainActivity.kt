package com.example.applan1
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
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
            val userId = it.uid  // Lấy ID của người dùng hiện tại
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val displayName = document.getString("name") ?: "Người dùng"
                        tvUsername.text = "Xin chào, $displayName"
                    } else {
                        tvUsername.text = "Xin chào, Người dùng"
                    }
                }
                .addOnFailureListener {
                    tvUsername.text = "Xin chào, Người dùng"
                    Toast.makeText(this, "Không thể tải tên người dùng", Toast.LENGTH_SHORT).show()
                }
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
        firestore.collection("doctors")
            .get()
            .addOnSuccessListener { result ->
                // Lấy danh sách bác sĩ từ Firestore
                val doctorList = result.documents.map { document ->
                    val name = document.getString("name") ?: "Unknown Doctor"
                    val experience = document.getString("experience") ?: "Chưa có thông tin"
                    val status = document.getString("status") ?: "Chưa xác định"
                    val specialization = document.getString("specialization") ?: "Chưa xác định"

                    // Trả về thông tin đầy đủ của bác sĩ dưới dạng chuỗi
                    "$name - $specialization - $experience - $status"
                }
                if (doctorList.isNotEmpty()) {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctorList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerDoctor.adapter = adapter
                } else {
                    Toast.makeText(this, "Không có bác sĩ nào khả dụng.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Không thể tải danh sách bác sĩ: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupConfirmButton() {
        btnConfirm.setOnClickListener {
            val date = etDate.text.toString()
            val time = etTime.text.toString()
            val doctor = spinnerDoctor.selectedItem.toString()

            // Kiểm tra xem người dùng đã điền đầy đủ thông tin chưa
            if (date.isEmpty() || time.isEmpty() || doctor.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo lịch hẹn dưới dạng hashmap
            val appointment = hashMapOf(
                "date" to date,
                "time" to time,
                "doctor" to doctor,
                "status" to "pending",  // Trạng thái ban đầu là "đang chờ"
                "userId" to firebaseAuth.currentUser?.uid // Lưu ID của người dùng đã đăng nhập
            )

            // Thêm lịch hẹn vào Firestore
            firestore.collection("appointments")
                .add(appointment)
                .addOnSuccessListener { documentReference ->
                    // Thông báo thành công
                    Toast.makeText(this, "Lịch hẹn đã được đặt thành công!", Toast.LENGTH_SHORT).show()

                    // Thực hiện reset các trường nhập liệu
                    etDate.text.clear()
                    etTime.text.clear()
                    spinnerDoctor.setSelection(0) // Reset Spinner về vị trí đầu tiên

                    // Bạn có thể lưu lại ID của lịch hẹn vào người dùng hoặc làm các thao tác khác ở đây
                }
                .addOnFailureListener { e ->
                    // Thông báo nếu có lỗi khi lưu lịch hẹn
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
