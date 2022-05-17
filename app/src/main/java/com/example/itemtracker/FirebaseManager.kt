package com.example.itemtracker

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

object FirebaseManager {


    fun setEntries(entries: ArrayList<Entry>) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("entries")

        myRef.setValue(entries)
    }

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