package com.sib.apps.sparklex.uctraining

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sib.apps.sparklex.uctraining.data.AppDatabase
import com.sib.apps.sparklex.uctraining.data.StudentEntity
import com.sib.apps.sparklex.uctraining.databinding.ActivityStudentEditBinding
import com.sib.apps.sparklex.uctraining.dto.StudentDto
import com.sib.apps.sparklex.uctraining.network.RetrofitClient
import com.sib.apps.sparklex.uctraining.network.StudentApi
import com.sib.apps.sparklex.uctraining.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentEditBinding
    private lateinit var repository: StudentRepository
    private lateinit var studentEntity: StudentEntity

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.enableEdgeToEdge(window)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityStudentEditBinding.inflate(
            layoutInflater
        )
        setContentView(binding.getRoot())

        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLinear) { view, insets ->
            val systemBars: Insets =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.getPaddingLeft(),
                systemBars.top,
                view.getPaddingRight(),
                systemBars.bottom
            )
            insets
        }

        val db: AppDatabase = AppDatabase.getDatabase(this)

        val api: StudentApi = RetrofitClient.api

        repository = StudentRepository(db.studentDao(), api)

        studentEntity = intent.getParcelableExtra("student", StudentEntity::class.java)!!

        binding.editName.setText(studentEntity.name)
        binding.editAge.setText(studentEntity.age.toString())
        binding.editDepartment.setText(studentEntity.department)

        binding.btnEdit.setOnClickListener {
            if (!checkForFieldValidation()) return@setOnClickListener
            updateStudent(
                StudentDto(
                    studentEntity.id,
                    binding.editName.getText().toString(),
                    binding.editAge.getText().toString().toInt(),
                    binding.editDepartment.getText().toString()
                )
            )
        }
    }

    private fun checkForFieldValidation(): Boolean {
        if (android.text.TextUtils.isEmpty(binding.editName.getText())) {
            binding.editName.error = "Please enter name"
            return false
        }
        if (android.text.TextUtils.isEmpty(binding.editAge.getText())) {
            binding.editAge.error = "Please enter age"
            return false
        }
        if (android.text.TextUtils.isEmpty(binding.editDepartment.getText())) {
            binding.editDepartment.error = "Please enter department"
            return false
        }
        return true
    }

    private fun updateStudent(studentDto: StudentDto) {
        lifecycleScope.launch {
            repository.updateStudent(studentDto)

            val intent = Intent().apply {
                putExtra("status", true)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
