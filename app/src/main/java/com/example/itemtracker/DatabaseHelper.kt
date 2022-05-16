package com.example.itemtracker

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.itemtracker.ItemTrackerDBContract.ItemEntry.SQL_CREATE_ENTRIES

class DatabaseHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(ItemTrackerDBContract.ItemEntry.SQL_CREATE_ENTRIES)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(ItemTrackerDBContract.ItemEntry.SQL_DROP_TABLE)
        onCreate(db)
    }

    companion object{
        const val DATABASE_NAME = "itemtracker.db"
        const val DATABASE_VERSION = 1
    }
}