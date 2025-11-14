package com.sib.apps.sparklex.uctraining.repository

import com.sib.apps.sparklex.uctraining.data.StudentDao
import com.sib.apps.sparklex.uctraining.data.StudentEntity
import com.sib.apps.sparklex.uctraining.dto.StudentDto
import com.sib.apps.sparklex.uctraining.network.StudentApi

class StudentRepository(
    private val dao: StudentDao,
    private val api: StudentApi
) {

    suspend fun getRemoteStudents(): List<StudentDto> {
        val remote = api.getStudents()
        remote.map { it.toEntity() }.also { entities ->
            dao.clearAll()
            dao.insertAll(entities)
        }
        return remote
    }

    suspend fun addStudent(student: StudentDto): StudentDto {
        val createdStudent = api.addStudent(student)
        val entity = createdStudent.toEntity()
        dao.insert(entity)
        return createdStudent
    }

    // TODO: add and return inserted item back 

    suspend fun updateStudent(student: StudentDto) {
        api.updateStudent(student.id, student)
        dao.update(student.toEntity())
    }

    suspend fun deleteStudent(id: Int?) {
        api.deleteStudent(id)
        dao.deleteById(id)
    }
}

fun StudentEntity.toDto() = StudentDto(id, name, age, department)
fun StudentDto.toEntity() = StudentEntity(id, name, age, department)