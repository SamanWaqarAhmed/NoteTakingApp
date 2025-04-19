package com.muqaddas.takingnotesapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Step 1: @Database annotation
@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    // Step 2: Abstract method for DAO
    public abstract NoteDao noteDao();

    // Step 3: Singleton instance to avoid multiple database instances
    private static NoteDatabase instance;

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()  // For database version migration
                    .build();
        }
        return instance;
    }
}
