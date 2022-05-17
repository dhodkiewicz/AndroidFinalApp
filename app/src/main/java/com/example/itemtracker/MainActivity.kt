package com.example.itemtracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Dalton's Final App - *Jabber*
 * Thanks for all the good time's Tony!
 */
class MainActivity : AppCompatActivity() {

    // a field to hold our DBHelper class
    lateinit var dbHelper: DBHelper
    //adapter for working with the recycler view and populating/setting it's items
    private val journalListAdapter = JournalListAdapter(this)

    //on creation of this obj
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //pass this context to the DBHelper
        dbHelper = DBHelper(this)

        //setting the recyclerView's adapter to the journalListAdapter
        recyclerView.adapter = journalListAdapter
        //setting the recyclerviews' layoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)
        // set the recyclerview's items by fetching all journal entries from our DataManager
        journalListAdapter.setItems(DataManager.fetchAllEntries(dbHelper))

        //set's the onclick listener for our fab (+) button
        fab.setOnClickListener{
            val addItem= Intent(this, AddEntryActivity::class.java)
            startActivityForResult(addItem,1)
        }

        // all this does is allow a list of entries to be posted to the firebase Real-time database
        // note that the table name is "entries" - defined in FirebaseManager -
        // going forward, I would probably have entries - then all entries would have the users
        //UID as a field
        btnExport.setOnClickListener{
            FirebaseManager.setEntries(DataManager.fetchAllEntries(dbHelper))
        }
    }

    // called after the activity being launched exits
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if this result is okay than we can set all the journal entries to the fetched entries from our DataManager
        if (resultCode == Activity.RESULT_OK){
            journalListAdapter.setItems(DataManager.fetchAllEntries(dbHelper))
        }
    }
}