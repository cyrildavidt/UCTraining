package com.sib.apps.sparklex.uctraining.network

import com.sib.apps.sparklex.uctraining.dto.StudentDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface StudentApi {
    @GET("/students")
    suspend fun getStudents(): List<StudentDto>

    @POST("/students")
    suspend fun addStudent(@Body studentDto: StudentDto): StudentDto

    @PUT("/students/{id}")
    suspend fun updateStudent(
        @Path("id") id: Int?,
        @Body studentDto: StudentDto
    ): StudentDto

    @DELETE("/students/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?)
}