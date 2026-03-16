package com.zhakki.quizapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [QuestionEntity::class, GameResultEntity::class, TokenEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun gameResultDao(): GameResultDao
    abstract fun tokenDao(): TokenDao
}