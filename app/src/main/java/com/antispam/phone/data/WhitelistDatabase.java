package com.antispam.phone.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Room database for storing whitelist entries
 */
@Database(entities = {WhitelistEntry.class}, version = 1, exportSchema = false)
public abstract class WhitelistDatabase extends RoomDatabase {
    
    private static WhitelistDatabase instance;
    
    public abstract WhitelistDao whitelistDao();
    
    public static synchronized WhitelistDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    WhitelistDatabase.class,
                    "whitelist_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}