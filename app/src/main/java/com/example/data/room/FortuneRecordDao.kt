package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FortuneRecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecord(record: FortuneRecordEntity): Long

    @Query("SELECT * FROM fortune_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<FortuneRecordEntity>>

    @Query("SELECT * FROM fortune_records WHERE attestationId = :attestationId LIMIT 1")
    suspend fun getRecordByAttestationId(attestationId: String): FortuneRecordEntity?
}
