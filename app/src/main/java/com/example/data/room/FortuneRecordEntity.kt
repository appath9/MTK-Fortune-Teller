package com.example.data.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fortune_records",
    indices = [Index(value = ["attestationId"], unique = true)]
)
data class FortuneRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long,
    val questionId: String,
    val questionText: String,
    val chosenNumber: Int,
    val answerText: String,
    val txSignature: String,
    val attestationId: String
)
