package com.stadiolinks.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class KitabuDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: KitabuDatabase? = null

        fun getDatabase(context: Context): KitabuDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    "kitabu_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PrepopulateCallback)
                    .build()
                    .also { INSTANCE = it }
            }

        private object PrepopulateCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val sampleBooks = listOf(
                    Triple("Mobile Application Development with Kotlin", "A. Ndlovu", "Technology"),
                    Triple("Database Systems: Concepts and Design", "L. Jacobs", "Databases"),
                    Triple("Introduction to Information Systems", "S. Naidoo", "Information Systems"),
                    Triple("Research Methods for Computing", "P. Mokoena", "Research"),
                    Triple("Web Design and Development", "T. Williams", "Web Development"),
                    Triple("Business Analysis Essentials", "K. Dlamini", "Business"),
                    Triple("User Experience Design", "R. Smith", "Design"),
                    Triple("Cloud Computing Fundamentals", "M. Khumalo", "Technology")
                )

                sampleBooks.forEach { (title, author, category) ->
                    db.execSQL(
                        "INSERT INTO books (title, author, category, isAvailable) VALUES (?, ?, ?, 1)",
                        arrayOf(title, author, category)
                    )
                }
            }
        }
    }
}
