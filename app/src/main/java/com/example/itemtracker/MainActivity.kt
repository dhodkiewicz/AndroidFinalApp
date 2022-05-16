package com.example.itemtracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var databaseHelper: DatabaseHelper
    private val itemListAdapter = ItemListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseHelper = DatabaseHelper(this)

        recyclerView.adapter = itemListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemListAdapter.setItems(DataManager.fetchAllItems(databaseHelper))

        fab.setOnClickListener{
            val addItem= Intent(this, AddItemActivity::class.java)
            startActivityForResult(addItem,1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            itemListAdapter.setItems(DataManager.fetchAllItems(databaseHelper))
        }
    }
}