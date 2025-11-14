package com.sib.apps.sparklex.uctraining

class StudentEditActivity : AppCompatActivity() {
    private var binding: com.sib.apps.sparklex.uctraining.databinding.ActivityStudentEditBinding? =
        null
    protected var repository: StudentRepository? = null
    private var studentEntity: StudentEntity? = null // Hold the student being edited

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.enableEdgeToEdge(getWindow())
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false)

        binding = com.sib.apps.sparklex.uctraining.databinding.ActivityStudentEditBinding.inflate(
            getLayoutInflater()
        )
        setContentView(binding!!.getRoot())

        ViewCompat.setOnApplyWindowInsetsListener(
            binding!!.rootLinear,
            object : androidx.core.view.OnApplyWindowInsetsListener {
                override fun onApplyWindowInsets(
                    view: android.view.View,
                    insets: WindowInsetsCompat
                ): WindowInsetsCompat {
                    val systemBars: androidx.core.graphics.Insets =
                        insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    view.setPadding(
                        view.getPaddingLeft(),
                        systemBars.top,
                        view.getPaddingRight(),
                        systemBars.bottom
                    )
                    return insets
                }
            })

        val db: AppDatabase = AppDatabase.Companion.getDatabase(this)

        val api: StudentApi = RetrofitClient.api

        repository = StudentRepository(db.studentDao(), api)

        studentEntity = getIntent().getParcelableExtra<StudentEntity?>("student")
        if (studentEntity != null) {
            // Populate fields from entity
            binding!!.editName.setText(studentEntity.name)
            binding!!.editAge.setText(studentEntity.age.toString())
            binding!!.editDepartment.setText(studentEntity.department)
        }

        binding!!.btnEdit.setOnClickListener(android.view.View.OnClickListener { v: android.view.View? ->
            if (!checkForFieldValidation()) return@setOnClickListener
            updateStudent(
                StudentDto(
                    studentEntity.id,
                    binding!!.editName.getText().toString(),
                    binding!!.editAge.getText().toString().toInt(),
                    binding!!.editDepartment.getText().toString()
                )
            )
        })
    }

    private fun checkForFieldValidation(): kotlin.Boolean {
        if (android.text.TextUtils.isEmpty(binding!!.editName.getText())) {
            binding!!.editName.setError("Please enter name")
            return false
        }
        if (android.text.TextUtils.isEmpty(binding!!.editAge.getText())) {
            binding!!.editAge.setError("Please enter age")
            return false
        }
        if (android.text.TextUtils.isEmpty(binding!!.editDepartment.getText())) {
            binding!!.editDepartment.setError("Please enter department")
            return false
        }
        return true
    }

    private fun updateStudent(studentDto: StudentDto?) {
    }
}
