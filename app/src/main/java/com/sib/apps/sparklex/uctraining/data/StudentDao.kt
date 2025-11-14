package com.sib.apps.sparklex.uctraining.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("select * from tb_students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Insert(onConflict = REPLACE)
    suspend fun insert(studentEntity: StudentEntity)

    @Update
    suspend fun update(studentEntity: StudentEntity)

    @Delete
    suspend fun delete(studentEntity: StudentEntity)

    @Query("DELETE FROM tb_students WHERE id = :id")
    suspend fun deleteById(id: Int?)

    @Query("DELETE FROM tb_students")
    suspend fun clearAll()

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(students: List<StudentEntity>)
}