package com.sib.apps.sparklex.uctraining

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.component1
import androidx.activity.result.component2
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sib.apps.sparklex.uctraining.data.AppDatabase
import com.sib.apps.sparklex.uctraining.data.StudentEntity
import com.sib.apps.sparklex.uctraining.databinding.ActivityStudentManageBinding
import com.sib.apps.sparklex.uctraining.dto.StudentDto
import com.sib.apps.sparklex.uctraining.network.RetrofitClient
import com.sib.apps.sparklex.uctraining.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentManageActivity : AppCompatActivity() {

    private lateinit var repository: StudentRepository
    private lateinit var binding: ActivityStudentManageBinding
    private lateinit var adapter: StudentAdapter

    private lateinit var editIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityStudentManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLinear) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }

        val db = AppDatabase.getDatabase(this)

        val api = RetrofitClient.api

        repository = StudentRepository(db.studentDao(), api)

        adapter = StudentAdapter(
            onDelete = { studentDto -> deleteStudent(studentDto.id) },
            onEdit = { studentEntity -> editStudent(studentEntity) }
        )
        binding.rvStudents.layoutManager = LinearLayoutManager(this)
        binding.rvStudents.adapter = adapter

        binding.btnAdd.setOnClickListener {
            if (!checkForFieldValidation())
                return@setOnClickListener
            addStudent(
                StudentDto(
                    name = binding.editName.text.toString(),
                    age = binding.editAge.text.toString().toIntOrNull() ?: 0,
                    department = binding.editDepartment.text.toString()
                )
            )
        }

        fetchStudents()

        editIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { (resultCode, data) ->
                if (resultCode == RESULT_OK && data != null) {
                    val success = data.getBooleanExtra("status", false)
                    if (success) fetchStudents()
                }
            }

    }

    private fun fetchStudents() {
        lifecycleScope.launch {
            val students = repository.getRemoteStudents()
            adapter.submitList(students)
        }
    }

    private fun addStudent(studentDto: StudentDto) {
        lifecycleScope.launch {
            repository.addStudent(studentDto)
            fetchStudents()
            binding.editName.text.clear()
            binding.editAge.text.clear()
            binding.editDepartment.text.clear()
            Toast.makeText(this@StudentManageActivity, "Student Added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(id: Int?) {
        lifecycleScope.launch {
            repository.deleteStudent(id)
            fetchStudents()
            Toast.makeText(this@StudentManageActivity, "Student deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editStudent(studentEntity: StudentEntity) {
        val intent = Intent(this, StudentEditActivity::class.java)
        intent.putExtra("student", studentEntity)
        editIntentLauncher.launch(intent)
    }

    private fun checkForFieldValidation(): Boolean {
        if (TextUtils.isEmpty(binding.editName.text)) {
            binding.editName.error = "Please enter name"
            return false
        }
        if (TextUtils.isEmpty(binding.editAge.text)) {
            binding.editAge.error = "Please enter age"
            return false
        }
        if (TextUtils.isEmpty(binding.editDepartment.text)) {
            binding.editDepartment.error = "Please enter department"
            return false
        }
        return true
    }
}