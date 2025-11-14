package com.sib.apps.sparklex.uctraining.dto

data class StudentDto(
    val id: Int? = null,
    val name: String,
    val age: Int,
    val department: String
) {
    override fun toString(): String {
        return "StudentDto(id=$id, name='$name', age=$age, department='$department')"
    }
}