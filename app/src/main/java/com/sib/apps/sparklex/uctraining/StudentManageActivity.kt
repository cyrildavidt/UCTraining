package com.sib.apps.sparklex.uctraining

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sib.apps.sparklex.uctraining.data.AppDatabase
import com.sib.apps.sparklex.uctraining.databinding.ActivityMainBinding
import com.sib.apps.sparklex.uctraining.dto.StudentDto
import com.sib.apps.sparklex.uctraining.network.StudentApi
import com.sib.apps.sparklex.uctraining.repository.StudentRepository
import kotlinx.coroutines.launch
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var repository: StudentRepository
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
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

        val client = okhttp3.OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.21.22.64:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val api = retrofit.create(StudentApi::class.java)
        repository = StudentRepository(db.studentDao(), api)

        adapter = StudentAdapter(
            onDelete = { studentDto -> deleteStudent(studentDto.id) },
            onEdit = { studentDto -> editStudent(studentDto) }
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
            Toast.makeText(this@MainActivity, "Student Added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteStudent(id: Int?) {
        lifecycleScope.launch {
            repository.deleteStudent(id)
            fetchStudents()
            Toast.makeText(this@MainActivity, "Student deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editStudent(studentDto: StudentDto) {
        lifecycleScope.launch {
            repository.updateStudent(studentDto)
            fetchStudents()
            Toast.makeText(this@MainActivity, "Student updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForFieldValidation(): Boolean {
        if (TextUtils.isEmpty(binding.editName.text)){
            binding.editName.error = "Please enter name"
            return false
        }
        if (TextUtils.isEmpty(binding.editAge.text)){
            binding.editAge.error = "Please enter age"
            return false
        }
        if (TextUtils.isEmpty(binding.editDepartment.text)){
            binding.editDepartment.error = "Please enter department"
            return false
        }
        return true
    }
}