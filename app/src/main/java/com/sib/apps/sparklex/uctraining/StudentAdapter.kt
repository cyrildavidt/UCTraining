package com.sib.apps.sparklex.uctraining

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sib.apps.sparklex.uctraining.data.StudentEntity
import com.sib.apps.sparklex.uctraining.databinding.ItemStudentBinding
import com.sib.apps.sparklex.uctraining.dto.StudentDto
import com.sib.apps.sparklex.uctraining.repository.toEntity

class StudentAdapter(
    private val onDelete: (StudentDto) -> Unit,
    private val onEdit: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students: List<StudentDto> = emptyList()

    fun submitList(list: List<StudentDto>) {
        students = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        private val TAG = "StudentAdapter"
        fun bind(student: StudentDto) {
            Log.d(TAG, "bind student: $student")
            binding.apply {
                student
                textName.text = student.name
                textAge.text = "Age: ${student.age}"
                textMajor.text = "Major: ${student.department}"
                btnDelete.setOnClickListener { onDelete(student) }
                btnEdit.setOnClickListener { onEdit(student.toEntity()) }
            }
        }
    }
}