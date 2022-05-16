package com.example.itemtracker

import android.provider.BaseColumns
import android.provider.BaseColumns._ID

class ItemTrackerDBContract {

    object ItemEntry : BaseColumns { // implements interface when working with a cursor
        const val TABLE_NAME = "item"
        const val COLUMN_NAME = "name"
        const val COLUMN_ID = _ID
        const val COLUMN_PRICE = "price"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_SKU = "sku"
        const val COLUMN_DATE = "usedByDate"
        const val COLUMN_DEPARTMENT = "department"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE $TABLE_NAME (" +
                   COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_NAME TEXT NOT NULL, " +
                    "$COLUMN_PRICE REAL NOT NULL, " +
                    "$COLUMN_QUANTITY INTEGER NOT NULL, " +
                    "$COLUMN_SKU INTEGER NOT NULL, " +
                    "$COLUMN_DATE INTEGER NOT NULL, " +
                    "$COLUMN_DEPARTMENT TEXT NOT NULL)"

        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS ${ItemEntry.TABLE_NAME}"
    }
}