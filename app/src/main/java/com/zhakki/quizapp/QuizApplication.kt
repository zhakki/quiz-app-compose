package com.zhakki.quizapp

import android.app.Application
import androidx.room.Room
import com.zhakki.quizapp.data.local.AppDatabase
import com.zhakki.quizapp.data.local.LocalDataSource

class QuizApplication : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quiz_database"
        ).build()
    }

    val localDataSource: LocalDataSource by lazy {
        LocalDataSource(
            questionDao = database.questionDao(),
            gameResultDao = database.gameResultDao(),
            tokenDao = database.tokenDao()
        )
    }
}
