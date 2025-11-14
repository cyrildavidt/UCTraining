package com.sib.apps.sparklex.uctraining.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tb_students")
data class StudentEntity(
    @PrimaryKey val id: Int? = null,
    val name: String,
    val age: Int,
    val department: String
): Parcelable