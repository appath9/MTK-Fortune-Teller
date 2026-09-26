package com.example.data

import android.content.Context
import com.example.data.room.AppDatabase
import com.example.data.room.FortuneRecordEntity
import kotlinx.coroutines.flow.Flow

class FortuneRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).fortuneRecordDao()

    val allRecords: Flow<List<FortuneRecordEntity>> = dao.getAllRecords()

    suspend fun insertRecord(record: FortuneRecordEntity) {
        dao.insertRecord(record)
    }

    suspend fun getRecordByAttestationId(attestationId: String): FortuneRecordEntity? {
        return dao.getRecordByAttestationId(attestationId)
    }
}
