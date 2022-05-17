package com.example.itemtracker

import android.provider.BaseColumns

/**
 * This is a class specifically used for defining the table in the SQLite db,
 * but also defines how we set/retrieve/update and access the fields.. Each base column is represented
 * by a particular field defined below under the object iEntry
 */
class EntryDBContract {
    object iEntry : BaseColumns { // implements interface when working with a cursor
        const val TABLE_NAME = "entry"
        const val ID = BaseColumns._ID
        const val ENTRY = "entryText"
        const val USERID = "userId"
        const val MOOD = "moodRating"
        const val DATE = "entryDate"
        const val LOC = "location"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$ENTRY TEXT NOT NULL, " +
                    "$USERID TEXT NOT NULL, " +
                    "$MOOD REAL NOT NULL, " +
                    "$DATE TEXT NOT NULL, " +
                    "$LOC TEXT NOT NULL)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${iEntry.TABLE_NAME}"
    }
}