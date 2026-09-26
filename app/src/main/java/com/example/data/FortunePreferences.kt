package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FortunePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fortune_teller_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_INTRO_DATE = "last_intro_date"
        private const val KEY_CURRENT_LANG = "current_lang"
        private const val KEY_LAST_READING_DATE = "last_reading_date"
        private const val KEY_DAILY_READING_COUNT = "daily_reading_count"
        private const val KEY_DAILY_WEB3_READING_COUNT = "daily_web3_reading_count"
        private const val KEY_MOCK_SEEKER_ELITE = "mock_seeker_elite"

        fun getTodayDateString(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(Date())
        }
    }

    var isMockSeekerEliteEnabled: Boolean
        get() = if (BuildConfig.DEBUG) prefs.getBoolean(KEY_MOCK_SEEKER_ELITE, false) else false
        set(value) {
            if (BuildConfig.DEBUG) {
                prefs.edit().putBoolean(KEY_MOCK_SEEKER_ELITE, value).apply()
            }
        }

    var lastIntroDate: String?
        get() = prefs.getString(KEY_LAST_INTRO_DATE, null)
        set(value) {
            prefs.edit().putString(KEY_LAST_INTRO_DATE, value).apply()
        }

    var currentLang: String
        get() = prefs.getString(KEY_CURRENT_LANG, "en") ?: "en"
        set(value) {
            prefs.edit().putString(KEY_CURRENT_LANG, value).apply()
        }

    var lastReadingDate: String?
        get() = prefs.getString(KEY_LAST_READING_DATE, null)
        set(value) {
            prefs.edit().putString(KEY_LAST_READING_DATE, value).apply()
        }

    var dailyReadingCount: Int
        get() = prefs.getInt(KEY_DAILY_READING_COUNT, 0)
        set(value) {
            prefs.edit().putInt(KEY_DAILY_READING_COUNT, value).apply()
        }

    var dailyWeb3ReadingCount: Int
        get() = prefs.getInt(KEY_DAILY_WEB3_READING_COUNT, 0)
        set(value) {
            prefs.edit().putInt(KEY_DAILY_WEB3_READING_COUNT, value).apply()
        }

    fun checkAndResetIfNewDay(todayDate: String = getTodayDateString()) {
        if (lastReadingDate != todayDate) {
            prefs.edit()
                .putString(KEY_LAST_READING_DATE, todayDate)
                .putInt(KEY_DAILY_READING_COUNT, 0)
                .putInt(KEY_DAILY_WEB3_READING_COUNT, 0)
                .apply()
        }
    }

    fun getFreeReadingsRemaining(todayDate: String = getTodayDateString()): Int {
        checkAndResetIfNewDay(todayDate)
        return maxOf(0, 2 - dailyReadingCount)
    }

    fun getPrototypeReadingsRemaining(todayDate: String = getTodayDateString()): Int {
        checkAndResetIfNewDay(todayDate)
        return maxOf(0, 3 - dailyWeb3ReadingCount)
    }

    fun getDailyReadingsRemaining(todayDate: String = getTodayDateString()): Int {
        return getFreeReadingsRemaining(todayDate)
    }

    fun incrementDailyReading(todayDate: String = getTodayDateString()) {
        checkAndResetIfNewDay(todayDate)
        val newCount = (dailyReadingCount + 1).coerceAtMost(2)
        prefs.edit().putInt(KEY_DAILY_READING_COUNT, newCount).apply()
    }

    fun incrementPrototypeWeb3Reading(todayDate: String = getTodayDateString()) {
        checkAndResetIfNewDay(todayDate)
        val newCount = (dailyWeb3ReadingCount + 1).coerceAtMost(3)
        prefs.edit().putInt(KEY_DAILY_WEB3_READING_COUNT, newCount).apply()
    }

    fun clearIntroDate() {
        prefs.edit().remove(KEY_LAST_INTRO_DATE).apply()
    }
}
