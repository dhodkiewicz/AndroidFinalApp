package com.example.itemtracker

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class AddItemActivity : Activity() {

    private val myCalendar = Calendar.getInstance()
    private lateinit var databaseHelper: DBHelper


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        databaseHelper = DBHelper(this)

        // on clicking ok on the calender dialog

        val date = LocalDateTime.now().toLocalDate().toString()
        etDate.setText(date)


        bSave.setOnClickListener {
            saveItem()
        }

        bCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveItem() {
        var isValid = true

       etEntry.error = if(etEntry?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        var invalidRating = ratingBar.numStars == 0



        if(isValid && !invalidRating){
            val entry = etEntry?.text.toString()
            val date = etDate.text
            val user = "12345"
            val mood = ratingBar.rating
            val loc = Location("dummyprovider")
            loc.setLatitude(20.3);
            loc.setLongitude(52.6);

            val jsonString = Gson().toJson(loc)

            val db = databaseHelper.writableDatabase

            val values = ContentValues()
            values.put(EntryDBContract.iEntry.ENTRY,entry)
            values.put(EntryDBContract.iEntry.DATE,date.toString())
            values.put(EntryDBContract.iEntry.USERID, user)
            values.put(EntryDBContract.iEntry.MOOD, mood)
            values.put(EntryDBContract.iEntry.LOC, jsonString)



           val result = db.insert(EntryDBContract.iEntry.TABLE_NAME, null, values)

            setResult(RESULT_OK, Intent())

            Toast.makeText(applicationContext, "Journal Entry Added", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun setUpCalender(date: DatePickerDialog.OnDateSetListener) {

        DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getFormattedDate(dobInMilis: Long?): String {

        return dobInMilis?.let {
            val sdf = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
            sdf.format(dobInMilis)
        } ?: "Not Found"
    }


}
