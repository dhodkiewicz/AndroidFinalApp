package com.example.itemtracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var dbHelper: DBHelper
    private val journalListAdapter = JournalListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)

        recyclerView.adapter = journalListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        journalListAdapter.setItems(DataManager.fetchAllEntries(dbHelper))

        fab.setOnClickListener{
            val addItem= Intent(this, AddItemActivity::class.java)
            startActivityForResult(addItem,1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            journalListAdapter.setItems(DataManager.fetchAllEntries(dbHelper))
        }
    }
}