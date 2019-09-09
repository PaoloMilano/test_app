package com.magicbluepenguin.testapplication

import android.app.Application
import androidx.room.Room
import com.magicbluepenguin.testapplication.data.cache.AppDatabase

class TestApplication : Application() {

    /**
     * Apparently this is an acceptable pattern as described below:
     *
     * Android made a deliberate design decision that is can seem surprising, to just give up on
     * the whole idea of applications cleanly exiting and instead let the kernel clean up their
     * resources. After all, the kernel needs to be able to do this anyway. Given that design,
     * keeping anything open for the entire duration of a process's life and never closing it is
     * simply not a leak. It will be cleaned up when the process is cleaned up.
     *
     * source: http://groups.google.com/group/android-developers/msg/74ee967b2fcff770
     */

    val appDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java, "app_database"
        ).build()
    }
}