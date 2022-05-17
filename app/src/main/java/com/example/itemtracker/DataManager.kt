package com.example.itemtracker

import android.content.ContentValues
import android.location.Location
import com.example.itemtracker.EntryDBContract.*
import com.example.itemtracker.EntryDBContract.iEntry.ID


import com.google.gson.Gson


object DataManager {

    fun fetchAllEntries(dbHelper: DBHelper) : ArrayList<Entry>{

        val entries = ArrayList<Entry>()

        val db = dbHelper.readableDatabase

        val columns = arrayOf(
            ID,
            iEntry.ENTRY,
            iEntry.USERID,
            iEntry.MOOD,
            iEntry.LOC,
            iEntry.DATE,
        )

        val cursor = db.query(
            iEntry.TABLE_NAME,
        columns,
        null,
        null,
        null,
        null,
        null)

        val idPos = cursor.getColumnIndex(ID)
        val entryPos = cursor.getColumnIndex(iEntry.ENTRY)
        val userPos = cursor.getColumnIndex(iEntry.USERID)
        val moodPos = cursor.getColumnIndex(iEntry.MOOD)
        val locPos = cursor.getColumnIndex(iEntry.LOC)
        val datePos = cursor.getColumnIndex(iEntry.DATE)


        while(cursor.moveToNext()){

            val id = cursor.getString(idPos)
            val entry = cursor.getString(entryPos)
            val user = cursor.getString(userPos)
            val mood = cursor.getString(moodPos).toDouble()
            val locJSON = cursor.getString(locPos)
            val location = Gson().fromJson(locJSON, Location::class.java)
            val date = cursor.getString(datePos)

            entries.add(Entry(id,user,entry,date,mood,location))
        }

        cursor.close()

        return entries
    }

    fun fetchEntry(dbHelper: DBHelper, entryId: String) : Entry?{

        val db = dbHelper.readableDatabase
        var tempEntry: Entry? = null

        val columns = arrayOf(
            iEntry.ENTRY,
            iEntry.USERID,
            iEntry.DATE,
            iEntry.LOC,
            iEntry.MOOD,
        )

        val sel = ID + " LIKE ? " //iEntry ID

        val selArgs = arrayOf(entryId)

        val cursor = db.query(
            iEntry.TABLE_NAME,
            columns,
            sel,
            selArgs,
            null,
            null,
            null
        )

        val entryPos = cursor.getColumnIndex(iEntry.ENTRY)
        val userPos = cursor.getColumnIndex(iEntry.USERID)
        val moodPos = cursor.getColumnIndex(iEntry.MOOD)
        val locPos = cursor.getColumnIndex(iEntry.LOC)
        val datePos = cursor.getColumnIndex(iEntry.DATE)

        while (cursor.moveToNext()){
            var entry = cursor.getString(entryPos)
            val user = cursor.getString(userPos)
            val mood = cursor.getString(moodPos).toDouble()
            val loc = cursor.getString(locPos)
            val location = Gson().fromJson(loc, Location::class.java)
            val date = cursor.getString(datePos)

            tempEntry = Entry(entryId, user, entry, date, mood, location)
        }

        cursor.close()
        return tempEntry

    }

    fun updateEntry(dbHelper: DBHelper, entry: Entry){

        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(iEntry.ENTRY, entry.entry)
        values.put(iEntry.MOOD, entry.moodRating)


        val sel = iEntry.ID + " LIKE ? "

        val selArgs = arrayOf(entry.id)

        db.update(iEntry.TABLE_NAME, values, sel, selArgs)
    }

    fun deleteEntry(dbHelper: DBHelper, entry: Entry) : Int{

        val db = dbHelper.writableDatabase

        val sel = iEntry.ID + " LIKE ? "

        val selArgs = arrayOf(entry.id)

        return db.delete(iEntry.TABLE_NAME, sel, selArgs)
    }
}


