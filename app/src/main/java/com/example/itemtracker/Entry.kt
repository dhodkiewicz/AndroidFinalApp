package com.example.itemtracker

import android.location.Location

class Entry(
    val id: String,
    val userId: String,
    var entry: String,
    val entryDate: String,
    val moodRating: Double,
    val location: Data,
)
{

    override fun toString(): String{
        return "id: $id, entry: $entry, journalDate: $entryDate, mood: $moodRating," +
                " user ID: $userId position: $location"
    }
}
