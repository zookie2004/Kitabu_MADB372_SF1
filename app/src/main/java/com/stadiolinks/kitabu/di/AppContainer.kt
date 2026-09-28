package com.stadiolinks.kitabu.di

import android.content.Context
import com.stadiolinks.kitabu.data.local.KitabuDatabase
import com.stadiolinks.kitabu.data.repository.OfflineLibraryRepository
import com.stadiolinks.kitabu.domain.repository.LibraryRepository

interface AppContainer {
    val libraryRepository: LibraryRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database = KitabuDatabase.getDatabase(context)

    override val libraryRepository: LibraryRepository by lazy {
        OfflineLibraryRepository(
            database = database,
            bookDao = database.bookDao(),
            bookingDao = database.bookingDao()
        )
    }
}
