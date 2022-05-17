package com.example.itemtracker

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Simple object for working with Firebase real-time database
 */
object FirebaseManager {

    // takes all of the journal entries and sets them in a table called "entries"
    fun setEntries(entries: ArrayList<Entry>) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("entries")

        myRef.setValue(entries) // values posted here
    }

    // this was a work in progress i'll come back to this later - (after submission)
    // but this is for fetching entries from the database but I'd like to do it in an observable manner
    // but before I do that I need to fix auth for users
    fun fetchEntries(){
        val db = FirebaseDatabase.getInstance()
        val myRef = db.getReference("entries")
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val entry = dataSnapshot.getValue(Entry::class.java)
                Log.d("success", entry!!.entry)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("error", databaseError.message)

            }

        }
        myRef.addListenerForSingleValueEvent(valueEventListener)
    }

}